package com.polygalov.okhttpsample;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainOkHttp extends AppCompatActivity {

    private TextView mInfoTextView;
    private ProgressBar progressBar;
    private EditText editText;
    OkHttpClient client;
    HttpUrl.Builder urlBuilder;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.edit_text);
        mInfoTextView = findViewById(R.id.tv_Load);
        progressBar = findViewById(R.id.progress_bar);

        Button btnLoad = findViewById(R.id.btn_load);
        client = new OkHttpClient();
        btnLoad.setOnClickListener(v -> onClick());
    }

    private void onClick() {
        urlBuilder = HttpUrl.parse("https://api.github.com/users").newBuilder();
        if (!editText.getText().toString().isEmpty()) {
            urlBuilder.addQueryParameter("login", editText.getText().toString());
        }
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadPageTask().execute(request);
        } else {
            Toast.makeText(this, "Подключите интернет", Toast.LENGTH_SHORT);
        }

    }

    private class DownloadPageTask extends AsyncTask<Request, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mInfoTextView.setText("");
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Request... requests) {
            try {
                return downloadOneUrl(requests[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            mInfoTextView.setText(result);
            progressBar.setVisibility(View.GONE);
            super.onPostExecute(result);
        }

        private String downloadOneUrl(Request request) throws IOException {
            String data = "";
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }else{
                    Headers responseHeaders = response.headers();
                    for (int i = 0; i < responseHeaders.size(); i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                    data = response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }
    }
}
