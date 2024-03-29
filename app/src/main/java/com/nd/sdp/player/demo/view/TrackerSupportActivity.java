package com.nd.sdp.player.demo.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.nd.android.bk.video.tracker.IViewTracker;
import com.nd.android.bk.video.tracker.Tracker;
import com.nd.sdp.bk.video.R;
import com.nd.sdp.player.demo.Backable;


/**
 * @author JiaoYun
 * @date 2019/10/17 20:27
 */
public class TrackerSupportActivity extends AppCompatActivity {

    public static void start(Context context){
        context.startActivity(new Intent(context, TrackerSupportActivity.class));
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contain);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.root, new TrackerSupportFragment(), "TrackerSupportFragment")
                .commit();
    }

    public void addDetailFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.root, new DetailFragment(), "DetailFragment")
                .addToBackStack("DetailFragment")
                .commitAllowingStateLoss();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Tracker.onConfigurationChanged(this, newConfig);
    }

    @Override
    public void onBackPressed() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                && getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
            // Get the current fragment using the method from the second step above...
            Fragment currentFragment = getCurrentFragment();

            // Determine whether or not this fragment implements Backable
            // Do a null check just to be safe
            if (currentFragment != null && currentFragment instanceof Backable) {

                if (((Backable) currentFragment).onBackPressed()) {
                    // If the onBackPressed override in your fragment
                    // did absorb the back event (returned true), return
                    return;
                } else {
                    // Otherwise, call the super method for the default behavior
                    super.onBackPressed();
                }
            }

            // Any other logic needed...
            // call super method to be sure the back button does its thing...
            super.onBackPressed();
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            String lastFragmentName = fragmentManager.getBackStackEntryAt(
                    fragmentManager.getBackStackEntryCount() - 1).getName();
            return fragmentManager.findFragmentByTag(lastFragmentName);
        }
        return null;
    }


}
