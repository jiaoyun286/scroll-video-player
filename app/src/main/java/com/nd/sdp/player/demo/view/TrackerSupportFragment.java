package com.nd.sdp.player.demo.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.bumptech.glide.Glide;
import com.nd.android.bk.video.detector.RecyclerScrollDetector;
import com.nd.android.bk.video.meta.DefaultMetaData;
import com.nd.android.bk.video.tracker.IViewTracker;
import com.nd.android.bk.video.tracker.Tracker;
import com.nd.android.bk.video.tracker.VisibleChangeListener;
import com.nd.android.bk.video.videomanager.controller.DefaultControllerView;
import com.nd.android.bk.video.videomanager.interfaces.PlayerItemChangeListener;
import com.nd.android.bk.video.videomanager.interfaces.VideoPlayerListener;
import com.nd.android.bk.video.videomanager.player.RatioImageView;
import com.nd.sdp.bk.video.R;
import com.nd.sdp.player.demo.Backable;
import com.nd.sdp.player.demo.ListDataGenerater;
import com.nd.sdp.player.demo.MainActivity;
import com.nd.sdp.player.demo.VideoModel;


import java.lang.ref.WeakReference;

/**
 * @author JiaoYun
 * @date 2019/10/15 19:06
 */
public class TrackerSupportFragment extends Fragment implements View.OnClickListener, VisibleChangeListener, PlayerItemChangeListener, VideoPlayerListener, Backable {
    private static final String TAG = TrackerSupportFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private static final float VISIBLE_THRESHOLD = 1f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracker_support,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        VideoListAdapter adapter = new VideoListAdapter();
        adapter.setOnClickListener(this);
        mRecyclerView.setAdapter(adapter);
        Tracker.addPlayerItemChangeListener(this);
        Tracker.addVideoPlayerListener(this);
    }

    @Override
    public void onClick(View view) {
        if(!Tracker.isSameTrackerView(getActivity(),view)) {
            Tracker.attach(this)
                    .trackView(view)
                    .into(new RecyclerScrollDetector(mRecyclerView))
                    .controller(new DefaultControllerView())
                    .visibleListener(this);
        }
//        Tracker.attach(getActivity()).trackView(view).into(new RecyclerScrollDetector(mRecyclerView));
        //无缝平移到详情页面进行播放
        ((TrackerSupportActivity) getActivity()).addDetailFragment();

    }

    @Override
    public void onVisibleChange(float visibleRatio, IViewTracker tracker) {
        Log.e(TAG, "onVisibleChange : edge -> " + tracker.getEdgeString());
        if(!tracker.getFloatLayerView().getVideoPlayerView().isComplete()) {
            if (visibleRatio <= VISIBLE_THRESHOLD) {
                tracker.hide();
            } else {
                tracker.show();
            }
        }
    }

    @Override
    public void onPlayerItemChanged(IViewTracker viewTracker) {

    }

    @Override
    public void onVideoSizeChanged(IViewTracker viewTracker, int width, int height) {

    }

    @Override
    public void onVideoPrepared(IViewTracker viewTracker) {

    }

    @Override
    public void onVideoCompletion(IViewTracker viewTracker) {
        //全屏播放，先切回竖屏
        if(viewTracker.isFullScreen()){
            viewTracker.toNormalScreen();
        }
        //将回退栈最上面的页面移除
        onBackPressed();
    }

    @Override
    public void onError(IViewTracker viewTracker, int what, int extra) {

    }

    @Override
    public void onBufferingUpdate(IViewTracker viewTracker, int percent) {

    }

    @Override
    public void onInfo(IViewTracker viewTracker, int what) {

    }

    @Override
    public void onVideoStarted(IViewTracker viewTracker) {

    }

    @Override
    public void onVideoPaused(IViewTracker viewTracker) {

    }

    @Override
    public void onVideoStopped(IViewTracker viewTracker) {

    }

    @Override
    public void onVideoReset(IViewTracker viewTracker) {

    }

    @Override
    public void onVideoReleased(IViewTracker viewTracker) {

    }

    @Override
    public boolean onBackPressed() {
        if (getActivity() != null){
            if(Tracker.isAttach(getActivity())){
                Tracker.destroy(getActivity());
            }
            getActivity().getSupportFragmentManager().popBackStack();
        }

        return true;
    }

    static class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoHolder>{

        private  WeakReference<View.OnClickListener> mWeakReference;
        @Override
        public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VideoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_view_new,parent,false),mWeakReference.get());
        }

        @Override
        public void onBindViewHolder(VideoHolder holder, int position) {
            VideoModel videoModel = ListDataGenerater.datas.get(position);
            holder.coverImage.setTag(R.id.tag_tracker_view,new DefaultMetaData(ListDataGenerater.datas.get(position).videoUrl));
            Glide.with(holder.coverImage.getContext()).load(videoModel.coverImage).into(holder.coverImage);
        }

        public void setOnClickListener(View.OnClickListener onClickListener){
            if(mWeakReference == null){
                mWeakReference = new WeakReference<>(onClickListener);
            }
        }

        @Override
        public int getItemCount() {
            return ListDataGenerater.datas.size();
        }

        static class VideoHolder extends RecyclerView.ViewHolder{
            private RatioImageView coverImage;
            public VideoHolder(View itemView, View.OnClickListener onClickListener) {
                super(itemView);
                coverImage = itemView.findViewById(R.id.view_tracker);
                coverImage.setRatio(16,9);
                coverImage.setOnClickListener(onClickListener);
            }
        }
    }
}
