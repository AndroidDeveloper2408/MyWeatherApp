package com.example.myweatherapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myweatherapp.InfoActivity;
import com.example.myweatherapp.R;

public class WeatherViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView itemDate;
    public TextView itemTemperature;
    public TextView itemDescription;
    public TextView itemWind;
    public TextView itemPressure;
    public TextView itemHumidity;
    public ImageView itemIcon;
    public CardView cardview;


    public WeatherViewHolder(View view) {
        super(view);
        this.itemDate = (TextView) view.findViewById(R.id.itemDate);
        this.itemTemperature = (TextView) view.findViewById(R.id.itemTemperature);
        this.itemDescription = (TextView) view.findViewById(R.id.itemDescription);
        this.itemWind = (TextView) view.findViewById(R.id.itemWind);
        this.itemPressure = (TextView) view.findViewById(R.id.itemPressure);
        this.itemHumidity = (TextView) view.findViewById(R.id.itemHumidity);
        this.itemIcon = (ImageView) view.findViewById(R.id.itemIcon);
        this.cardview = (CardView)view.findViewById(R.id.cardView);

        cardview.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        Intent intent = new Intent(context, InfoActivity.class);
        intent.putExtra("intent", getAdapterPosition());
        context.startActivity(intent);
    }
}