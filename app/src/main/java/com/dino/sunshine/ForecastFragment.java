package com.dino.sunshine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ForecastFragment extends Fragment {

    public ForecastFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_item_forecast, container, false);


        GetOpenWeatherData getOpenWeatherData = new GetOpenWeatherData("Mumbai", 7);
        getOpenWeatherData.execute();

        return rootView;
    }
}
