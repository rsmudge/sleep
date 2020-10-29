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

public class ObjectValue : ScalarType
{
   protected Object value;

   public ObjectValue(Object _value)
   {
      value = _value;
   }

   public ScalarType copyValue()
   {
      return this;
   }

   public int intValue()
   {
      String str = toString();

      if (str.length() == 0) { return 0; }
      if (str.equals("true")) { return 1; }
      if (str.equals("false")) { return 0; }

      try
      {
         return Integer.decode(str).intValue();
      }
      catch (Exception ex)
      {
         return 0;
      }
   }

   public long longValue()
   {
      String str = toString();

      if (str.length() == 0) { return 0L; }
      if (str.equals("true")) { return 1L; }
      if (str.equals("false")) { return 0L; }

      try
      {
         return java.lang.Long.decode(str).longValue();
      }
      catch (java.lang.Exception ex)
      {
         return 0L;
      }
   }

   public double doubleValue()
   {
      String str = toString();

      if (str.length() == 0) { return 0.0; }
      if (str.equals("true")) { return 1.0; }
      if (str.equals("false")) { return 0.0; }

      try
      {
         return Double.parseDouble(str);
      }
      catch (Exception ex)
      {
         return 0;
      }
   }

   public String toString()
   {
      return value.toString();
   }

   public Object objectValue()
   {
      return value;
   }

   public Type getType() {
      return this.value.GetType();
   }

}
}