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

#include <jni.h>
#include <cstring>
#include "release_settings.h"
#include "log.h"

static JNIEnv *JNI_Env;
static jclass JNI_Class_bck;
static jclass JNI_Class_Lib3D;
static jmethodID JNI_nativeCrashed;
static jmethodID JNI_logCallStack;


extern "C" {

static struct sigaction old_sa[NSIG];

void android_sigaction(int signal, siginfo_t *info, void *reserved)
{
	LOGE("android_sigaction : native code crashed!");
	JNI_Env->CallStaticVoidMethod(JNI_Class_bck, JNI_nativeCrashed);
	old_sa[signal].sa_handler(signal);
	LOGE("android_sigaction : end of report");
}

//JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved)
jint JNI_OnLoad(JavaVM *jvm, void *reserved)
{
    JNIEnv *env = NULL;
    if (jvm->GetEnv((void **)&env, JNI_VERSION_1_2))
        return JNI_ERR;
    JNI_Class_bck = env->FindClass("com/zerracsoft/bck/JNIinterface");
	JNI_Class_Lib3D = env->FindClass("com/zerracsoft/Lib3D/JNIinterface");


	JNI_Env = env;

    JNI_nativeCrashed = env->GetStaticMethodID(JNI_Class_bck, "nativeCrashed", "()V");
	JNI_logCallStack = env->GetStaticMethodID(JNI_Class_Lib3D, "logCallStack", "()V");

	// Try to catch crashes...
	struct sigaction handler;
	memset(&handler, 0, sizeof(handler));
	handler.sa_sigaction = android_sigaction;
	handler.sa_flags = SA_RESETHAND;
#define CATCHSIG(X) sigaction(X, &handler, &old_sa[X])
	CATCHSIG(SIGILL);
	CATCHSIG(SIGABRT);
	CATCHSIG(SIGBUS);
	CATCHSIG(SIGFPE);
	CATCHSIG(SIGSEGV);
	CATCHSIG(SIGSTKFLT);
	CATCHSIG(SIGPIPE);

    return JNI_VERSION_1_2;
}

}


// log Callstack
void LogJavaCallStack()
{
	JNI_Env->CallStaticVoidMethod(JNI_Class_Lib3D, JNI_logCallStack);
}

