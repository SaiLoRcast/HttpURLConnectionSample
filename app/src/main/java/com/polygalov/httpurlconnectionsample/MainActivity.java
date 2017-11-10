package com.polygalov.httpurlconnectionsample;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private TextView mInfoTextView;
    private ProgressBar progressBar;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.edit_text);
        mInfoTextView = findViewById(R.id.tv_Load);
        progressBar = findViewById(R.id.progress_bar);

        Button btnLoad = findViewById(R.id.btn_load);
        btnLoad.setOnClickListener(v -> onClick());
    }

    private void onClick() {
        String bestUrl = "https://api.github.com/users";
        if (!editText.getText().toString().isEmpty()) {
            bestUrl += "/" + editText.getText();
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadPageTask().execute(bestUrl);
        } else {
            Toast.makeText(this, "Подключите интернет", Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadPageTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mInfoTextView.setText("");
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadOneUrl(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "error";
        }

        @Override
        protected void onPostExecute(String result) {
            mInfoTextView.setText(result);
            progressBar.setVisibility(View.GONE);
            super.onPostExecute(result);
        }

        private String downloadOneUrl(String address) throws IOException {
            InputStream inputStream = null;
            String data = "";

            try {
                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(100000);
                connection.setConnectTimeout(100000);
//                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(true);
                connection.setUseCaches(false);
                connection.setDoInput(true);
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("Метод запроса: " + connection.getRequestMethod());
                    System.out.println("Ответное сообщение: " + connection.getResponseMessage());

                    Map<String, List<String>> myMap = connection.getHeaderFields();

                    Set<String> myField = myMap.keySet();
                    System.out.println("\nДалее следует заголовок: ");
                    for (String k : myField) {
                        System.out.println("Ключ: " + k + " Значение: " + myMap.get(k));
                    }
                    inputStream = connection.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int read = 0;
                    while ((read = inputStream.read()) != -1) {
                        baos.write(read);
                    }
                    byte[] result = baos.toByteArray();
                    baos.close();
                    data = new String(result);
                } else {
                    data = connection.getResponseMessage() + " .Error Code: " + responseCode;
                }
                connection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return data;
        }
    }
}
