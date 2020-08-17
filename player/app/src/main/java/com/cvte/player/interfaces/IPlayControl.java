package com.cvte.player.interfaces;

import java.io.IOException;

/**
 * Created by user on 2020/8/11.
 */

public interface IPlayControl  {
    //播放状态
    public int PLAY_STATE_PLAYER = 1;
    public int PLAY_STATE_PAUSE = 2;
    public int PLAY_STATE_STOP = 3;


    void playOrPause() throws IOException;
    void pause();
    void resume();
    void stop();

    void seekTo(int seek);

    //UI的控制接口给逻辑层
    void registerViewController(IPlayViewControl viewController);

    void unRegisterViewController();

}
