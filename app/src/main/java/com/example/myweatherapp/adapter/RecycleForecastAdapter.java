package com.example.myweatherapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myweatherapp.R;
import com.example.myweatherapp.getMainSettings.Weather;

import java.util.ArrayList;

import static com.example.myweatherapp.getMainSettings.MainSettings.convertDate;
import static com.example.myweatherapp.getMainSettings.MainSettings.equalsTemp;
import static com.example.myweatherapp.getMainSettings.MainSettings.setIconToList;

public class RecycleForecastAdapter extends RecyclerView.Adapter<WeatherViewHolder> {

    ArrayList<Weather> itemList = new ArrayList<>();

    public RecycleForecastAdapter(ArrayList<Weather> itemList) {
        this.itemList = itemList;
    }

    public void swap(ArrayList<Weather> datas){
        itemList.clear();
        itemList.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_cardview, null);

        WeatherViewHolder viewHolder = new WeatherViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {
        holder.itemDate.setText(convertDate(itemList.get(position).getDateTimestamp()));
        holder.itemIcon.setImageResource(setIconToList(itemList.get(position).getIcon()));
        holder.itemTemperature.setText(equalsTemp(itemList.get(position).getMin_temperature(),
                itemList.get(position).getMax_temperature()));
        holder.itemDescription.setText(itemList.get(position).getDescription());
        holder.itemWind.setText(String.format("Wind: %.1f m/s", Double.valueOf(itemList.get(position).getWind())));
        holder.itemPressure.setText(String.format("Pressure: %.1f hPa", Double.valueOf(itemList.get(position).getPressure())));
        holder.itemHumidity.setText(String.format("Humidity: %.1f ", Double.valueOf(itemList.get(position).getHumidity()))+"%");
    }

    @Override
    public int getItemCount() {
        return (null != itemList ? itemList.size() : 0);
    }


}
