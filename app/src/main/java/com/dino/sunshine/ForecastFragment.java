package com.dino.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ForecastFragment extends Fragment implements GetOpenWeatherDataInterface {

    private final String LOG_TAG = ForecastFragment.class.getSimpleName();
    private ArrayAdapter<String> mWeatherForecastArrayAdapter;
    private List<DayWeatherForecast> forecasts;

    public ForecastFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchWeatherData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mWeatherForecastArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, new ArrayList<String>());

        ListView listView = (ListView)rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mWeatherForecastArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedForecast = mWeatherForecastArrayAdapter.getItem(position);

                //Toast.makeText(getActivity(), selectedForecast, Toast.LENGTH_SHORT).show();

                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                detailIntent.putExtra(Intent.EXTRA_TEXT, selectedForecast);
                startActivity(detailIntent);

            }
        });

        fetchWeatherData();

        return rootView;
    }

    private void fetchWeatherData() {

        // get location from pref
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.preference_key_location), getString(R.string.preference_defaultValue_location));

        // get temp unit from pref
        String tempUnit = prefs.getString(getString(R.string.preference_key_temperature), getString(R.string.preference_temp_unit_metric));


        GetOpenWeatherData getOpenWeatherData = new GetOpenWeatherData(location, tempUnit, 7);
        getOpenWeatherData.delegate = this;
        getOpenWeatherData.execute();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.action_refresh) {
            fetchWeatherData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void weatherDataDownloadComplete(List<DayWeatherForecast> forecasts) {
        mWeatherForecastArrayAdapter.clear();

        this.forecasts = forecasts;
        for(DayWeatherForecast dayWeatherForecast: forecasts) {
            String weatherDesc = dayWeatherForecast.toString();
            mWeatherForecastArrayAdapter.add(weatherDesc);
        }
    }
}
