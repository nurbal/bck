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

#ifndef __LOG_LIB3D__
#define __LOG_LIB3D__

#include <android/log.h>

#define  LOG_TAG    "Lib3Djni"

#include <stdio.h>
static char s[256];

#define  LOGI(...)  //{sprintf(s,"%s %X %s #%i",LOG_TAG,pthread_self(), __FILE__, __LINE__); __android_log_print(ANDROID_LOG_INFO,s,__VA_ARGS__);}
#define  LOGW(...)  //{sprintf(s,"%s %d %s #%i",LOG_TAG,pthread_self(), __FILE__, __LINE__); __android_log_print(ANDROID_LOG_WARN,s,__VA_ARGS__);}
#define  LOGV(...)  //{sprintf(s,"%s %X %s #%i",LOG_TAG,pthread_self(), __FILE__, __LINE__); __android_log_print(ANDROID_LOG_VERBOSE,s,__VA_ARGS__);}
#define  LOGE(...)  //{sprintf(s,"%s %d %s #%i",LOG_TAG,pthread_self(), __FILE__, __LINE__); __android_log_print(ANDROID_LOG_ERROR,s,__VA_ARGS__);}
#define  LOGJNI(...)  //{sprintf(s,"%s %X %s #%i",LOG_TAG,pthread_self(), __FILE__, __LINE__); __android_log_print(ANDROID_LOG_INFO,s,__VA_ARGS__);}
#define  LOGD(...)  //{sprintf(s,"%s %X %s #%i",LOG_TAG,pthread_self(), __FILE__, __LINE__); __android_log_print(ANDROID_LOG_DEBUG,s,__VA_ARGS__);}

#define CHECK_NAN(f) {if(isnan(f) || !isfinite(f)) LOGW("NaN detected!");}
#define CHECK_NAN_V(v) {CHECK_NAN(v->x); CHECK_NAN(v->y); CHECK_NAN(v->z); }




#endif // __LOG_LIB3D__
