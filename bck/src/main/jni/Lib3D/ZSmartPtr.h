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

#ifndef __SMART_PTR__
#define __SMART_PTR__
#include "release_settings.h"

template <class P> class ZSmartPtr;
class ZSmartObject;

class ZSmartObject
{
//	friend template class ZSmartPtr;

public:
	ZSmartObject()	{ m_RefCount=0; }
	virtual ~ZSmartObject();

	int GetRefCount()	{return m_RefCount;}

//protected:
	int m_RefCount;
};

template <class P> class ZSmartPtr
{
public :
	ZSmartPtr ()								{ m_Ptr = 0;}
	ZSmartPtr (P* Ptr)							{ m_Ptr = Ptr; m_Ptr->m_RefCount++; }
	ZSmartPtr (const ZSmartPtr<P>& sPtr)		{ m_Ptr = sPtr.m_Ptr; m_Ptr->m_RefCount++; }
	~ZSmartPtr ()								{ Release(); }

	P*					operator -> () const	{ return m_Ptr; }
	P&					operator * () const		{ return *m_Ptr; }
						operator P* () const	{ return m_Ptr; }

	void* GetVoid() const	{ return (void*)m_Ptr; }

	ZSmartPtr<P>&		operator = (const ZSmartPtr<P>& SmartPtr);
	ZSmartPtr<P>&		operator = (const P* SmartPtr);
	bool				operator == (const ZSmartPtr<P>& SmartPtr)	{return (m_Ptr==SmartPtr.m_Ptr);}
private :
	void				Release ();

	P*		m_Ptr;
};

// ------------------------------------------------------------------------------------------------------------------

template <class P> ZSmartPtr<P>& ZSmartPtr<P>::operator = (const ZSmartPtr<P>& sPtr)
{
	if (this != &sPtr)
	{
		Release();
		m_Ptr	= sPtr.m_Ptr;
		m_Ptr->m_RefCount++;
	}
	return *this;
}

template <class P> ZSmartPtr<P>& ZSmartPtr<P>::operator = (const P* sPtr)
{
	if (m_Ptr != sPtr)
	{
		Release();
		if (sPtr)
		{
			m_Ptr	= (P*) sPtr;
			m_Ptr->m_RefCount++;
		}
	}
	return *this;
}

template <class P> void ZSmartPtr<P>::Release ()
{
	if (m_Ptr)
	{
		m_Ptr->m_RefCount--;
		if (m_Ptr->m_RefCount == 0)
		{
			delete(m_Ptr);		m_Ptr = 0;
		}
	}
}

// ------------------------------------------------------------------------------------------------------------------
/*
template <class T> class ZSmartArray
{
public:
	ZSmartArray(int size);
	~ZSmartArray();

	void 			Add(ZSmartPtr<T> elt);
	bool			Remove(ZSmartPtr<T> elt);
	void			Remove(int i);
	int				Find(ZSmartPtr<T> elt);	// -1 = not found
	void			Clear();

	int 			GetCount() {return m_Count;}			// nb of elements in array
	ZSmartPtr<T>	Get(int i);
private:
	ZSmartArray() {}	// to prevent use

	ZSmartPtr<T>*	m_Elements;
	int	m_Size;
	int m_Count;
};

template <class T> ZSmartArray<T>::ZSmartArray(int size)
{
	m_Elements = new ZSmartPtr<T>[size];
	m_Size = size;
	m_Count = 0;
}

template <class T> ZSmartArray<T>::~ZSmartArray()
{
	delete [] m_Elements;
}

template <class T> void ZSmartArray<T>::Add(ZSmartPtr<T> elt)
{
	if (m_Count==m_Size) return;
	m_Elements[m_Count++] = elt;
}

template <class T> bool ZSmartArray<T>::Remove(ZSmartPtr<T> elt)
{
	LOGI("ZSmartArray::Remove 1 count=%i",elt->GetRefCount());
	int i=Find(elt);
	if (i==-1) return false;
	LOGI("ZSmartArray::Remove 2 i=%i count=%i",i,elt->GetRefCount());
	Remove(i);
	LOGI("ZSmartArray::Remove 3 count=%i",elt->GetRefCount());
	return true;
}

template <class T> void ZSmartArray<T>::Remove(int index)
{
	LOGI("ZSmartArray::Remove 4");

	for (int i=index; i<m_Count-1; i++)
		m_Elements[i] = m_Elements[i+1];
	LOGI("ZSmartArray::Remove 5");
	m_Elements[m_Count-1] = (T*)0;
	LOGI("ZSmartArray::Remove 6");

	m_Count--;
	LOGI("ZSmartArray::Remove 7");

}

template <class T> int ZSmartArray<T>::Find(ZSmartPtr<T> elt)
{
	for (int i=0; i<m_Count; i++)
		if (m_Elements[i]==elt)
			return i;
	return -1;
}

template <class T> void ZSmartArray<T>::Clear()
{
	for (int i=0; i<m_Count; i++)
		m_Elements[i]=(T*)0;
	m_Count = 0;
}

template <class T> ZSmartPtr<T> ZSmartArray<T>::Get(int i)
{
	if (m_Elements[i]->GetRefCount()==0)
		LOGE("ZSmartArray<T>::Get m_Elements[i]->GetRefCount()=0 i=%i/%i",i,m_Count);
	return m_Elements[i];
}
*/

#endif //__SMART_PTR__
