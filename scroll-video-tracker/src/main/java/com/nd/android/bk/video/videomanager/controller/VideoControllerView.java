package com.nd.android.bk.video.videomanager.controller;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nd.android.bk.video.R;
import com.nd.android.bk.video.tracker.IViewTracker;
import com.nd.android.bk.video.tracker.Tracker;
import com.nd.android.bk.video.utils.Utils;
import com.nd.android.bk.video.videomanager.interfaces.PlayerItemChangeListener;

import java.lang.ref.WeakReference;
import java.util.Formatter;
import java.util.Locale;

/**
 * @author JiaoYun
 * @date 2019/10/14 22:22
 */
public class VideoControllerView extends FrameLayout implements VideoGestureListener, PlayerItemChangeListener {
    private static final String TAG = "VideoControllerView";

    private static final int HANDLER_ANIMATE_OUT = 1;
    private static final int HANDLER_UPDATE_PROGRESS = 2;
    private static final long PROGRESS_SEEK = 500;
    private static final long ANIMATE_TIME = 300;
    private static final long AUTO_HIDE_TIME = 3000;

    private View mRootView;
    private SeekBar mSeekBar;
    private TextView mEndTime, mCurrentTime;
    private boolean mIsShowing;
    private boolean mIsDragging;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private GestureDetector mGestureDetector;

    private Activity mContext;
    private boolean mCanSeekVideo;
    private boolean mCanControlVolume;
    private boolean mCanControlSpeed;
    private boolean mCanControlBrightness;
    private String mVideoTitle;
    private MediaPlayerControlListener mMediaPlayerControlListener;
    private ViewGroup mAnchorView;
    private View mVideoView;

    @DrawableRes
    private int mExitIcon;
    @DrawableRes
    private int mPauseIcon;
    @DrawableRes
    private int mPlayIcon;
    @DrawableRes
    private int mShrinkIcon;
    @DrawableRes
    private int mStretchIcon;


    private View mTopLayout;
    private ImageButton mBackButton;
    private TextView mTitleText;


    private View mCenterLayout;
    private ImageView mCenterImage;
    private ProgressBar mCenterProgress;
    private float mCurBrightness = -1;
    private int mCurVolume = -1;
    private AudioManager mAudioManager;
    private int mMaxVolume;
    private int duration;

    private View mBottomLayout;
    private ImageButton mPauseButton;
    private ImageButton mFullscreenButton;
    private TextView mTvPalyRate;
    private float mCurrentSpeed = 1.0f;
    private float mlastSpeed = 1.0f;
    private String mCurrentSpeedDes = "1x";
    private Handler mHandler = new ControllerViewHandler(this);

