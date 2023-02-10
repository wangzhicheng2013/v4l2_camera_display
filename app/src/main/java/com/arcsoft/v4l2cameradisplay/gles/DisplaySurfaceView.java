package com.arcsoft.v4l2cameradisplay.gles;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class DisplaySurfaceView extends GLSurfaceView {
    private final static String TAG = "DisplaySurfaceView";
    private final static int OPENGL_ES_VERSION = 2;
    public DisplaySurfaceView(Context context) {
        super(context);
        mGlRenderer = new GLRenderer();
        setEGLContextClientVersion(OPENGL_ES_VERSION);               // 设置opengl版本
        setRenderer(mGlRenderer);                                    // 设置渲染器
        setRenderMode(RENDERMODE_WHEN_DIRTY);                        // 设置渲染模式
    }

    public DisplaySurfaceView(Context context, AttributeSet attrs) {
        super(context,attrs);
        mGlRenderer = new GLRenderer();
        setEGLContextClientVersion(OPENGL_ES_VERSION);
        setRenderer(mGlRenderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        Log.d(TAG,"DisplaySurfaceView init");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        Log.d(TAG,"surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        super.surfaceChanged(holder, format, w, h);
        Log.d(TAG,"surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        Log.d(TAG,"surfaceDestroyed");
    }

    public GLRenderer mGlRenderer;

    public static class GLRenderer implements Renderer {

        private int[] mTexName;
        NV12Display mNV12Display;

        SurfaceTexture mSurfaceTexture;

        GLRenderer() {
            mSurfaceTexture = new SurfaceTexture(0);
        }

        public byte[] nv12;
        public int mWidth;
        public int mHeight;
        private int mSurfaceWidth, mSurfaceHeight;
        private int mShowWidth, mShowHeight;
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config){
            mNV12Display = new NV12Display();
            // 设置背景颜色
            GLES20.glClearColor(0.0f,0,0,0.0f);

            mTexName = new int[3];
            GLES20.glGenTextures(3,mTexName,0);                 //生成纹理

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexName[0]);    //将指定纹理绑定到目标上
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);   //对纹理的设置
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);   //使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);//GL_CLAMP_TO_EDGE：采样纹理边缘，即剩余部分显示纹理临近的边缘颜色值
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexName[1]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexName[2]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0,0,width,height);
            mSurfaceWidth = width;
            mSurfaceHeight = height;
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            // 画背景颜色
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            if (0 == mWidth || 0 == mHeight || null == nv12) {
                return;
            }
            Log.d(TAG,"onDrawFrame");
            adjustShowScreen();
            mNV12Display.draw(nv12,mWidth,mHeight,mTexName);
        }
        public void adjustShowScreen() {
            // find the most suitable window size according to the aspect ratio
            if ((mWidth == mSurfaceWidth) && (mHeight == mSurfaceHeight)) {
                return;
            }
            if ((float)mWidth / (float)mHeight < (float)mSurfaceWidth / (float)mSurfaceHeight) {
                // Wider screen
                mShowWidth = (int)((float)mSurfaceHeight * (float)mWidth / (float)mHeight);
                mShowHeight = mSurfaceHeight;
            } else {
                // Higher screen
                mShowHeight = (int)((float)mSurfaceWidth * (float)mHeight / (float)mWidth);
                mShowWidth = mSurfaceWidth;
            }
            GLES20.glViewport(0, 0, mShowWidth, mShowHeight);
        }
    }
}