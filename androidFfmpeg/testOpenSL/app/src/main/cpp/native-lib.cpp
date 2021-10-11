#include <jni.h>
#include <string>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include <android/log.h>

#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,"testff",__VA_ARGS__)

static SLObjectItf  engineSL = NULL;
SLEngineItf  CreateSL()
{
    SLresult re;
    SLEngineItf en;
    //创建引擎
    re = slCreateEngine(&engineSL,0,0,0,0,0);
    if(re != SL_RESULT_SUCCESS) return  NULL;
    //实例化
    re = (*engineSL)->Realize(engineSL,SL_BOOLEAN_FALSE);
    if(re != SL_RESULT_SUCCESS) return  NULL;
    //得到接口
    re = (*engineSL)->GetInterface(engineSL,SL_IID_ENGINE,&en);
    if(re != SL_RESULT_SUCCESS) return NULL;
    return en;

}

void PcmCall(SLAndroidSimpleBufferQueueItf bufferQueueItf, void *const)
{
    LOGW("PCM CAll");
    static FILE *fp = NULL;
    static char *buf = NULL;
    if(!buf){
        buf = new char[1024*1024];
    }
    if(!fp){
        fp=fopen("/sdcard/test.pcm","rb");
    }

    if(!fp) return;

    //没有到结尾
    if(feof(fp) == 0)
    {
        int len = fread(buf,1,1024,fp);
        if(len > 0){
            //将PCM数据放在buffer里面
            (*bufferQueueItf)->Enqueue(bufferQueueItf,buf,len);
        }
    }

}

extern "C"
JNIEXPORT jstring
JNICALL
Java_com_cvte_testopensl_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";

    //1. 创建引擎
    SLEngineItf eng = CreateSL();
    if(eng){
        LOGW("create success");
    } else {
        LOGW("create failed");
    }

    //2 创建混音器并初始化
    SLObjectItf mix = NULL;
    SLresult re =0;
    re=(*eng)->CreateOutputMix(eng,&mix,0,0,0);
    if(re != SL_RESULT_SUCCESS)
    {
        LOGW("CreateOutputMix failed !");
    }

    re = (*mix)->Realize(mix,SL_BOOLEAN_FALSE);
    if(re != SL_RESULT_SUCCESS)
    {
        LOGW("(*mix)->Realize failed !");
    }
    SLDataLocator_OutputMix outputMix = {SL_DATALOCATOR_OUTPUTMIX,mix};
    SLDataSink audioSink = {&outputMix,0};

    //3 配置输入OpenSL的PCM的信息
    //3.1缓冲队列
    SLDataLocator_AndroidSimpleBufferQueue que = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE,10};
    //3.2音频格式
    SLDataFormat_PCM pcm = {
            SL_DATAFORMAT_PCM,
            2,
            SL_SAMPLINGRATE_44_1,
            SL_PCMSAMPLEFORMAT_FIXED_16,
            SL_PCMSAMPLEFORMAT_FIXED_16,
            SL_SPEAKER_FRONT_LEFT|SL_SPEAKER_FRONT_RIGHT,
            SL_BYTEORDER_LITTLEENDIAN
    };
    SLDataSource ds = {&que,&pcm};


    //4 创建播放器
    SLObjectItf player = NULL;
    SLPlayItf playItf = NULL;
    SLAndroidSimpleBufferQueueItf pcmQue = NULL;
    //创建的参数，代表开放的接口
    const SLInterfaceID ids[] = {SL_IID_BUFFERQUEUE};
    const SLboolean req[] = {SL_BOOLEAN_TRUE};
    (*eng)->CreateAudioPlayer(eng,&player,&ds,&audioSink, sizeof(ids)/ sizeof(SLInterfaceID),ids,req);
    if(re != SL_RESULT_SUCCESS)
    {
        LOGW("CreateAudioPlayer failed");
    } else {
        LOGW("CreateAudioPlayer success");
    }

    (*player)->Realize(player,SL_BOOLEAN_FALSE);
    //获取player的接口
    re = (*player)->GetInterface(player,SL_IID_PLAY,&playItf);
    if(re != SL_RESULT_SUCCESS){
        LOGW("(*player)->GetInterface SL_IID_PLAY failed");
    }
    //获取播放的缓冲队列
    re = (*player)->GetInterface(player,SL_IID_BUFFERQUEUE,&pcmQue);
    if(re != SL_RESULT_SUCCESS){
        LOGW("(*player)->GetInterface SL_IID_BUFFERQUEUE failed");
    }

    //注册回调函数 播放队列空时调用
    (*pcmQue)->RegisterCallback(pcmQue, (slAndroidSimpleBufferQueueCallback) PcmCall, 0);
    //设置为播放状态
    (*playItf)->SetPlayState(playItf,SL_PLAYSTATE_PLAYING);
    //启动队列回调,队列为空的时候回调
    (*pcmQue)->Enqueue(pcmQue,"",1);

    return env->NewStringUTF(hello.c_str());
}
