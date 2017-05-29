

package com.radioyps.webviewweather;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final String url_weather = "http://weather.gc.ca/wxlink/wxlink.html?cityCode=qc-147&amp;lang=e";
    private WebView mWebView = null;
    private ScrollView mScrollView = null;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.activity_main);

        final String mimeType = "text/html";



        mWebView = (WebView) findViewById(R.id.wv1);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        mWebView.loadData("<a href='x'>Hello World! - 1</a>", mimeType, null);

        //without this toast message, screenshot will be blank, dont ask me why...
        Toast.makeText(this, "Taking screenshot...", Toast.LENGTH_SHORT).show();


        // This is the important code :)
        mWebView.setDrawingCacheEnabled(true);

        //width x height of your webview and the resulting screenshot
        mWebView.measure(300, 200);
        mWebView.layout(0, 0, 300, 200);
//        setDrawingCache(mScrollView);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //without this method, your app may crash...
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //new takeScreenshotTask().execute();

                takeScreeshot(view);
                //stopSelf();


            }
        });

        mWebView.loadUrl(url_weather);

    }

    private void setDrawingCache(View view){
        view.setDrawingCacheEnabled(true);

// this is the important code :)
// Without it the view will have a dimension of 0,0 and the bitmap will be null
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.buildDrawingCache(true);

    }

    private void takeScreeshot(WebView view){
        synchronized (this) {try {wait(5350);} catch (InterruptedException e) {}}

        //here I save the bitmap to file
        view.setDrawingCacheEnabled(true);
        Bitmap b = view.getDrawingCache(true);
//        Bitmap b = mScrollView.getDrawingCache();
        if(b == null){
            Toast.makeText(this, "Error on Taking Screenshot, return", Toast.LENGTH_SHORT).show();
            Log.e("Mainactivity", "error on taking screenshot...");
            return;
        }

        //File file = new File("example-screenshot.png");
        File file = new File(this.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), "example-screenshot.png");
        OutputStream out;


        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            b.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();

        } catch (IOException e) {
            Log.e("ScreenshotService", "IOException while trying to save thumbnail, Is /sdcard/ writable?");

            e.printStackTrace();
        }

        Toast.makeText(this, "Screenshot taken", Toast.LENGTH_SHORT).show();
    }

}
