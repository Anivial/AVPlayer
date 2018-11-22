package fr.enssat.huang_tran.avplayer;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private VideoViewModel videoViewModel;

    VideoView videoView;
    WebView webView;
    LinearLayout chapters;
    List<Button> buttons;
    Thread thread;
    JSONArray jsonChapters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        parseJson();
        initVideoView();
        initWebView();
        initChapters();
        initThread();

        videoViewModel = ViewModelProviders.of(this).get(VideoViewModel.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.seekTo(videoViewModel.getPosition().getValue());
        videoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoViewModel.setPosition(videoView.getCurrentPosition());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.interrupt();

    }

    private void parseJson(){
        try {
            JSONObject reader = new JSONObject(getJson());
            jsonChapters = reader.getJSONArray("chapters");
        } catch (Exception e){

        }
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
        buttons = new ArrayList<Button>();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        chapters = findViewById(R.id.chapters);
        try {
            for (int i = 0 ; i < jsonChapters.length(); i++){
                Button myButton = new Button(this);
                myButton.setText(jsonChapters.getJSONObject(i).getString("name"));
                myButton.setLayoutParams(layoutParams);
                final int time = jsonChapters.getJSONObject(i).getInt("time")*1000;
                myButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        videoView.seekTo(time);
                    }
                });
                chapters.addView(myButton);
                buttons.add(myButton);
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

    private void initThread(){
        thread = new Thread(new Runnable() {
            int pre = -1;
            public void run() {
                do{
                    int current = videoView.getCurrentPosition();
                    updateChapters(current);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while(true);
            }

            private void updateChapters(int current) {
                int button = jsonChapters.length()-1;
                int i = 0;
                boolean found = false;
                try {
                    while (!found && i<jsonChapters.length()-1){
                        if (current < jsonChapters.getJSONObject(i+1).getInt("time")*1000 && current >= jsonChapters.getJSONObject(i).getInt("time")*1000) {
                            button = i;
                            found = true;
                        }
                        i++;
                    }
                    for(int j = 0; j<jsonChapters.length();j++){
                        if (j == button){
                            buttons.get(j).setTextColor(Color.BLUE);
                        } else {
                            buttons.get(j).setTextColor(Color.BLACK);
                        }
                    }
                    if(!(pre==button)){
                        System.out.println("pre : " + pre);
                        System.out.println("number : " + button);
                        final int temp = button;
                        webView.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    webView.loadUrl(jsonChapters.getJSONObject(temp).getString("url"));
                                } catch (Exception e){

                                }
                            }
                        });
                        pre = button;
                    }
                } catch (Exception e){
                }
            }

        });
        thread.start();
    }

}
