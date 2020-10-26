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

using java.io;
using sleep.bridges.BridgeUtilities;
using sleep.runtime.ScriptEnvironment;
namespace sleep.bridges.io{


/** The buffer works as follows.  Once allocated it is open for writing.  When the scripter chooses to
    close the buffer it is then available for reading.  The second time it is closed all of its resources
    are deallocated. */
public class BufferObject : IOObject
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
      base.close();

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
}