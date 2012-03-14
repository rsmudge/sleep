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
package sleep.engine.types;

import sleep.runtime.ScalarType;

public class StringValue implements ScalarType
{
   protected String value;

   public StringValue(String _value)
   {
      value = _value;
   }

   public ScalarType copyValue()
   {
      return this;
   }

   /** does nothing for now... */
   private String numberOnlyString()
   {
      return value;
   }

   public int intValue()
   {
      try
      {
         return Integer.parseInt(numberOnlyString());
      }
      catch (Exception ex)
      {
         return 0;
      }
   }

   public long longValue()
   {
      try
      {
         return Long.parseLong(numberOnlyString());
      }
      catch (Exception ex)
      {
         return 0L;
      }
   }

   public double doubleValue()
   {
      try
      {
         return Double.parseDouble(numberOnlyString());
      }
      catch (Exception ex)
      {
         return 0.0;
      }
   }

   public String toString()
   {
      return value;
   }

   public Object objectValue()
   {
      return value;
   }

   public Class getType() { return this.getClass(); }
}