    public VideoControllerView(Builder builder) {
        super(builder.context);
        this.mContext = builder.context;
        this.mMediaPlayerControlListener = builder.mediaPlayerControlListener;

        this.mVideoTitle = builder.videoTitle;
        this.mCanSeekVideo = builder.canSeekVideo;
        this.mCanControlVolume = builder.canControlVolume;
        this.mCanControlBrightness = builder.canControlBrightness;
        this.mExitIcon = builder.exitIcon;
        this.mPauseIcon = builder.pauseIcon;
        this.mPlayIcon = builder.playIcon;
        this.mStretchIcon = builder.stretchIcon;
        this.mShrinkIcon = builder.shrinkIcon;
        this.mVideoView = builder.videoView;
        this.mCanControlSpeed = builder.canControlSpeed;
        setAnchorView(builder.anchorView);
        this.mVideoView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                toggleControllerView();
                return mMediaPlayerControlListener.isFullScreen();
            }
        });

        Tracker.addPlayerItemChangeListener(this);
    }

    @Override
    public void onPlayerItemChanged(IViewTracker viewTracker) {
        mlastSpeed = mCurrentSpeed = 1.0f;
        if(mMediaPlayerControlListener.isPlaying()){
            mMediaPlayerControlListener.setSpeed(mCurrentSpeed);
        }
        mTvPalyRate.setText("1x");
        mPauseButton.setImageResource(mPauseIcon);
        mHandler.postDelayed(mHideRunnable,AUTO_HIDE_TIME);
    }

    public static class Builder {

        private Activity context;
        private boolean canSeekVideo = true;
        private boolean canControlVolume = true;
        private boolean canControlBrightness = true;
        private boolean canControlSpeed = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M;
        private String videoTitle = "";
        private MediaPlayerControlListener mediaPlayerControlListener;
        private ViewGroup anchorView;
        private View videoView;
        @DrawableRes
        private int exitIcon = R.drawable.video_top_back;
        @DrawableRes
        private int pauseIcon = R.drawable.ic_media_pause;
        @DrawableRes
        private int playIcon = R.drawable.ic_media_play;
        @DrawableRes
        private int shrinkIcon = R.drawable.ic_media_fullscreen_shrink;
        @DrawableRes
        private int stretchIcon = R.drawable.ic_media_fullscreen_stretch;

        public Builder(@Nullable Activity context, @Nullable MediaPlayerControlListener mediaControlListener){
            this.context = context;
            this.mediaPlayerControlListener = mediaControlListener;
        }
        public Builder with(@Nullable Activity context) {
            this.context = context;
            return this;
        }

        public Builder withMediaControlListener(@Nullable MediaPlayerControlListener mediaControlListener) {
            this.mediaPlayerControlListener = mediaControlListener;
            return this;
        }


        public Builder withVideoTitle(String videoTitle) {
            this.videoTitle = videoTitle;
            return this;
        }

        public Builder withVideoView(@Nullable View videoView){
            this.videoView = videoView;
            return this;
        }

        public Builder exitIcon(@DrawableRes int exitIcon) {
            this.exitIcon = exitIcon;
            return this;
        }

        public Builder pauseIcon(@DrawableRes int pauseIcon) {
            this.pauseIcon = pauseIcon;
            return this;
        }

        public Builder playIcon(@DrawableRes int playIcon) {
            this.playIcon = playIcon;
            return this;
        }

        public Builder shrinkIcon(@DrawableRes int shrinkIcon) {
            this.shrinkIcon = shrinkIcon;
            return this;
        }

        public Builder stretchIcon(@DrawableRes int stretchIcon) {
            this.stretchIcon = stretchIcon;
            return this;
        }

        public Builder canSeekVideo(boolean canSeekVideo) {
            this.canSeekVideo = canSeekVideo;
            return this;
        }

        public Builder canControlVolume(boolean canControlVolume) {
            this.canControlVolume = canControlVolume;
            return this;
        }

        public Builder canControlBrightness(boolean canControlBrightness) {
            this.canControlBrightness = canControlBrightness;
            return this;
        }

        public Builder canControlSpeed(boolean canControlSpeed) {
            this.canControlSpeed = canControlSpeed;
            return this;
        }

        public VideoControllerView build(@Nullable ViewGroup anchorView) {
            this.anchorView = anchorView;
            VideoControllerView videoControllerView = new VideoControllerView(this);
            videoControllerView.show();
            return videoControllerView;
        }

    }


    private static class ControllerViewHandler extends Handler {
        private final WeakReference<VideoControllerView> mView;

        ControllerViewHandler(VideoControllerView view) {
            mView = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoControllerView view = mView.get();
            if (view == null || view.mMediaPlayerControlListener == null) {
                return;
            }

            int pos;
            switch (msg.what) {
                case HANDLER_ANIMATE_OUT:
                    Log.e(TAG, "handleMessage: HANDLER_ANIMATE_OUT" );
                    view.hide();
                    break;
                case HANDLER_UPDATE_PROGRESS:
                    pos = view.setSeekProgress();
                    if (!view.mIsDragging && view.mIsShowing && view.mMediaPlayerControlListener.isPlaying()) {//just in case

                        msg = obtainMessage(HANDLER_UPDATE_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }
    }


    private View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflate.inflate(R.layout.layout_media_controller, null);
        initControllerView();

        return mRootView;
    }


    private void initControllerView() {

        mTopLayout = mRootView.findViewById(R.id.layout_top);
        mBackButton = (ImageButton) mRootView.findViewById(R.id.top_back);
        mBackButton.setImageResource(mExitIcon);
        mTvPalyRate = mRootView.findViewById(R.id.tv_rate);
        mTvPalyRate.setText(mCurrentSpeedDes);
        if (mBackButton != null) {
            mBackButton.requestFocus();
            mBackButton.setOnClickListener(mBackListener);
        }

        mTitleText = (TextView) mRootView.findViewById(R.id.top_title);


        mCenterLayout = mRootView.findViewById(R.id.layout_center);
        mCenterLayout.setVisibility(GONE);
        mCenterImage = (ImageView) mRootView.findViewById(R.id.image_center_bg);
        mCenterProgress = (ProgressBar) mRootView.findViewById(R.id.progress_center);


        mBottomLayout = mRootView.findViewById(R.id.layout_bottom);
        mPauseButton = (ImageButton) mRootView.findViewById(R.id.bottom_pause);
        if (mPauseButton != null) {
            mPauseButton.requestFocus();
            mPauseButton.setOnClickListener(mPauseListener);
        }

        mFullscreenButton = (ImageButton) mRootView.findViewById(R.id.bottom_fullscreen);
        if (mFullscreenButton != null) {
            mFullscreenButton.requestFocus();
            mFullscreenButton.setOnClickListener(mFullscreenListener);
        }
        if(mMediaPlayerControlListener != null){
            duration = mMediaPlayerControlListener.getDuration();
        }
        mSeekBar = (SeekBar) mRootView.findViewById(R.id.bottom_seekbar);
        if (mSeekBar != null) {
            mSeekBar.setOnSeekBarChangeListener(mSeekListener);
            if(duration > 0){
                mSeekBar.setMax(duration / 1000);
            }else {
                mSeekBar.setMax(1000);
            }
        }

        mEndTime = (TextView) mRootView.findViewById(R.id.bottom_time);
        mCurrentTime = (TextView) mRootView.findViewById(R.id.bottom_time_current);


        mFormatBuilder = new StringBuilder();
        if(mCanControlSpeed){
            mTvPalyRate.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    changePalySpeed();

                }
            });
        }
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }


    private void changePalySpeed() {

        if (mCurrentSpeed == 1.0f) {
            mCurrentSpeedDes = "1.2x";
            mCurrentSpeed = 1.2f;
        } else if (mCurrentSpeed == 1.2f) {
            mCurrentSpeed = 1.5f;
            mCurrentSpeedDes = "1.5x";
        } else if (mCurrentSpeed == 1.5f) {
            mCurrentSpeed = 2.0f;
            mCurrentSpeedDes = "2x";
        } else if(mCurrentSpeed == 2.0f){
            mCurrentSpeed = 1.0f;
            mCurrentSpeedDes = "1x";
        }
        mTvPalyRate.setText(mCurrentSpeedDes);
        if(mMediaPlayerControlListener.isPlaying()){
            mlastSpeed = mCurrentSpeed;
            mMediaPlayerControlListener.setSpeed(mCurrentSpeed);
        }
    }
    public void showWithTitle(String videoTitle){
        this.mVideoTitle = videoTitle;
        show();
    }
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mBackButton.setVisibility(mMediaPlayerControlListener.isFullScreen() ? View.VISIBLE : View.GONE);
        mFullscreenButton.setVisibility(mMediaPlayerControlListener.isFullScreen() ? View.GONE : View.VISIBLE);
        toggleFullScreen();

    }

    private void show() {

        if (!mIsShowing && mAnchorView != null) {


            mHandler.removeCallbacks(mHideRunnable);
            mHandler.postDelayed(mHideRunnable,AUTO_HIDE_TIME);
            LayoutParams tlp = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            adjustUiDisplay();
            mAnchorView.addView(VideoControllerView.this, tlp);

            mIsShowing = true;
        }

        setSeekProgress();
        if (mPauseButton != null) {
            mPauseButton.requestFocus();
        }
        togglePausePlay();
        toggleFullScreen();

        mHandler.sendEmptyMessage(HANDLER_UPDATE_PROGRESS);

    }




    private void adjustUiDisplay() {
        ViewGroup.LayoutParams params = mBottomLayout.getLayoutParams();
        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) mTitleText.getLayoutParams();
        if(mCanControlSpeed){
            mTvPalyRate.setVisibility(View.VISIBLE);
        }
        if(mMediaPlayerControlListener.isFullScreen()){
            params.height = (int) getResources().getDimension(R.dimen.video_bottom_landscape);
            params1.topMargin = (int) getResources().getDimension(R.dimen.video_top_landscape);
            if(Utils.isAllScreenDevice(mContext)){
                params.width = Utils.getDeviceWidth(mContext) - Utils.getStatusBarHeight(mContext);
            }
            mBackButton.setVisibility(VISIBLE);
        }else {

            mPauseButton.setImageResource(mPauseIcon);
            params.height = (int) getResources().getDimension(R.dimen.video_bottom_portrait);
            params1.topMargin = (int) getResources().getDimension(R.dimen.video_top_portrait);
            mBackButton.setVisibility(GONE);
        }
        mBottomLayout.setLayoutParams(params);
        mTitleText.setLayoutParams(params1);
    }


    public void toggleControllerView() {

        if (!isShowing()) {
            show();
        } else {
            Message msg = mHandler.obtainMessage(HANDLER_ANIMATE_OUT);
            mHandler.removeMessages(HANDLER_ANIMATE_OUT);
            mHandler.sendMessageDelayed(msg, AUTO_HIDE_TIME);
        }
    }

    public void removeAllCallBacks(){
        hide();
    }



    public boolean isShowing() {
        return mIsShowing;
    }

    /**
     * 隐藏 controller view
     */
    public void hide() {
        if (mAnchorView == null || mMediaPlayerControlListener.isPaused()) {
            return;
        }

        mAnchorView.removeView(VideoControllerView.this);
        mHandler.removeMessages(HANDLER_UPDATE_PROGRESS);
        mIsShowing = false;
    }


    private String stringToTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 依据video播放进度设置seekbar的进度
     *
     * @return 当前播放位置
     */
    private int setSeekProgress() {
        if (mMediaPlayerControlListener == null || mIsDragging) {
            return 0;
        }

        int position = mMediaPlayerControlListener.getCurrentPosition();
        int duration = mMediaPlayerControlListener.getDuration();
        if (mSeekBar != null) {
            if (position < duration) {
                // use long to avoid overflow
                int pos =  position / 1000;
                mSeekBar.setProgress( pos);
                if(!mSeekBar.isEnabled()){
                    mSeekBar.setEnabled(true);
                }
            }
            //获取缓冲百分百
            int percent = mMediaPlayerControlListener.getBufferPercentage();
            //设置缓冲进度
            mSeekBar.setSecondaryProgress(percent * 10);
        }

        if (mEndTime != null)
            mEndTime.setText(stringToTime(duration));
        if (mCurrentTime != null) {
            Log.e(TAG, "position:" + position + " -> duration:" + duration);
            mCurrentTime.setText(stringToTime(position));
            if(mMediaPlayerControlListener.isComplete()){
                mCurrentTime.setText(stringToTime(duration));
                mSeekBar.setProgress(duration / 1000);
                mSeekBar.setEnabled(false);
            }
        }
        if(mTitleText != null){
            mTitleText.setText(mVideoTitle);
        }
        mPauseButton.setImageResource(mMediaPlayerControlListener.isPlaying() ? mPauseIcon : mPlayIcon);
        return position;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mHandler.removeCallbacks(mHideRunnable);
        mHandler.postDelayed(mHideRunnable,100);
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                mCurVolume = -1;
                mCurBrightness = -1;
                mCenterLayout.setVisibility(GONE);
                mPauseButton.setVisibility(VISIBLE);
            default:
                if(mGestureDetector != null)
                    mGestureDetector.onTouchEvent(event);
        }
        return mMediaPlayerControlListener.isFullScreen();

    }

    /**
     * 切换播放/暂停
     */
    private void togglePausePlay() {
        if (mRootView == null || mPauseButton == null || mMediaPlayerControlListener == null) {
            return;
        }

        if (mMediaPlayerControlListener.isPlaying()) {
            mPauseButton.setImageResource(mPauseIcon);
        } else {
            mPauseButton.setImageResource(mPlayIcon);
        }
    }

    /**
     * 切换屏幕方向
     */
    public void toggleFullScreen() {
        if (mRootView == null || mFullscreenButton == null || mMediaPlayerControlListener == null) {
            return;
        }

        if (mMediaPlayerControlListener.isFullScreen()) {
            mFullscreenButton.setImageResource(mShrinkIcon);
        } else {
            mFullscreenButton.setImageResource(mStretchIcon);
        }
    }

    private void doPauseResume() {
        if (mMediaPlayerControlListener == null) {
            return;
        }

        if (mMediaPlayerControlListener.isPlaying()) {
            mMediaPlayerControlListener.pause();
        } else {
            if(mCurrentSpeed != mlastSpeed){
                mMediaPlayerControlListener.setSpeed(mCurrentSpeed);
            }else {
                mMediaPlayerControlListener.start();
            }

            mHandler.postDelayed(mHideRunnable,AUTO_HIDE_TIME);
        }
    }

    private void doToggleFullscreen() {
        if (mMediaPlayerControlListener == null) {
            return;
        }

        mMediaPlayerControlListener.toggleFullScreen();
    }

    /**
     * seekbar进度变化监听
     */
    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {

            mIsDragging = true;
            mHandler.removeMessages(HANDLER_UPDATE_PROGRESS);
            mHandler.removeCallbacks(mHideRunnable);
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {

            if(mMediaPlayerControlListener != null){
                int progress = bar.getProgress();
                mMediaPlayerControlListener.seekTo(progress * 1000);
            }
            mIsDragging = false;
            togglePausePlay();
            mHandler.sendEmptyMessage(HANDLER_UPDATE_PROGRESS);
            mHandler.removeCallbacks(mHideRunnable);
            mHandler.postDelayed(mHideRunnable,AUTO_HIDE_TIME);
        }
    };

    @Override
    public void setEnabled(boolean enabled) {
        if (mPauseButton != null) {
            mPauseButton.setEnabled(enabled);
        }
        if (mSeekBar != null) {
            mSeekBar.setEnabled(enabled);
        }
        super.setEnabled(enabled);
    }


    /**
     * 顶部返回按钮点击事件监听
     */
    private OnClickListener mBackListener = new OnClickListener() {
        public void onClick(View v) {
            mMediaPlayerControlListener.exit();
        }
    };


    /**
     * 暂停/播放按钮点击事件监听
     */
    private OnClickListener mPauseListener = new OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
            show();
        }
    };

    /**
     * 全屏按钮点击事件监听
     */
    private OnClickListener mFullscreenListener = new OnClickListener() {
        public void onClick(View v) {
            doToggleFullscreen();
            show();
        }
    };


    public void setMediaPlayerControlListener(MediaPlayerControlListener mediaPlayerListener) {
        mMediaPlayerControlListener = mediaPlayerListener;
        togglePausePlay();
        toggleFullScreen();
    }


    private void setAnchorView(ViewGroup view) {
        mAnchorView = view;
        LayoutParams frameParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        removeAllViews();
        View v = makeControllerView();
        addView(v, frameParams);

        setGestureListener();
    }


    private void setGestureListener() {

        if(mCanControlVolume) {
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }

        mGestureDetector = new GestureDetector(mContext, new ViewGestureListener(mContext, this));
    }


    @Override
    public void onSingleTap() {
        toggleControllerView();
    }

    @Override
    public void onHorizontalScroll(boolean seekForward) {
        if (mCanSeekVideo) {
            if (seekForward) {
                seekForWard();
            } else {
                seekBackWard();
            }
        }
    }

    private void seekBackWard() {
        if (mMediaPlayerControlListener == null) {
            return;
        }

        int pos = mMediaPlayerControlListener.getCurrentPosition();
        pos -= PROGRESS_SEEK;
        mMediaPlayerControlListener.seekTo(pos);
        setSeekProgress();

        show();
    }

    private void seekForWard() {
        if (mMediaPlayerControlListener == null) {
            return;
        }

        int pos = mMediaPlayerControlListener.getCurrentPosition();
        pos += PROGRESS_SEEK;
        mMediaPlayerControlListener.seekTo(pos);
        setSeekProgress();

        show();
    }

    @Override
    public void onVerticalScroll(float percent, int direction) {
        if(mMediaPlayerControlListener.isFullScreen()) {
            if (direction == ViewGestureListener.SWIPE_LEFT) {
                if (mCanControlBrightness) {
                    mCenterImage.setImageResource(R.drawable.video_bright_bg);
                    mPauseButton.setVisibility(GONE);
                    updateBrightness(percent);
                }
            } else {
                if (mCanControlVolume) {
                    mCenterImage.setImageResource(R.drawable.video_volume_bg);
                    mPauseButton.setVisibility(GONE);
                    updateVolume(percent);
                }
            }
        }
    }


    private void updateVolume(float percent) {

        mCenterLayout.setVisibility(VISIBLE);

        if (mCurVolume == -1) {
            mCurVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mCurVolume < 0) {
                mCurVolume = 0;
            }
        }

        int volume = (int) (percent * mMaxVolume) + mCurVolume;
        if (volume > mMaxVolume) {
            volume = mMaxVolume;
        }

        if (volume < 0) {
            volume = 0;
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

        int progress = volume * 100 / mMaxVolume;
        mCenterProgress.setProgress(progress);
    }


    private void updateBrightness(float percent) {

        if (mCurBrightness == -1) {
            mCurBrightness = mContext.getWindow().getAttributes().screenBrightness;
            if (mCurBrightness <= 0.01f) {
                mCurBrightness = 0.01f;
            }
        }

        mCenterLayout.setVisibility(VISIBLE);

        WindowManager.LayoutParams attributes = mContext.getWindow().getAttributes();
        attributes.screenBrightness = mCurBrightness + percent;
        if (attributes.screenBrightness >= 1.0f) {
            attributes.screenBrightness = 1.0f;
        } else if (attributes.screenBrightness <= 0.01f) {
            attributes.screenBrightness = 0.01f;
        }
        mContext.getWindow().setAttributes(attributes);

        float p = attributes.screenBrightness * 100;
        mCenterProgress.setProgress((int) p);

    }

    private Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }



    public interface MediaPlayerControlListener {
        /**
         * 开始播放
         */
        void start();

        /**
         * 暂停播放
         */
        void pause();

        /**
         * 获取视频时长
         *
         * @return 视频资源的总时长
         */
        int getDuration();

        /**
         * 获取当前播放进度
         *
         * @return 播放进度的毫秒值
         */
        int getCurrentPosition();

        /**
         * 跳到指定进度位置播放
         *
         * @param position
         */
        void seekTo(int position);

        /**
         * 判断视频是否是播放状态
         *
         * @return
         */
        boolean isPlaying();

        /**
         * 视频是否播放结束
         * @return
         */
        boolean isComplete();

        /**
         * 获取缓冲进度的百分比
         *
         * @return
         */
        int getBufferPercentage();

        /**
         * 播放器是否是否全屏
         * @return 是否全屏
         */
        boolean isFullScreen();

        /**
         * 切换到全屏播放
         */
        void toggleFullScreen();

        /**
         * 退出播放器
         */
        void exit();

        void setSpeed(float speed);



        boolean isPaused();
    }
}
