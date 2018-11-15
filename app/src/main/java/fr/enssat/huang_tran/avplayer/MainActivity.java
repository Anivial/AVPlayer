package fr.enssat.huang_tran.avplayer;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class MainActivity extends AppCompatActivity {

    VideoView videoView;
    WebView webView;
    LinearLayout chapters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initVideoView();
        initWebView();
        initChapters();
    }

    private void initVideoView() {
        videoView = findViewById(R.id.videoView);
        Uri uri = Uri.parse("https://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_320x180.mp4");
        videoView.setVideoURI(uri);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.start();

    }

    private void initWebView(){
        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setOverScrollMode(webView.OVER_SCROLL_NEVER);
        webView.loadUrl("https://en.wikipedia.org/wiki/Big_Buck_Bunny");
    }

    private void initChapters(){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        chapters = findViewById(R.id.chapters);
        try {
            JSONObject reader = new JSONObject(getJson());
            final JSONArray array = reader.getJSONArray("chapters");
            for (int i = 0 ; i < array.length(); i++){
                Button myButton = new Button(this);
                myButton.setText(array.getJSONObject(i).getString("name"));
                myButton.setLayoutParams(layoutParams);
                final int time = array.getJSONObject(i).getInt("time")*1000;
                myButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        videoView.seekTo(time);
                    }
                });
                chapters.addView(myButton);
            }
        } catch (Exception e) {

        }

    }

    private String getJson(){
        InputStream is = getResources().openRawResource(R.raw.metadata);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            is.close();
        } catch (Exception e){

        }
        return writer.toString();
    }
}
