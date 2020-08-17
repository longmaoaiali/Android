package com.cvte.player.interfaces;

/**
 * Created by user on 2020/8/11.
 */

public interface IPlayViewControl {

    //播放状态与进度的改变
    void onPlayerStateChange(int state);
    void onSeekChange(final int seek);

}
