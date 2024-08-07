package com.example.weatherforcast;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView weatherRV;
    private RelativeLayout homeRL;
    private TextView cityTv, temperatureTv, conditionTv;
    private TextInputEditText searchEdt;
    private ProgressBar loadingPB;
    private ImageView backIv, iconIv, SearchIv;
    private ArrayList<ModelClassRV> modelClassRVArrayList;
    private AdapterRV adapterRV;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS); // To set the status bar to transparent
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View decorView = getWindow().getDecorView();
        WindowInsetsControllerCompat windowInsetsController = ViewCompat.getWindowInsetsController(decorView);
        if (windowInsetsController != null) {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
            windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
        initViews();
        adapterRV = new AdapterRV(this, modelClassRVArrayList);
        weatherRV.setAdapter(adapterRV);
        getWeatherInfo("Bengaluru");

        SearchIv.setOnClickListener(v -> performSearch());

        searchEdt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                if (event == null || !event.isShiftPressed()) {
                    performSearch();
                    hideKeyboard(); // Hide keyboard after search
                    return true; // Consume the event
                }
            }
            return false; // Pass the event to other listeners
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEdt.getWindowToken(), 0);
    }

    private void hideProgressBar() {
        loadingPB.setVisibility(View.GONE);
        homeRL.setVisibility(View.VISIBLE);
    }
    private void showProgressBar() {
        loadingPB.setVisibility(View.VISIBLE);
        homeRL.setVisibility(View.GONE);
    }

    private void performSearch() {
        showProgressBar();

        String city = searchEdt.getText().toString();
        if (city.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please enter city name", Toast.LENGTH_SHORT).show();
        } else {
            getWeatherInfo(city);
        }
    }

    public void getWeatherInfo(String cityName) {
        String url = "https://api.weatherapi.com/v1/forecast.json?key=" + BuildConfig.API_KEY + "&q=" + cityName + "&days=1&aqi=no&alerts=no";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                modelClassRVArrayList.clear();
                String data = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    String temperature = jsonObject.getJSONObject("current").getString("temp_c") + "°C";
                    String city = jsonObject.getJSONObject("location").getString("name");
                    int isDay = jsonObject.getJSONObject("current").getInt("is_day");
                    String condition = jsonObject.getJSONObject("current").getJSONObject("condition").getString("text");
                    String icon = jsonObject.getJSONObject("current").getJSONObject("condition").getString("icon");
                    JSONObject forecastObj = jsonObject.getJSONObject("forecast");
                    JSONObject forecastArray = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastArray.getJSONArray("hour");

                    for (int i = 0; i < hourArray.length(); i++) {
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String windSpeed = hourObj.getString("wind_kph");
                        String iconURL = hourObj.getJSONObject("condition").getString("icon");
                        String temp = hourObj.getString("temp_c");
                        ModelClassRV modelClassRV = new ModelClassRV(temp, time, iconURL, windSpeed, isDay);
                        modelClassRVArrayList.add(modelClassRV);
                    }
                    runOnUiThread(() -> {
                        hideProgressBar();
                        temperatureTv.setText(temperature);
                        cityTv.setText(city);
                        Picasso.get().load("https:".concat(icon)).into(iconIv);
                        conditionTv.setText(condition);
                        if (isDay == 1) {
                            backIv.setImageResource(R.drawable.day);
                        } else {
                            backIv.setImageResource(R.drawable.night);
                        }
                        adapterRV.notifyDataSetChanged();
                    });

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Log.e("Error", "onFailure: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "Enter valid city name...", Toast.LENGTH_SHORT).show();
                    hideProgressBar();
                });
            }
        });
    }

    public void initViews() {
        weatherRV = findViewById(R.id.RVWeather);
        homeRL = findViewById(R.id.RLHome);
        cityTv = findViewById(R.id.TVCityName);
        temperatureTv = findViewById(R.id.TVTemperature);
        conditionTv = findViewById(R.id.TVCondition);
        searchEdt = findViewById(R.id.EdtCity);
        loadingPB = findViewById(R.id.PD);
        backIv = findViewById(R.id.IVBack);
        iconIv = findViewById(R.id.IVIcon);
        SearchIv = findViewById(R.id.IVSearch);

        modelClassRVArrayList = new ArrayList<>();
    }
}
