#include <jni.h>
#include <string>
#include <android/log.h>
#include <android/native_window_jni.h>
#include <EGL/egl.h>
#include <GLES2/gl2.h>
#include <string.h>

#define LOGD(...) __android_log_print(ANDROID_LOG_WARN,"testgl",__VA_ARGS__)

//顶点着色器glsl
//#可以将字符转换为字符串
#define GET_STR(x) #x
static const char *vertexShader = GET_STR(
        attribute vec4 aPosition; //顶点坐标
        attribute vec2 aTexCoord; //材质顶点坐标
        varying vec2 vTexCoord;   //输出的材质坐标
        void main(){
            vTexCoord = vec2(aTexCoord.x,1.0-aTexCoord.y);
            gl_Position = aPosition;
        }
);

//片元着色器,软解码和部分x86硬解码
static const char *fragYUV420P = GET_STR(
        precision mediump float;    //精度
        varying vec2 vTexCoord;     //顶点着色器传递的坐标
        uniform sampler2D yTexture; //输入的材质（不透明灰度，单像素）
        uniform sampler2D uTexture;
        uniform sampler2D vTexture;
        void main(){
            vec3 yuv;
            vec3 rgb;
            yuv.r = texture2D(yTexture,vTexCoord).r;
            yuv.g = texture2D(uTexture,vTexCoord).r - 0.5;
            yuv.b = texture2D(vTexture,vTexCoord).r - 0.5;
            rgb = mat3(1.0,     1.0,    1.0,
                       0.0,-0.39465,2.03211,
                       1.13983,-0.58060,0.0)*yuv;
            //输出像素颜色
            gl_FragColor = vec4(rgb,1.0);
        }
);

GLint InitShader(const char *code,GLint type)
{
    GLint sh = glCreateShader(type);
    if(sh==0){
        LOGD("glCreateShader %d failed!",type);
        return 0;
    }
    //加载shader
    glShaderSource(sh,
                   1,//shader数量
                   &code,//shader代码
                   0);

    //编译shader ,显卡去跑shader程序
    glCompileShader(sh);
    //获取编译情况
    GLint status;
    glGetShaderiv(sh,GL_COMPILE_STATUS,&status);
    if(status == 0){
        LOGD("glCompileShader failed!");
        return 0;
    }

    return sh;
}

extern "C"
JNIEXPORT jstring
JNICALL
Java_com_cvte_testopengl_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";

    return env->NewStringUTF(hello.c_str());
}


