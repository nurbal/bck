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

#ifndef __ZLIST__
#define __ZLIST__

//#include "log.h"
#include <android/log.h>
#include "release_settings.h"

template <class T> class ZList;
template <class T> class ZListIterator;

template <class T> struct TZListNode
{ 
	T			m_Element;
	TZListNode*	m_Previous;
	TZListNode*	m_Next; 
};

template <class T> class ZList
{
public :
	ZList();
	~ZList();

	T		Add (T Element);					//!< Ajoute un element � la liste (en fin de liste)
	T		AddFirst (T Element);				//!< Ajoute un element au d�but de la liste
	
	bool	IsInList (T Element) const;			//!< L'element est-il dans la liste ?

//	void	Destroy ();							//!< Destroy all elements.
	//void	Destroy (int i);					//!< Destroy the i-element.
//	void	Destroy (T Element);				//!< Destroy one element.
//	void	DestroyLast ();						//!< Destroy the last element.

	void	Unlink ();							//!< D�tache tous les elements de la liste. Penser � faire un delete sur les elements d�tach�s !
	bool	Unlink (T Element);					//!< D�tache un element de la liste. Penser � faire un delete sur l'element d�tach� !
	void	UnlinkLast ();						//!< D�tache le dernier element de la liste. Penser � faire un delete sur l'element d�tach� !

	int	GetNbElements () const			{ return m_NbElements; }
	bool	IsEmpty() const				{ return 0==m_NbElements; }
	T		Get (int i) const;
	T		GetFirst () const				{ if (m_First)		return (m_First->m_Element);	return 0; }
	T		GetLast () const				{ if (m_Last)		return (m_Last->m_Element);		return 0; }

	ZListIterator<T> Begin () const		{ return ZListIterator<T>(this); }

	typedef ZListIterator<T>	Iterator;

private :
	template <class U> friend class ZListIterator;

	TZListNode<T>*	m_First;			//!< Premier element de la liste
	TZListNode<T>*	m_Last;				//!< Dernier element de la liste
	int			m_NbElements;		//!< Nombre d'elements
};

template <class T> class ZListIterator
{
public :
    ZListIterator ();
	ZListIterator (const ZList<T>* List);

	ZListIterator&		Next ();							//!< Increment the position in the list.
	ZListIterator&		Previous ();						//!< Decrement the position in the list.
	bool				IsEnd ()							{ return (m_Current == 0); }
	T&					Get	()								{ return m_Current->m_Element; }
	T*					GetNext ();							//!< Get the next in the list, don't change the position of this. returns null
	T*					GetPrevious ();						//!< Get the previous in the list, don't change the position of this. returns null

	ZListIterator&		MoveForward ();						//!< Swaps the node with its next in the list. Iterator will be moved with the node.
	ZListIterator&		MoveBackward ();					//!< Swaps the node with its previous in the list. Iterator will be moved with the node.


	ZListIterator<T>&	operator = (ZList<T>& List);
						operator T ()						{ return m_Current->m_Element; }
	T					operator * ()						{ return m_Current->m_Element; }

private :
	friend class ZList<T>;

	ZList<T>*		m_List;
	TZListNode<T>*	m_Current;
};

//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------

template <class T> ZList<T>::ZList()
{
	m_First			= 0;
	m_Last			= 0;
	m_NbElements	= 0;
}

template <class T> ZList<T>::~ZList()
{
	//Destroy();
	Unlink();
}

//------------------------------------------------------------------------------
//------------------------------------------------------------------------------

template <class T> T ZList<T>::Add (T Element)
{
//	LOGD("ZList::Add this=#%X elt=#%X size=%d",(int)(void*)this,(int)(void*)Element,m_NbElements);
	if (!Element)	return 0;			// Rien � ajouter !

	TZListNode<T> *node = new TZListNode<T>;
	node->m_Element	= Element;
	node->m_Next	= 0;
	node->m_Previous	= 0;

	if (m_Last==0)
	{
		m_First = m_Last = node;
	}
	else
	{
		m_Last->m_Next	= node;
		node->m_Previous	= m_Last;
		m_Last			= node;
	}
	m_NbElements++;

	return Element;
}

template <class T> T ZList<T>::AddFirst (T Element)
{
//	LOGD("ZList::AddFirst this=#%X elt=#%X size=%d",(int)(void*)this,(int)(void*)Element,m_NbElements);
	if (!Element)	return 0;			// Rien � ajouter !

	TZListNode<T> *node = new TZListNode<T>;
	node->m_Element	= Element;
	node->m_Next	= m_First;
	if (m_First) m_First->m_Previous = node;

	if (m_Last==0) m_Last = node;

	m_First = node;
	m_NbElements++;

	return Element;
}

template <class T> bool ZList<T>::IsInList (T Element) const
{
	if (!m_First)		return false;		// Liste vide
	if (!Element)		return false;		// element invalide

	TZListNode<T> *node = m_First;
	while(node)
	{
		if (Element==node->m_Element)	return true;	// Trouv� !
		node = node->m_Next;
	}

	return false;
}

