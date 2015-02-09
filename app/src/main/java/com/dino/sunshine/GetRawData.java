package com.dino.sunshine;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

enum DownloadStatus { IDLE, PROCESSING, INVALID_URL, EMPTY_OR_INVALID_DATA, OK }

public class GetRawData extends AsyncTask<String, Void, String> {

    private final String LOG_TAG = GetRawData.class.getSimpleName();
    private String mRawUrl;
    private String mRawData;
    private DownloadStatus mDownloadStatus;

    public GetRawData(String rawUrl) {
        mRawUrl = rawUrl;
    }

    public String getRawData() {
        return mRawData;
    }

    public DownloadStatus getDownloadStatus() {
        return mDownloadStatus;
    }


    @Override
    protected String doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;

        try {
            URL url = new URL(params[0]);

            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if(inputStream == null) {
                return null;
            }

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer stringBuffer = new StringBuffer();

            String line;
            while((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + '\n');
            }

            return stringBuffer.toString();


        } catch (Exception e) {
            Log.e(LOG_TAG, "Error", e);
            return null;
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }

            if(bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Failed to close input stream", e);
                }
            }
        }
    }

    @Override
    protected void onPostExecute(String s) {

        Log.v(LOG_TAG, "Returned Data: " + s);

        if(s == null) {
            if(mRawUrl == null) {
                mDownloadStatus = DownloadStatus.INVALID_URL;
            } else {
                mDownloadStatus = DownloadStatus.EMPTY_OR_INVALID_DATA;
            }
        } else {
            mDownloadStatus = DownloadStatus.OK;
        }
    }
}
