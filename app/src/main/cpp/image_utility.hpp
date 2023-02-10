#pragma once
#include <stdio.h>
#include <string.h>
#include <fcntl.h>
#include "single_instance.hpp"
class image_utility {
public:
    char *convert_uyvy_nv12(const char *uyvy_content,
                            size_t size,
                            size_t width,
                            size_t height,
                            size_t &frame_size) {
        if (!uyvy_content) {
            return nullptr;
        }
        // convert uyvy to nv12
        frame_size = width * height * 3 / 2;
        char *nv21_content = (char *)malloc(sizeof(char) * frame_size);
        if (!nv21_content) {
            return nullptr;
        }
        size_t y_size = width * height;
        size_t pixels_in_a_row = width * 2;
        char *nv12_y_ptr = nv21_content;
        char *nv12_uv_ptr = nv21_content + y_size;
        int lines = 0;
        for (int i = 0;i < size;i += 4) {
            // copy y channel
            *nv12_y_ptr++ = uyvy_content[i + 1];
            *nv12_y_ptr++ = uyvy_content[i + 3];
            if (0 == i % pixels_in_a_row) {
                ++lines;
            }
            if (lines % 2) {       // extract the UV value of odd rows
                // copy uv channel
                *nv12_uv_ptr++ = uyvy_content[i];
                *nv12_uv_ptr++ = uyvy_content[i + 2];
            }
        }
        return nv21_content;
    }
    char *convert_uyvy_nv21(const char *uyvy_content,
                            size_t size,
                            size_t width,
                            size_t height,
                            size_t &frame_size) {
        if (!uyvy_content) {
            return nullptr;
        }
        // convert uyvy to nv21
        frame_size = width * height * 3 / 2;
        char *nv21_content = (char *)malloc(sizeof(char) * frame_size);
        if (!nv21_content) {
            return nullptr;
        }
        size_t y_size = width * height;
        size_t pixels_in_a_row = width * 2;
        char *nv21_y_ptr = nv21_content;
        char *nv21_uv_ptr = nv21_content + y_size;
        int lines = 0;
        for (int i = 0;i < size;i += 4) {
            // copy y channel
            *nv21_y_ptr++ = uyvy_content[i + 1];
            *nv21_y_ptr++ = uyvy_content[i + 3];
            if (0 == i % pixels_in_a_row) {
                ++lines;
            }
            if (lines % 2) {       // extract the UV value of odd rows
                // copy uv channel
                *nv21_uv_ptr++ = uyvy_content[i + 2];
                *nv21_uv_ptr++ = uyvy_content[i];
            }
        }
        return nv21_content;
    }
};

#define  G_IMAGE_UTILITY single_instance<image_utility>::instance()