//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
/*
template <class T> void	ZList<T>::Destroy ()
{
	TZListNode<T> *index, *next;
	index = m_First;

	while(index!=0)
	{
		next = index->m_Next;
		delete (index->m_Element);
		delete (index);
		index = next;
	}

	m_First			= 0;
	m_Last			= 0;
	m_NbElements	= 0;
}

template <class T> void	ZList<T>::Destroy (T Element)
{
	if (m_First==0)		return;		// rien � effacer !
	if (Element==0)		return;

	TZListNode<T> *node=m_First, *prev=0;

	while(node)
	{
		if (Element==node->m_Element)
		{
			if (node->m_Next!=0)
			{
				if (node==m_First)	m_First 		= node->m_Next;
				else				prev->m_Next	= node->m_Next;
			}
			else
			{
				if (node==m_First)	{ m_First = m_Last = 0; }
				else				{ prev->m_Next = 0; m_Last = prev; }
			}

			delete(node->m_Element);
			delete(node);
			m_NbElements--;
			break;
		}

		prev	= node;
		node	= node->m_Next;
	}
}

template <class T> void	ZList<T>::DestroyLast ()
{
	if (m_Last)
		Destroy(m_Last->m_Element);
}
*/
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------

template <class T>  void ZList<T>::Unlink ()
{
	TZListNode<T> *index, *next;
	index = m_First;

	while(index!=0)
	{
		next = index->m_Next;
		delete (index);
		index = next;
	}

	m_First			= 0;
	m_Last			= 0;
	m_NbElements	= 0;
}

template <class T> bool ZList<T>::Unlink (T Element)
{
//	LOGD("ZList::Unlink this=#%X elt=#%X size=%d",(int)(void*)this,(int)(void*)Element,m_NbElements);
	TZListNode<T> *node = m_First;

	// Trouve l'element
	while(node)
	{
		if (Element==node->m_Element)	break;
		node		= node->m_Next;
	}

	if (!node)
	{
		return	false;
		//LOGE("ZList::Unlink(T) element not found in list!");
		__android_log_print(ANDROID_LOG_ERROR,"ZList::Unlink(T)","element not found in list!");
	}
	TZListNode<T> *previous = node->m_Previous;
	TZListNode<T> *next = node->m_Next;

	if (node==m_First) m_First = next;
	if (node==m_Last) m_Last = previous;
	if (next) next->m_Previous = previous;
	if (previous) previous->m_Next = next;

	delete(node);
	m_NbElements--;

	return true;
}

template <class T>  void ZList<T>::UnlinkLast ()
{
	if (m_Last)
		Unlink(m_Last->m_Element);
}


//------------------------------------------------------------------------------
//------------------------------------------------------------------------------

template <class T> T ZList<T>::Get (int i) const
{
	if (m_First==0) 	return 0;
	if (i>=m_NbElements)	return 0;

	TZListNode<T> *node	= m_First;
	while(i>0) { node=node->m_Next; --i; }

	return node->m_Element;
}

//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------

template <class T> ZListIterator<T>::ZListIterator ()
{
	m_List		= 0;
	m_Current	= 0;
}

template <class T> ZListIterator<T>::ZListIterator (const ZList<T>* List)
{
    m_List		= (ZList<T>*)List;
	m_Current	= m_List->m_First;
}

template <class T> ZListIterator<T>& ZListIterator<T>::Next ()
{
	if (m_Current) m_Current = m_Current->m_Next;;
	return *this;
}

template <class T> ZListIterator<T>& ZListIterator<T>::Previous ()
{
	if (m_Current) m_Current = m_Current->m_Previous;
	return *this;
}

template <class T> T* ZListIterator<T>::GetNext ()
{
	if (m_Current && m_Current->m_Next)
		return &(m_Current->m_Next->m_Element);
	return 0;
}

template <class T> T* ZListIterator<T>::GetPrevious ()
{
	if (m_Current && m_Current->m_Previous)
		return &(m_Current->m_Previous->m_Element);
	return 0;
}

template <class T> ZListIterator<T>& ZListIterator<T>::MoveForward ()
{
	if (m_Current && m_Current->m_Next)
	{
		TZListNode<T> *other = m_Current->m_Next;
		other->m_Previous = m_Current->m_Previous;
		m_Current->m_Next = other->m_Next;
		m_Current->m_Previous = other;
		other->m_Next = m_Current;
		if (!m_Current->m_Next)
			m_List->m_Last = m_Current;
		else
			m_Current->m_Next->m_Previous = m_Current;
		if (!other->m_Previous)
			m_List->m_First = other;
		else
			other->m_Previous->m_Next = other;
	}
	return *this;
}

template <class T> ZListIterator<T>& ZListIterator<T>::MoveBackward ()
{
	if (m_Current && m_Current->m_Previous)
	{
		TZListNode<T> *other = m_Current->m_Previous;
		other->m_Next = m_Current->m_Next;
		m_Current->m_Previous = other->m_Previous;
		m_Current->m_Next = other;
		other->m_Previous = m_Current;
		if (!m_Current->m_Previous)
			m_List->m_First = m_Current;
		else
			m_Current->m_Previous->m_Next = m_Current;
		if (!other->m_Next)
			m_List->m_Last = other;
		else
			other->m_Next->m_Previous = other;
	}
	return *this;
}


template <class T>ZListIterator<T>& ZListIterator<T>::operator = (ZList<T>& List)
{
	m_List		= &List;
	m_Current	= List.m_First;
	return *this;
}

//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------

#endif // __ZLIST__
