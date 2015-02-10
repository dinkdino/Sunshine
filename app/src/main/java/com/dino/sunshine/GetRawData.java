package com.dino.sunshine;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

enum DownloadStatus { IDLE, PROCESSING, INVALID_URL, EMPTY_OR_INVALID, OK }

public class GetRawData {

    private final String LOG_TAG = GetRawData.class.getSimpleName();
    protected String mRawUrl;
    protected String mRawData;
    protected String mMethodType;
    protected DownloadStatus mDownloadStatus;


    public String getRawData() {
        return mRawData;
    }

    public void setRawUrl(String rawUrl) {
        mRawUrl = rawUrl;
    }

    public DownloadStatus getDownloadStatus() {
        return mDownloadStatus;
    }

    public void setMethodType(String methodType) {
        mMethodType = methodType;
    }

    public GetRawData(String mRawUrl) {
        this.mRawUrl = mRawUrl;
        this.mDownloadStatus = DownloadStatus.IDLE;
        this.mMethodType = "GET";
    }

    public void execute() {
        mDownloadStatus = DownloadStatus.PROCESSING;
        DownloadRawData downloadRawData = new DownloadRawData();
        downloadRawData.execute(mRawUrl);
    }

    // Async Task
    public class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;

            try {

                // Get Url
                URL url = new URL(params[0]);

                // Create connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(mMethodType);
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuffer stringBuffer = new StringBuffer();

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line + '\n');
                }

                return stringBuffer.toString();

            } catch (Exception e) {
                mDownloadStatus = DownloadStatus.IDLE;
                Log.e(LOG_TAG, "Error", e);
                return null;
            } finally {
                mDownloadStatus = DownloadStatus.IDLE;
                if(urlConnection != null)  {
                    urlConnection.disconnect();
                }

                if(bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch(final IOException e) {
                        Log.e(LOG_TAG, "Failed to close stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            mRawData = s;

            if(mRawData == null) {
                if(mRawUrl == null) {
                    mDownloadStatus = DownloadStatus.INVALID_URL;
                } else {
                    mDownloadStatus = DownloadStatus.EMPTY_OR_INVALID;
                }
            } else {
                mDownloadStatus = DownloadStatus.OK;
            }
        }
    }
}
