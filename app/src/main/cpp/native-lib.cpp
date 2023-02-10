#include <jni.h>
#include <string>
#include "v4l2_camera.hpp"
#include "image_utility.hpp"
#define G_V4L2_QCARCAM single_instance<v4l2_camera>::instance()
std::unique_ptr<v4l2_camera>G_V4L2_CAMERA_PTR = std::make_unique<v4l2_camera>(G_V4L2_QCARCAM);
extern "C"
JNIEXPORT jint JNICALL
Java_com_arcsoft_v4l2cameradisplay_ProcessThread_init_1camera(JNIEnv *env, jobject thiz) {
    // TODO: implement init_camera()
    return G_V4L2_CAMERA_PTR->init();
}
extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_arcsoft_v4l2cameradisplay_ProcessThread_get_1nv12_1frame(JNIEnv *env, jobject thiz) {
    // TODO: implement get_nv12_frame()
    if (false == G_V4L2_CAMERA_PTR->get_frame()) {
        return env->NewByteArray(0);
    }
    size_t width = G_V4L2_CAMERA_PTR->get_width();
    size_t height = G_V4L2_CAMERA_PTR->get_height();
    size_t frame_size = 0;
    char *nv12_content = G_IMAGE_UTILITY.convert_uyvy_nv12(G_V4L2_CAMERA_PTR->frame, G_V4L2_CAMERA_PTR->frame_len, width, height, frame_size);
    if (nullptr == nv12_content) {
        return env->NewByteArray(0);
    }
    jbyteArray data = env->NewByteArray(frame_size);    // return nv12
    env->SetByteArrayRegion(data, 0, frame_size, (jbyte *)nv12_content);
    free(nv12_content);
    return data;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_arcsoft_v4l2cameradisplay_ProcessThread_get_1width(JNIEnv *env, jobject thiz) {
    // TODO: implement get_width()
    return G_V4L2_CAMERA_PTR->get_width();
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_arcsoft_v4l2cameradisplay_ProcessThread_get_1height(JNIEnv *env, jobject thiz) {
    // TODO: implement get_height()
    return G_V4L2_CAMERA_PTR->get_height();
}