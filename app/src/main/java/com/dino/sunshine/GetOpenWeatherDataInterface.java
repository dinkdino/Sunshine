package com.dino.sunshine;

import java.util.List;

public interface GetOpenWeatherDataInterface {
    public void weatherDataDownloadComplete(List<DayWeatherForecast> forecasts);
}
