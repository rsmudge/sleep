/* 
 * Copyright (C) 2002-2012 Raphael Mudge (rsmudge@gmail.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package sleep.bridges.io;

import java.io.*;
import sleep.bridges.BridgeUtilities;
import sleep.runtime.ScriptEnvironment;

/** The buffer works as follows.  Once allocated it is open for writing.  When the scripter chooses to
    close the buffer it is then available for reading.  The second time it is closed all of its resources
    are deallocated. */
public class BufferObject extends IOObject
{
   /** The writeable source for this IO object */
   protected ByteArrayOutputStream source;

   /** The readable source for this IO object */
   protected ByteArrayInputStream  readme;

   /** returns the stream referenced by this IOObject */
   public Object getSource()
   {
      return source;
   }

   /** handles our closing semantices i.e. first time it is called the writeable portion is opened
       up for reading and the second time all resources are deallocated */
   public void close()
   {   
      super.close();

      if (readme != null)
      {
         readme = null;
      }

      if (source != null)
      {
         readme = new ByteArrayInputStream(source.toByteArray());
         openRead(readme);
         source = null;
      }
   }

   /** allocates a writeable buffer with the specified initial capacity */
   public void allocate(int initialSize)
   {
      source = new ByteArrayOutputStream(initialSize);
      openWrite(source);
   }
}
