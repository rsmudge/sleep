/*
 * Copyright 2002-2020 Raphael Mudge
 * Copyright 2020 Sebastian Ritter
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 *    conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 *    of conditions and the following disclaimer in the documentation and/or other materials
 *    provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without specific prior
 *    written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 
using System;
using java = biz.ritter.javapi;

using  sleep.runtime;

namespace sleep.engine.types{
[Serializable]
public class MyLinkedList : java.util.AbstractSequentialList<Object> , java.lang.Cloneable, java.io.Serializable, java.util.List<Object>
{
   [Serializable]
   private class MyListIterator : java.util.ListIterator<Object>, java.io.Serializable
   {
      protected int       index; 
      protected int       start;
      protected ListEntry current;
      protected int       modCountCheck = modCount;

      public void checkSafety()
      {
         if (modCountCheck != modCount)
         {
            throw new ConcurrentModificationException("@array changed during iteration");
         }
      }

      public MyListIterator(ListEntry entry, int index)
      {
         this.index   = index;
         this.start   = index;
         current      = entry;
      }

      public void add(Object o)
      {
         checkSafety();

         /* add the new element after the current element */
         current = current.addAfter(o);
 
         /* increment the list so that the next element returned is
            unaffected by this call */
         index++;
        
         modCountCheck++;
      }

      public bool hasNext()
      {
         return index != size;
      }

      public bool hasPrevious()
      {
         return index != 0;
      }

      public Object next()
      {
         checkSafety();
         current = current.next();
         index++;
         return current.element();
      }

      public Object previous()
      {
         checkSafety();
         current = current.previous();
         index--;
         return current.element();
      }

      public int nextIndex()
      {
         return index;
      }

      public int previousIndex()
      {
         return index - 1;
      }

      public void remove()
      {
         if (current == header)
         {
            throw new IllegalStateException("list is empty");
         }

         checkSafety();
         current = current.remove().previous();

         index--;

         modCountCheck++;
      }

      public void set(Object o)
      {
         if (current == header)
         {
            throw new IllegalStateException("list is empty");
         }

         checkSafety();
         current.setElement(o);
      }
   }

   [NonSerialized]
   private int sizeJ = 0;
   [NonSerialized]
   private ListEntry header;

   /* fields used by sublists */
   [NonSerialized]
   private MyLinkedList parentList;

   public override int size()
   {
      return sizeJ;
   }

   private MyLinkedList(MyLinkedList plist, ListEntry begin, ListEntry end, int _size)
   {
      parentList = plist;
      modCount = parentList.modCount;

      header = new SublistHeaderEntry(begin, end);
      size = _size;
   }

   public MyLinkedList()
   {
      header = new NormalListEntry(SleepUtils.getScalar("[:HEADER:]"), null, null);
      header.setNext(header);
      header.setPrevious(header);
   }   

   public java.util.List<Object> subList(int beginAt, int endAt)
   { 
      checkSafety();

      ListEntry begin = getAt(beginAt).next();  /* included */
      ListEntry end = getAt(endAt); /* not included */

      /* we want each sublist to consist of a direct view into the parent... operations on other
         sublists will fail if the parent is changed through some other sublist, this makes things
         efficient and safe */
        
      while (begin is ListEntryWrapper)
      {
         begin = ((ListEntryWrapper)begin).parent;
      }

      while (end is ListEntryWrapper)
      {
         end = ((ListEntryWrapper)end).parent;
      }

      return new MyLinkedList(parentList == null ? this : parentList, begin, end, (endAt - beginAt));
   }

   /** add an object to the list */
   public bool add(Object o)
   {
      ListEntry entry = header;
      header.previous().addAfter(o);
      return true;
   }

   /** add an object to the list at the specified index */
   public void add(int index, Object element)
   {
      ListEntry entry = getAt(index);
      entry.addAfter(element); 
   }

   /** get an object from the linked list */
   public Object get(int index)
   {
      if (index >= size)
         throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);

      return getAt(index).next().element();
   }

   /** remove an object at the specified index */
   public Object remove(int index)
   {
      if (index >= size)
         throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);

      ListEntry entry = getAt(index).next();
      Object value = entry.element();
      entry.remove();

      return value;
   }

   /** returns the entry at the specified index */
   private ListEntry getAt(int index)
   {
      if (index < 0 || index > size)
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);

      ListEntry entry = header;

      if (index == size)
      {
         return header.previous();
      }
      else if (index < (size / 2))
      {
         for (int x = 0; x < index; x++)
         {
            entry = entry.next();
         }
      }
      else
      {
         entry = entry.previous();
         for (int x = size; x > index; x--)
         {
            entry = entry.previous();
         }
      }

      return entry;
   }

   public java.util.ListIterator<Object> listIterator(int index)
   {
      return new MyListIterator(getAt(index), index);
   }

   // code for the ListEntry //

   private interface ListEntry : java.io.Serializable
   {
      public ListEntry remove();
      public ListEntry addBefore(Object o);
      public ListEntry addAfter(Object o);

      public ListEntry next();
      public ListEntry previous();

      public void setNext(ListEntry entry);
      public void setPrevious(ListEntry entry);

      public Object element();
      public void setElement(Object o);
   }

   public void checkSafety()
   {
      if (parentList != null && modCount != parentList.modCount)
      {
         throw new ConcurrentModificationException("parent @array changed after &sublist creation");
      }
   }

   private class SublistHeaderEntry : ListEntry
   {
      private ListEntry anchorLeft;
      private ListEntry anchorRight;

      public SublistHeaderEntry(ListEntry a, ListEntry b) 
      {
         anchorLeft  = a.previous();
         anchorRight = b.next();
      }

      public ListEntry remove() 
      {
         throw new UnsupportedOperationException("remove");
      }

      public ListEntry previous() 
      {
         return new ListEntryWrapper(anchorRight.previous());
      }

      public ListEntry next() 
      {
         return new ListEntryWrapper(anchorLeft.next());
      }

      public void setNext(ListEntry e)
      {
         anchorRight.setPrevious(e);
         e.setNext(anchorRight);
      }

      public void setPrevious(ListEntry e)
      {
         anchorLeft.setNext(e);
         e.setPrevious(anchorLeft);
      }

      public ListEntry addBefore(Object o)
      {
         return previous().addAfter(o);
      }

      public ListEntry addAfter(Object o)
      {
         return next().addBefore(o);
      }

      public Object element()
      {
         return SleepUtils.getScalar("[:header:]");
      }

      public void setElement(Object o)
      {
         throw new UnsupportedOperationException("setElement");
      }
   }

   private class ListEntryWrapper : ListEntry
   {
      public ListEntry parent;

      public ListEntryWrapper(ListEntry _parent)
      {
         parent = _parent;
      }

      public ListEntry remove()
      {
         checkSafety();

         ListEntry temp = parent.remove();

         size--;
         modCount++;

         if (size == 0)
         {
            return header;
         }
         else
         {
            if (parent == header.next())
            {
                header.setNext(temp);
            } 

            if (parent == header.previous())
            {
                header.setPrevious(temp);
            }
         }

         return new ListEntryWrapper(temp);
      }

      public ListEntry addBefore(Object o)
      {
         checkSafety();

         ListEntry temp = parent.addBefore(o);

         size++;
         modCount++;

         if (size == 1)
         {
            header.setNext(temp);
            header.setPrevious(temp);
         }
         else if (parent == header.next())
         {
            header.setPrevious(temp);
         }

         return new ListEntryWrapper(temp);
      }

      public ListEntry addAfter(Object o)
      {
         checkSafety();

         ListEntry temp = parent.addAfter(o);

         size++;
         modCount++;

         if (size == 1)
         {
            header.setNext(temp);
            header.setPrevious(temp);
         }
         else if (parent == header.previous())
         {
            header.setNext(temp);
         }
	
         return new ListEntryWrapper(temp);
      }

      public void setNext(ListEntry entry)
      {
         throw new UnsupportedOperationException("ListEntryWrapper::setNext");
      }

      public void setPrevious(ListEntry entry)
      {
         throw new UnsupportedOperationException("ListEntryWrapper::setPrevious");
      }

      public Object element()
      {
         return parent.element();
      }

      public void setElement(Object o)
      {
         parent.setElement(o);
      }

      public ListEntry next()
      {
         checkSafety();

         if (parent == header.next())
         {
            return new ListEntryWrapper(header);
         }

         ListEntryWrapper r = new ListEntryWrapper(parent.next());
         return r;
      }

      public ListEntry previous()
      {
         checkSafety();

         if (parent == header.previous())
         {
            return new ListEntryWrapper(header);
         }

         ListEntryWrapper r = new ListEntryWrapper(parent.previous());
         return r;
      }
   }

   private class NormalListEntry : ListEntry
   {
      public Object elementObject;
      public ListEntry previousEntry;
      public ListEntry nextEntry;

      public NormalListEntry(Object _element, ListEntry _previous, ListEntry _next)
      {
         elementObject  = _element;
         previousEntry = _previous;
         nextEntry     = _next;

         if (previousEntry != null)
         {
            previousEntry.setNext(this);
         }

         if (nextEntry != null)
         {
            nextEntry.setPrevious(this);
         }
      }

      public void setNext(ListEntry entry) 
      {
         nextEntry = entry;
      }

      public void setPrevious(ListEntry entry)
      {
         previousEntry = entry;
      }

      public ListEntry next()
      {
         return nextEntry;
      }

      public ListEntry previous()
      {
         return previousEntry;
      }

      public ListEntry remove()
      {
         ListEntry prev = previous();
         ListEntry nxt  = next();

         nxt.setPrevious(prev);
         prev.setNext(nxt);

         size--;
         modCount++;
         return nxt;
      }

      public void setElement(Object o)
      {
         elementObject = o;
      }

      public Object element()
      {
         return elementObject;
      }

      public ListEntry addBefore(Object o)
      {
         ListEntry temp = new NormalListEntry(o, this.previous, this);

         size++;
         modCount++;

         return temp;
      }

      public ListEntry addAfter(Object o)
      {
         ListEntry temp = new NormalListEntry(o, this, this.nextEntry);

         size++;
         modCount++;

         return temp;
      }

      public String toString()
      {
         StringBuffer buffer = new StringBuffer(":[" + element() + "]:");

         if (this == header)
         {
             buffer = new StringBuffer(":[HEADER]:");
         }

         ListEntry entry = this.previous();
         while (entry != header)
         {
            buffer.insert(0, "[" + entry.element() + "]-> ");
            entry = entry.previous();
         }
         
         entry = this.next();
         while (entry != header)
         {
            buffer.append(" ->[" + entry.element() + "]");
            entry = entry.next();
         }

         return buffer.toString();
      }
   }

    /* save this list to the stream */
    private void writeObject(java.io.ObjectOutputStream outJ) //throws java.io.IOException 
    {
       lock (this) {
        /* grab any fields I missed */
	outJ.defaultWriteObject();
      
        /* write out the size */
        outJ.writeInt(size);

        /* blah blah blah */
        Iterator i = iterator();
        while (i.hasNext())
        {
           outJ.writeObject(i.next());
        }
       }
    }

    /* reconstitute this list from the stream */
    private void readObject(java.io.ObjectInputStream inJ)// throws java.io.IOException, ClassNotFoundException 
    {
       lock (this) {
        /* read any fields I missed */
	inJ.defaultReadObject();
      
        /* read in the size */
        int size = inJ.readInt();

        /* create the header */
        header = new NormalListEntry(SleepUtils.getScalar("[:HEADER:]"), null, null);
        header.setNext(header);
        header.setPrevious(header);

        /* populate the list */
        for (int x = 0; x < size; x++)
        {
           add(inJ.readObject());
        }
    }
    }

    public Object clone () {
       return this.MemberwiseClone();
    }
}
}