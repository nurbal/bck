/*
 * This file is part of the Bridge Construction Kit distribution (https://github.com/https://github.com/nurbal/bck).
 * Copyright (c) 2019 Bruno Carrez.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

#ifndef __LOG_BUILDER__
#define __LOG_BUILDER__

#include <android/log.h>
#include <pthread.h>

#define  LOG_TAG    "BCK_JNI"

#include <stdio.h>
static char s[256];

#define  LOGI(...)  //{sprintf(s,"%s %d %s #%i",LOG_TAG,pthread_self(), __FILE__, __LINE__); __android_log_print(ANDROID_LOG_INFO,s,__VA_ARGS__);}
#define  LOGW(...)  //{sprintf(s,"%s %d %s #%i",LOG_TAG,pthread_self(), __FILE__, __LINE__); __android_log_print(ANDROID_LOG_WARN,s,__VA_ARGS__);}
#define  LOGE(...)  {sprintf(s,"%s %d %s #%i",LOG_TAG,(unsigned int)pthread_self(), __FILE__, __LINE__); __android_log_print(ANDROID_LOG_ERROR,s,__VA_ARGS__);}
#define  LOGD(...)  //{sprintf(s,"%s %d %s #%i",LOG_TAG,(unsigned int)pthread_self(), __FILE__, __LINE__); __android_log_print(ANDROID_LOG_DEBUG,s,__VA_ARGS__);}

#define CHECK_PTR(p) {if (!(p)) LOGE("CHECK_PTR FAILED !!!!!!");}
#define CHECK_SPTR(sp) {if (!(sp)) LOGE("CHECK_PTR FAILED (1) !!!!!!"); if (!(sp)->GetVoid()) LOGE("CHECK_PTR FAILED (2) !!!!!!");}

#define CHECK_NAN(f) {if(isnan(f) || !isfinite(f)) LOGE("NaN detected!");}
#define CHECK_NAN_V(v) {CHECK_NAN(v->x); CHECK_NAN(v->y); CHECK_NAN(v->z); }


#endif // __LOG_BUILDER__
