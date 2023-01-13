package com.ebrem.androidproject;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    Button button;
    String str_url = "https://www.upcdatabase.com/item/";
    TextView desc;
    WebView myWebView,myWebView1;
    String strResult = "";
    private String myUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.btn_scan);
        desc = findViewById(R.id.description);
        myWebView = findViewById(R.id.web_view);
        myWebView1 = findViewById(R.id.web_view1);
        button.setOnClickListener(v->{
            scanCode();
        });
    }

    private void scanCode(){
        ScanOptions options = new ScanOptions();
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }


    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result->{
       if(result.getContents() != null){
           AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
           String strResult = result.getContents();
           String myUrl = str_url + strResult;
           AsyncHttpClient client = new AsyncHttpClient();
           client.get(myUrl, new AsyncHttpResponseHandler() {
               @Override
               public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                   if (responseBody != null) {
                       assert desc != null;
                       try
                       {
                           Document doc = Jsoup.parse(new String(responseBody));
                           String result = String.valueOf(doc.select("tbody tr:nth-of-type(2) td:nth-of-type(3)"));
                           if (result == ""){
                               desc.setText("Item not available in database");
                           } else {
                               result = result.replace("<td>", "");
                               result = result.replace("</td>", "");
                               desc.setText(result);
                               result = result.replace(" ", "+");
                               String newURL = "https://www.akakce.com/arama/?q=" + result;
                               result = result.replace("+", "%20");
                               String newURL1 = "https://www.cimri.com/arama?q=" + result;
                               myWebView.loadUrl(newURL);
                               myWebView1.loadUrl(newURL1);
                           }
                       }
                       catch (Exception e)
                       {
                           e.printStackTrace();
                       }
                   }
               }
               @Override
               public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

               }
           });


       }
    });

}

