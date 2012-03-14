package sleep.engine.types;

import java.io.Serializable;
import java.util.*;

import sleep.runtime.*;

public class MyLinkedList extends AbstractSequentialList implements Cloneable, Serializable, List
{
   private class MyListIterator implements ListIterator, Serializable
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

      public boolean hasNext()
      {
         return index != size;
      }

      public boolean hasPrevious()
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

   private transient int size = 0;
   private transient ListEntry header;

   /* fields used by sublists */
   private transient MyLinkedList parentList;

   public int size()
   {
      return size;
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

   public List subList(int beginAt, int endAt)
   { 
      checkSafety();

      ListEntry begin = getAt(beginAt).next();  /* included */
      ListEntry end = getAt(endAt); /* not included */

      /* we want each sublist to consist of a direct view into the parent... operations on other
         sublists will fail if the parent is changed through some other sublist, this makes things
         efficient and safe */
        
      while (begin instanceof ListEntryWrapper)
      {
         begin = ((ListEntryWrapper)begin).parent;
      }

      while (end instanceof ListEntryWrapper)
      {
         end = ((ListEntryWrapper)end).parent;
      }

      return new MyLinkedList(parentList == null ? this : parentList, begin, end, (endAt - beginAt));
   }

   /** add an object to the list */
   public boolean add(Object o)
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

   public ListIterator listIterator(int index)
   {
      return new MyListIterator(getAt(index), index);
   }

   // code for the ListEntry //

   private interface ListEntry extends Serializable
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

   private class SublistHeaderEntry implements ListEntry
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

   private class ListEntryWrapper implements ListEntry
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

   private class NormalListEntry implements ListEntry
   {
      public Object element;
      public ListEntry previous;
      public ListEntry next;

      public NormalListEntry(Object _element, ListEntry _previous, ListEntry _next)
      {
         element  = _element;
         previous = _previous;
         next     = _next;

         if (previous != null)
         {
            previous.setNext(this);
         }

         if (next != null)
         {
            next.setPrevious(this);
         }
      }

      public void setNext(ListEntry entry) 
      {
         next = entry;
      }

      public void setPrevious(ListEntry entry)
      {
         previous = entry;
      }

      public ListEntry next()
      {
         return next;
      }

      public ListEntry previous()
      {
         return previous;
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
         element = o;
      }

      public Object element()
      {
         return element;
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
         ListEntry temp = new NormalListEntry(o, this, this.next);

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
    private synchronized void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException 
    {
        /* grab any fields I missed */
	out.defaultWriteObject();
      
        /* write out the size */
        out.writeInt(size);

        /* blah blah blah */
        Iterator i = iterator();
        while (i.hasNext())
        {
           out.writeObject(i.next());
        }
    }

    /* reconstitute this list from the stream */
    private synchronized void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException 
    {
        /* read any fields I missed */
	in.defaultReadObject();
      
        /* read in the size */
        int size = in.readInt();

        /* create the header */
        header = new NormalListEntry(SleepUtils.getScalar("[:HEADER:]"), null, null);
        header.setNext(header);
        header.setPrevious(header);

        /* populate the list */
        for (int x = 0; x < size; x++)
        {
           add(in.readObject());
        }
    }
}
