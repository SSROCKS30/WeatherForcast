package com.example.weatherforcast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdapterRV extends RecyclerView.Adapter<AdapterRV.ViewHolder>{
    private Context context;
    private ArrayList<ModelClassRV> modelClassRVList;

    public AdapterRV(Context context, ArrayList<ModelClassRV> modelClassRVList) {
        this.context = context;
        this.modelClassRVList = modelClassRVList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelClassRV modelClassRV = modelClassRVList.get(position);
        holder.RLCard.setBackgroundResource(modelClassRV.getIsDay() == 1 ? R.drawable.custom_card_day : R.drawable.custom_card_back);
        holder.temperatureTV.setText(modelClassRV.getTemperature() + "°C");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        {
            try {
                holder.timeTV.setText(output.format(input.parse(modelClassRV.getTime())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        holder.windSpeedTV.setText(modelClassRV.getWindSpeed() + " km/h");
        Picasso.get().load("https:".concat(modelClassRV.getIconURL())).into(holder.AiconIV);
    }
    @Override
    public int getItemCount() {
        return modelClassRVList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView temperatureTV, timeTV, windSpeedTV;
        private ImageView AiconIV;
        private RelativeLayout RLCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            temperatureTV = itemView.findViewById(R.id.TVTemp);
            timeTV = itemView.findViewById(R.id.TVTime);
            windSpeedTV = itemView.findViewById(R.id.TVSpeed);
            AiconIV = itemView.findViewById(R.id.IVIcon);
            RLCard = itemView.findViewById(R.id.RLCard);

        }
    }
}
