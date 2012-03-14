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
package sleep.bridges;

import sleep.runtime.Scalar;

/** <p>Arguments passed to functions with the form <code>key =&gt; expression</code> are available via
  * the KeyValuePair object.  The following is the implementation of the built-in function 
  * <code>&hash(key => "value", key2 => 3, ...)</code>:</p>
  *
  * <pre> class hash implements Function
  * {
  *    public Scalar evaluate(String n, ScriptInstance si, Stack arguments)
  *    {
  *       Scalar value = SleepUtils.getHashScalar();
  *
  *       while (!arguments.isEmpty())
  *       {
  *          <b>KeyValuePair kvp = BridgeUtilities.getKeyValuePair(arguments);</b>
  *
  *          Scalar blah = value.getHash().getAt(kvp.getKey());
  *          blah.setValue(kvp.getValue());
  *       }
  *
  *       return value;
  *    }
  * }</pre>
  *
  * @see sleep.bridges.BridgeUtilities
  */
public class KeyValuePair
{
   /** the key scalar */
   protected Scalar key; 

   /** the value scalar */
   protected Scalar value; 

   /** Instantiates a key/value pair */
   public KeyValuePair(Scalar _key, Scalar _value)
   {
      key   = _key;
      value = _value;
   }

   /** Obtain the key portion of this pair */
   public Scalar getKey() { return key; }

   /** Obtain the value portion of this pair */
   public Scalar getValue() { return value; }

   /** Return a string representation of this key/value pair */
   public String toString()
   {
      return key.toString() + "=" + value.toString();
   }
}

