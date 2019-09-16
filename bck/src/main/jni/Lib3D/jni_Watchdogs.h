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

#ifndef __WATHDOGS__
#define __WATHDOGS__

//#include "jni_Misc.h"
#include "lib3d_mutex.h"

/*
extern int JNICount;
extern char lastJNICall[256];

#define JNI_IN { JNICount++; if (JNICount>1) {LOGW("{%d} JNICount = %i lastCallJNI=%s",(unsigned int)pthread_self(),JNICount,lastJNICall); LogJavaCallStack();} sprintf(lastJNICall,"{%d} %s #%i",(unsigned int)pthread_self(),__FILE__,__LINE__); }
#define JNI_OUT { JNICount--; }
*/
#define JNI_IN 				JNI_MUTEX_LOCK	// on s'assure de ne pas appeler la lib en multithread
#define JNI_OUT				JNI_MUTEX_UNLOCK	// on s'assure de ne pas appeler la lib en multithread


#define JNI_DESTRUCTOR_IN 	JNI_IN
#define JNI_DESTRUCTOR_OUT 	JNI_OUT




#endif
