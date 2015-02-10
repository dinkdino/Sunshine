package com.dino.sunshine;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

enum WeatherDataFormat { JSON, XML }

public class GetOpenWeatherData extends GetRawData {

    private final String LOG_TAG = GetOpenWeatherData.class.getSimpleName();
    private WeatherDataFormat mFormat = WeatherDataFormat.JSON;
    private String mUnits = "metric";
    private String mNumberOfDays;
    private final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
    private String mFinalUri;

    public void setFormat(WeatherDataFormat format) {
        mFormat = format;
    }

    public void setUnits(String units) {
        mUnits = units;
    }

    public GetOpenWeatherData(String searchCriteria, int numberOfDays) {
        super(null);
        mNumberOfDays = numberOfDays > 0 ? Integer.toString(numberOfDays) : "7";

        createRequestUri(searchCriteria);
    }

    public void execute() {
        setRawUrl(mFinalUri);
        DownloadOpenWeatherData downloadOpenWeatherData = new DownloadOpenWeatherData();
        downloadOpenWeatherData.execute(mFinalUri);
    }

    private boolean createRequestUri(String searchCriteria) {

        mFinalUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("q", searchCriteria)
                .appendQueryParameter("mode", mFormat == WeatherDataFormat.JSON ? "json" : "xml")
                .appendQueryParameter("units", mUnits)
                .appendQueryParameter("cnt", mNumberOfDays).toString();

        return mFinalUri != null;
    }

    private void parseJsonData() {
        if(mDownloadStatus != DownloadStatus.OK) {
            Log.e(LOG_TAG, "Failed to download data");
            return;
        }

        // Parse json
        try {
            JSONObject jsonObject = new JSONObject(getRawData());
            JSONArray daysList = jsonObject.getJSONArray("list");

            Log.v(LOG_TAG, "WeatherData: " + daysList.toString());
        } catch(JSONException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Failed to parse json data");
        }
    }

    public class DownloadOpenWeatherData extends DownloadRawData {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(mFormat == WeatherDataFormat.JSON) {
                parseJsonData();
            } else {
                //parseXmlData();
            }
        }
    }

}
