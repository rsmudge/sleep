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
package sleep.runtime;

import java.io.*;

import sleep.taint.*;

public class WatchScalar extends Scalar
{
   protected ScriptEnvironment owner;
   protected String            name;

   public WatchScalar(String _name, ScriptEnvironment _owner)
   {
      name  = _name;
      owner = _owner;
   }

   public void flagChange(Scalar valuez)
   {
      if (owner != null && (value != null || array != null || hash != null))
      {
         owner.showDebugMessage("watch(): " + name + " = " + SleepUtils.describe(valuez));
      }
   }

   /** set the value of this scalar container to a scalar value of some type */
   public void setValue(ScalarType _value)
   {
      /** check if we're merely tainting this scalar... if we are then we can ignore it. */
      if (! (_value.getClass() == TaintedValue.class && ((TaintedValue)_value).untaint() == value) )
      {
         Scalar blah = new Scalar();
         blah.setValue(_value);
         flagChange(blah);
      }
      
      super.setValue(_value);
   }

   /** set the value of this scalar container to a scalar array */
   public void setValue(ScalarArray _array)
   {
      Scalar blah = new Scalar();
      blah.setValue(_array);
      flagChange(blah);

      super.setValue(_array);
   }

   /** set the value of this scalar container to a scalar hash */
   public void setValue(ScalarHash _hash)
   {
      Scalar blah = new Scalar();
      blah.setValue(_hash);
      flagChange(blah);

      super.setValue(_hash);
   }

   private void writeObject(ObjectOutputStream out) throws IOException
   {
       if (SleepUtils.isEmptyScalar(this))
       {
          out.writeObject(null);
       }
       else
       {
          out.writeObject(value);
       }
       out.writeObject(array);
       out.writeObject(hash);
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
   {
       value = (ScalarType)in.readObject();
       array = (ScalarArray)in.readObject();
       hash  = (ScalarHash)in.readObject();

       if (value == null && array == null && hash == null)
       {
          setValue(SleepUtils.getEmptyScalar());
       }
   }
}
