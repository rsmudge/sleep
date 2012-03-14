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
import sleep.runtime.ScriptInstance;
import sleep.interfaces.Variable;
import sleep.interfaces.Loadable;

import java.util.Hashtable;

public class DefaultVariable implements Variable, Loadable
{
    protected Hashtable values = new Hashtable();

    public boolean scalarExists(String key)
    {
        return values.containsKey(key);
    }

    public Scalar getScalar(String key)
    {
        return (Scalar)values.get(key);
    }

    public Scalar putScalar(String key, Scalar value)
    {
        return (Scalar)values.put(key, value);
    }

    public void removeScalar(String key)
    {
        values.remove(key);
    }

    public Variable createLocalVariableContainer()
    {
        return new DefaultVariable();
    }

    public Variable createInternalVariableContainer()
    {
        return new DefaultVariable();
    }

    public void scriptLoaded (ScriptInstance script)
    {
    }

    public void scriptUnloaded (ScriptInstance script)
    {
    }

    public DefaultVariable()
    {

    }
}
