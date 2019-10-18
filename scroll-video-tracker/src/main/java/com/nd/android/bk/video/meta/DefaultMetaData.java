package com.nd.android.bk.video.meta;

/**
 * @author JiaoYun
 * @date 2019/10/14 20:45
 */
public class DefaultMetaData implements MetaData {
    private String videoUrl;

    public DefaultMetaData(String videoUrl){
        this.videoUrl = videoUrl;
    }

    @Override
    public String getVideoUrl() {
        return videoUrl;
    }
}
