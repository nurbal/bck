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

#include "lib3d_mutex.h"
#include <pthread.h>
#include <android/log.h>
#include <unistd.h>


// on encapsule les mutexes dans une classe avec les infos pour le debug, timeout, etc.
class Lib3DMutex
{
protected:
	pthread_mutexattr_t		m_MutexAttr;
	pthread_mutex_t			m_Mutex;
	const char* 			m_Name;

	const char* 			m_LockFile;
	int						m_LockLine;
	pthread_t 				m_LockThread;

public:
	Lib3DMutex(const char* name)
	{
		pthread_mutexattr_init(&m_MutexAttr);
		pthread_mutex_init(&m_Mutex,&m_MutexAttr);
		m_Name = name;
	}

	void Lock(const char* file, int line)
	{
		clock_t startTime,currentTime;
		bool warnedDeadlock = false;
		bool warnedLocked = false;
		startTime = clock();
		while (0!=pthread_mutex_trylock(&m_Mutex))
		{
			if (!warnedLocked)
				__android_log_print(ANDROID_LOG_INFO,"Lib3DMutex","Mutex \"%s\" locked. trying from %s:%d #%X - locked from %s:%d #%X",m_Name,file,line,(unsigned int)pthread_self(),m_LockFile,m_LockLine,(unsigned int)m_LockThread);
			warnedLocked=true;

			usleep(1000);
			currentTime = clock();
			if (!warnedDeadlock && (currentTime-startTime)>CLOCKS_PER_SEC/10)
			{
				__android_log_print(ANDROID_LOG_WARN,"Lib3DMutex","Mutex \"%s\" potential deadlock. trying from %s:%d #%X - locked from %s:%d #%X",m_Name,file,line,(unsigned int)pthread_self(),m_LockFile,m_LockLine,(unsigned int)m_LockThread);
				warnedDeadlock = true;
			}
		}
		m_LockFile = file;
		m_LockLine = line;
		m_LockThread = pthread_self();
		if (warnedDeadlock || warnedLocked)
		{
			currentTime = clock();
			__android_log_print(ANDROID_LOG_WARN,"Lib3DMutex","Mutex \"%s\" lock took %f sec.",m_Name,(float)(currentTime-startTime)/(float)CLOCKS_PER_SEC);
		}
	}

	void Unlock()
	{
		pthread_mutex_unlock(&m_Mutex);
	}

};



Lib3DMutex gInstancesMutex("Instances");
Lib3DMutex gLightMutex("Light");
Lib3DMutex gCameraMutex("Camera");
Lib3DMutex gInstancesTransformerMutex("InstancesTransformer");
Lib3DMutex gQueuedInstancesMutex("QueuedInstances");




void InstancesMutexLock(const char* file, int line)					{gInstancesMutex.Lock(file,line);}
void InstancesMutexUnlock()											{gInstancesMutex.Unlock();}
void LightMutexLock(const char* file, int line)						{gLightMutex.Lock(file,line);}
void LightMutexUnlock()												{gLightMutex.Unlock();}
void CameraMutexLock(const char* file, int line)					{gCameraMutex.Lock(file,line);}
void CameraMutexUnlock()											{gCameraMutex.Unlock();}
void InstancesTransformerMutexLock(const char* file, int line) 		{gInstancesTransformerMutex.Lock(file,line);}
void InstancesTransformerMutexUnlock() 								{gInstancesTransformerMutex.Unlock();}
void QueuedInstancesMutexLock(const char* file, int line)			{gQueuedInstancesMutex.Lock(file,line);}
void QueuedInstancesMutexUnlock()									{gQueuedInstancesMutex.Unlock();}


Lib3DMutex gJNIMutex("JNI");
void JNIMutexLock(const char* file, int line)						{gJNIMutex.Lock(file,line);}
void JNIMutexUnlock()												{gJNIMutex.Unlock();}
