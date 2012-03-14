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
 
import java.util.*;
import java.io.*;

import sleep.engine.*;
import sleep.interfaces.*;
import sleep.runtime.*;

public class DefaultEnvironment implements Loadable, Environment
{
    public void scriptUnloaded (ScriptInstance si)
    {
    }

    public void scriptLoaded (ScriptInstance si)
    {
        Hashtable env = si.getScriptEnvironment().getEnvironment(); 
        env.put("sub",    this);
        env.put("inline", this);
    }

    public void bindFunction(ScriptInstance si, String type, String name, Block code)
    {
        Hashtable env = si.getScriptEnvironment().getEnvironment(); 

        if (type.equals("sub"))
        {
           env.put("&"+name, new SleepClosure(si, code));
        }
        else if (type.equals("inline"))
        {
           env.put("^&"+name, code); /* add an inline function, very harmless */
        }
    }
}
