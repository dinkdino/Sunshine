package com.dino.sunshine;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

enum WeatherDataFormat { JSON, XML }

public class GetOpenWeatherData extends GetRawData {

    private final String LOG_TAG = GetOpenWeatherData.class.getSimpleName();
    private WeatherDataFormat mFormat = WeatherDataFormat.JSON;
    private String mUnits = "metric";
    private String mNumberOfDays;
    private final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
    private String mFinalUri;
    public GetOpenWeatherDataInterface delegate;

    private List<DayWeatherForecast> mForecasts;

    public void setFormat(WeatherDataFormat format) {
        mFormat = format;
    }

    public void setUnits(String units) {
        mUnits = units;
    }

    public List<DayWeatherForecast> getForecasts() {
        return mForecasts;
    }

    public GetOpenWeatherData(String searchCriteria, int numberOfDays) {
        super(null);
        mNumberOfDays = numberOfDays > 0 ? Integer.toString(numberOfDays) : "7";
        mForecasts = new ArrayList<DayWeatherForecast>();

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

            for(int i = 0; i < daysList.length(); i++) {
                JSONObject dayForecast = daysList.getJSONObject(i);

                // Date
                long timeStamp = dayForecast.getLong("dt");
                Date date = new Date(timeStamp * 1000);

                JSONObject weather = dayForecast.getJSONArray("weather").getJSONObject(0);
                String desc = weather.getString("main");

                JSONObject temp = dayForecast.getJSONObject("temp");
                double min = temp.getDouble("min");
                double max = temp.getDouble("max");

                DayWeatherForecast dayWeatherForecast = new DayWeatherForecast(date, desc, min, max);

                mForecasts.add(dayWeatherForecast);
            }

            if(delegate != null) {
                delegate.weatherDataDownloadComplete(mForecasts);
            }

        } catch(JSONException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Failed to parse json data");
        }
    }

    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date).toString();
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DATETIME = "dt";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        String[] resultStrs = new String[numDays];
        for(int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            long dateTime = dayForecast.getLong(OWM_DATETIME);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather", which is 1 element long.
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            // Temperatures are in a child object called "temp".  Try not to name variables
            // "temp" when working with temperature.  It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }

        return resultStrs;
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
