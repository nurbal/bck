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

#ifndef __LIB3D_MUTEX__
#define __LIB3D_MUTEX__

// les mutex utilis�s par les threads de rendu et d'update pour ne pas e marcher dessus...
/*
extern pthread_mutex_t gInstancesMutex;				// rendu en cours; verrouille les instances
extern pthread_mutex_t gLightMutex;					// rendu en cours; verrouille la light
extern pthread_mutex_t gCameraMutex;				// rendu en cours; verrouille la camera
extern pthread_mutex_t gInstancesTransformerMutex;	// pr�paration des instances avant rendu (copie des transformers, alpha, etc...)
extern pthread_mutex_t gQueuedInstancesMutex;		// mise � jour de la liste des instances � rendre avant le rendu
*/


// passer par des macros permettra de loguer plus facilement en cas de soucis...
#define INSTANCES_MUTEX_LOCK 				{InstancesMutexLock(__FILE__, __LINE__);}
#define INSTANCES_MUTEX_UNLOCK 				{InstancesMutexUnlock();}
#define LIGHT_MUTEX_LOCK 					{LightMutexLock(__FILE__, __LINE__);}
#define LIGHT_MUTEX_UNLOCK					{LightMutexUnlock();}
#define CAMERA_MUTEX_LOCK 					{CameraMutexLock(__FILE__, __LINE__);}
#define CAMERA_MUTEX_UNLOCK					{CameraMutexUnlock();}
#define INSTANCES_TRANSFORMER_MUTEX_LOCK 	{InstancesTransformerMutexLock(__FILE__, __LINE__);}
#define INSTANCES_TRANSFORMER_MUTEX_UNLOCK 	{InstancesTransformerMutexUnlock();}
#define QUEUED_INSTANCES_MUTEX_LOCK 		{QueuedInstancesMutexLock(__FILE__, __LINE__);}
#define QUEUED_INSTANCES_MUTEX_UNLOCK 		{QueuedInstancesMutexUnlock();}

void InstancesMutexLock(const char* file, int line);
void InstancesMutexUnlock();
void LightMutexLock(const char* file, int line);
void LightMutexUnlock();
void CameraMutexLock(const char* file, int line);
void CameraMutexUnlock();
void InstancesTransformerMutexLock(const char* file, int line);
void InstancesTransformerMutexUnlock();
void QueuedInstancesMutexLock(const char* file, int line);
void QueuedInstancesMutexUnlock();


#define JNI_MUTEX_LOCK 				{JNIMutexLock(__FILE__, __LINE__);}
#define JNI_MUTEX_UNLOCK 			{JNIMutexUnlock();}

void JNIMutexLock(const char* file, int line);
void JNIMutexUnlock();


#endif // __LIB3D_MUTEX__
