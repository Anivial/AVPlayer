package fr.enssat.huang_tran.avplayer;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    VideoView videoView;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initVideoView();
        //initWebView();
    }

    private void initVideoView() {
        videoView = findViewById(R.id.videoView);
        Uri uri = Uri.parse("https://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_320x180.mp4");
        videoView.setVideoURI(uri);
        videoView.start();
    }

    private void initWebView(){
        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("https://en.wikipedia.org/wiki/Big_Buck_Bunny");
    }
}
