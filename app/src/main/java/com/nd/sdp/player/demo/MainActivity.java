package com.nd.sdp.player.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.nd.sdp.bk.video.R;
import com.nd.sdp.player.demo.view.PagerSupportActivity;
import com.nd.sdp.player.demo.view.TrackerSupportActivity;
import com.nd.sdp.player.demo.view.TrackerSupportFragment;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }





    public void onVideoSupport(View view) {
        PagerSupportActivity.start(this);
    }

    public void onTrackerSupport(View view){
        TrackerSupportActivity.start(this);
    }


}
