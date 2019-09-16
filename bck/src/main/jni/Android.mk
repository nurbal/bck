LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := BCK
LOCAL_SRC_FILES := \
				BCK/jni_Misc.cpp \
				BCK/jni_Level.cpp \
				BCK/jni_Link.cpp \
				BCK/jni_LinkBar.cpp \
				BCK/jni_LinkCable.cpp \
				BCK/jni_LinkJack.cpp \
				BCK/jni_Node.cpp \
				BCK/Level.cpp \
				BCK/Link.cpp \
				BCK/LinkBar.cpp \
				BCK/LinkCable.cpp \
				BCK/LinkJack.cpp \
				BCK/Node.cpp \
				BCK/P6Pool.cpp \
				BCK/P6PoolLinkBreak.cpp \
				BCK/Rails.cpp \
				Lib3D/jni_Watchdogs.cpp \
				Lib3D/lib3d_mutex.cpp \
				Lib3D/ZColor.cpp \
				Lib3D/jni_ZColor.cpp \
				Lib3D/ZSmartPtr.cpp \
				Lib3D/ZVector.cpp \
				Lib3D/jni_ZVector.cpp \
				Lib3D/ZQuaternion.cpp \
				Lib3D/jni_ZQuaternion.cpp \
				Lib3D/ZMatrix.cpp \
				Lib3D/jni_ZMatrix.cpp \
				Lib3D/ZSRT.cpp \
				Lib3D/jni_ZSRT.cpp \
				Lib3D/ZObject.cpp \
				Lib3D/jni_ZObject.cpp \
				Lib3D/ZMesh.cpp \
				Lib3D/jni_ZMesh.cpp \
				Lib3D/ZWaterRect.cpp \
				Lib3D/jni_ZWaterRect.cpp \
				Lib3D/ZPointsMesh.cpp \
				Lib3D/jni_ZPointsMesh.cpp \
				Lib3D/ZParticleSystem.cpp \
				Lib3D/ZParticleSystemNewton.cpp \
				Lib3D/jni_ZParticleSystem.cpp \
				Lib3D/ZInstance.cpp \
				Lib3D/jni_ZInstance.cpp \
				Lib3D/ZMaterial.cpp \
				Lib3D/jni_ZMaterial.cpp \
				Lib3D/ZRenderer.cpp \
				Lib3D/jni_ZRenderer.cpp \
				Lib3D/ZCamera.cpp \
				Lib3D/ZFrustumCamera.cpp \
				Lib3D/jni_ZFrustumCamera.cpp \
				Lib3D/ZOrthoCamera.cpp \
				Lib3D/jni_ZOrthoCamera.cpp \
				Lib3D/ZScene.cpp \
				Lib3D/jni_ZScene.cpp \

LOCAL_LDLIBS := -lGLESv1_CM -ldl -llog

include $(BUILD_SHARED_LIBRARY)
