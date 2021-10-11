基于ffmpeg的Android音视频开发
=============================

资料
----
NDK 版本 14b https://developer.android.com/ndk/downloads/older_releases?hl=zh-cn  
ffmpeg 版本 FFmpeg 3.4.8 "Cantor" https://ffmpeg.org/download.html#releases  
  
  解码性能测试：  
  - 1.软解码 单线程: 帧率 20fps      内存 27M    CPU使用率 12%  
  - 2.软解码 8线程:  帧率 95-110fps  内存 77M    CPU使用率 83%  
  - 3.硬解码 :       帧率 130fps     内存 24M    CPU使用率 13%  

项目进度
--------
2020:12:13 编译ffmpeg源码，导入头文件和库文件,初始化Android studio工程  
2020:12:15 完成解封装操作，成功读取到stream并且获得AVPacket数据  
2020:12:16 完成解码操作，成功读取到AVframe获得解码后的数据数据  
2020:12:17 解码后的视频数据转换为RGBA格式 并使用surfaceView显示,去除窗口标题并横屏显示  
2020:12:18 OpenSL播放PCM码流的Demo  