extern "C"
JNIEXPORT void JNICALL
Java_com_cvte_testopengl_Xplayer_open(JNIEnv *env, jobject instance, jstring url_,
                                      jobject surface) {
    const char *url = env->GetStringUTFChars(url_, 0);

    // TODO
    LOGD("open url is %s",url);
    FILE *fp = fopen(url,"rb");
    if(!fp){
        LOGD("open file %s failed!",url);
        return;
    }
    //获取原始窗口
    ANativeWindow *nwin = ANativeWindow_fromSurface(env,surface);
    //1 display的创建和初始化
    EGLDisplay display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if(display == EGL_NO_DISPLAY){
        LOGD("eglGetDisplay failed");
        return;
    }

    if(EGL_TRUE != eglInitialize(display,0,0)){
        LOGD("eglInitialize failed");
        return;
    }

    //2 surface
    //2-1 surface窗口配置
    //输出配置
    EGLConfig config;
    EGLint configNum;
    EGLint configSpec[]={
            EGL_RED_SIZE,8,
            EGL_GREEN_SIZE,8,
            EGL_BLUE_SIZE,8,
            EGL_SURFACE_TYPE,EGL_WINDOW_BIT,EGL_NONE
    };

    if(EGL_TRUE != eglChooseConfig(display,configSpec,&config,1,&configNum)){
        LOGD("eglChooseConfig failed");
        return;
    }
    //创建surface
    EGLSurface winsurface = eglCreateWindowSurface(display,config,nwin,0);
    if(winsurface == EGL_NO_SURFACE) {
        LOGD("eglCreateWindowSurface failed");
        return;
    }

    //3.context 创建关联的上下文
    const EGLint ctxAttr[] = {
            EGL_CONTEXT_CLIENT_VERSION,2,EGL_NONE
    };

    EGLContext context = eglCreateContext(display,config,EGL_NO_CONTEXT,ctxAttr);
    if(context == EGL_NO_CONTEXT) {
        LOGD("eglCreateContext failed");
        return;
    }

    if(EGL_TRUE != eglMakeCurrent(display,winsurface,winsurface,context)){
        LOGD("eglMakeCurrent failed");
        return;
    }

    LOGD("EGL init success!");

    //顶点和片元的初始化
    //顶点shader初始化
    GLint vsh = InitShader(vertexShader,GL_VERTEX_SHADER);
    //YUV420 shader初始化
    GLint fsh = InitShader(fragYUV420P,GL_FRAGMENT_SHADER);

    //创建渲染程序
    GLint program = glCreateProgram();
    if(program == 0){
        LOGD("glCreateProgram failed!");
        return;
    }
    //渲染程序加入着色器代码
    glAttachShader(program,vsh);
    glAttachShader(program,fsh);

    //链接程序
    glLinkProgram(program);
    GLint status = 0;
    glGetProgramiv(program,GL_LINK_STATUS,&status);
    if(status != GL_TRUE){
        LOGD("glGetProgramiv failed!");
        return;
    }
    glUseProgram(program);
    LOGD("glGetProgramiv success!");

    //加入三维顶点数据，两个三角形组成一个正方形
    static float ver[] = {
            1.0f,-1.0f,0.0f,
            -1.0f,-1.0f,0.0f,
            1.0f,1.0f,0.0f,
            -1.0f,1.0f,0.0f
    };
    GLuint apos = (GLuint)glGetAttribLocation(program,"aPosition");
    glEnableVertexAttribArray(apos);
    //传递顶点
    glVertexAttribPointer(apos,3,GL_FLOAT,GL_FALSE,12,ver);
    //加入材质坐标
    static float txt[] = {
            1.0f,0.0f,
            0.0f,0.0f,
            1.0f,1.0f,
            0.0f,1.0f
    };

    GLuint atex = (GLuint)glGetAttribLocation(program,"aTextCoord");
    glEnableVertexAttribArray(atex);
    glVertexAttribPointer(atex,2,GL_FLOAT,GL_FALSE,8,txt);

    int width = 424;
    int height = 240;
    //材质纹理初始化
    //1.设置材质纹理层
    glUniform1i(glGetUniformLocation(program,"yTexture"),0);//对应纹理第一层
    glUniform1i(glGetUniformLocation(program,"uTexture"),1);//对应纹理第二层
    glUniform1i(glGetUniformLocation(program,"vTexture"),2);//对应纹理第三层
    //创建opengl的纹理
    GLuint texts[3] = {0};
    //创建三个纹理
    glGenTextures(3,texts);
    //设置纹理属性
    glBindTexture(GL_TEXTURE_2D,texts[0]);
    //放大或者缩小的过滤器都采用线性过滤
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
    //设置纹理的格式和大小
    glTexImage2D(GL_TEXTURE_2D,
                 0,
                 GL_LUMINANCE,//gpu内部格式 亮度 灰度图
                 width,height,//尺寸是2的次方
                 0,
                 GL_LUMINANCE,//数据的格式 亮度 灰度图
                 GL_UNSIGNED_BYTE,//像素的数据格式
                 NULL //纹理的数据
    );


    //设置纹理属性
    glBindTexture(GL_TEXTURE_2D,texts[1]);
    //放大或者缩小的过滤器都采用线性过滤
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
    //设置纹理的格式和大小
    glTexImage2D(GL_TEXTURE_2D,
                 0,
                 GL_LUMINANCE,//gpu内部格式 亮度 灰度图
                 width/2,height/2,//尺寸是2的次方
                 0,
                 GL_LUMINANCE,//数据的格式 亮度 灰度图
                 GL_UNSIGNED_BYTE,//像素的数据格式
                 NULL //纹理的数据
    );

    //设置纹理属性 第三个材质
    glBindTexture(GL_TEXTURE_2D,texts[2]);
    //放大或者缩小的过滤器都采用线性过滤
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
    //设置纹理的格式和大小
    glTexImage2D(GL_TEXTURE_2D,
                 0,
                 GL_LUMINANCE,//gpu内部格式 亮度 灰度图
                 width/2,height/2,//尺寸是2的次方
                 0,
                 GL_LUMINANCE,//数据的格式 亮度 灰度图
                 GL_UNSIGNED_BYTE,//像素的数据格式
                 NULL //纹理的数据
    );

    //纹理的修改与显示
    unsigned char *buf[3] = {0};
    buf[0] = new unsigned char[width*height];
    buf[1] = new unsigned char[width*height/4];
    buf[2] = new unsigned char[width*height/4];

    for(int i=0;i<10000;i++)
    {

    //memset(buf[0],i,width*height);
    //memset(buf[1],i,width*height/4);
    //memset(buf[2],i,width*height/4);
        // 420P yyyyyyyy uu vv
    if(feof(fp) == 0){
        fread(buf[0],1,width*height,fp);
        fread(buf[1],1,width*height/4,fp);
        fread(buf[2],1,width*height/4,fp);
    }

    //激活第一层纹理 并绑定到创建的第一层纹理
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D,texts[0]);
    //替换纹理内容
    glTexSubImage2D(GL_TEXTURE_2D,0,0,0,width,height,GL_LUMINANCE,GL_UNSIGNED_BYTE,buf[0]);

    //激活第二层纹理 并绑定到创建的第二层纹理
    glActiveTexture(GL_TEXTURE0+1);
    glBindTexture(GL_TEXTURE_2D,texts[1]);
    //替换纹理内容
    glTexSubImage2D(GL_TEXTURE_2D,0,0,0,width/2,height/2,GL_LUMINANCE,GL_UNSIGNED_BYTE,buf[1]);

    //激活第三层纹理 并绑定到创建的第三层纹理
    glActiveTexture(GL_TEXTURE0+2);
    glBindTexture(GL_TEXTURE_2D,texts[2]);
    //替换纹理内容
    glTexSubImage2D(GL_TEXTURE_2D,0,0,0,width/2,height/2,GL_LUMINANCE,GL_UNSIGNED_BYTE,buf[2]);

    //三维绘制图像
    glDrawArrays(GL_TRIANGLE_STRIP,0,4);
    //窗口显示
    eglSwapBuffers(display,winsurface);

    }

    env->ReleaseStringUTFChars(url_, url);
}