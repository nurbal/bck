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

#include "helpers.h"
#include "log.h"
#include "../BCK/jni_Misc.h"

#include "ZSmartPtr.h"

ZSmartObject::~ZSmartObject()
{
	if (m_RefCount)
	{
		LOGE("ZSmartObject::~SmartObject whith RefCount=%i (java call stack follows)",m_RefCount);
		LogJavaCallStack();
	}
}
