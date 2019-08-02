package com.example.mywebbrowser;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private EditText url;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.browser);
        url = findViewById(R.id.url);
        Button ok = findViewById(R.id.buttonOK);
        ok.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                String strUrl = url.getText().toString();
                final URL uri = new URL(strUrl);
                final Handler handler = new Handler();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpsURLConnection urlConnection;
                            urlConnection = (HttpsURLConnection) uri.openConnection();
                            urlConnection.setRequestMethod("GET");
                            urlConnection.setReadTimeout(10000);

                            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            StringBuffer preResult = new StringBuffer();

                            preResult.append(in.readLine());

                            while(in.ready())
                                preResult.append(in.readLine());

                            final String result = preResult.toString();

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    webView.loadData(result, "text/html; charset=utf-8", "utf-8");
                                }
                            });
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    };
}
