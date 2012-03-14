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
package sleep.interfaces;

import sleep.runtime.Scalar; 
import sleep.runtime.ScriptInstance;

/**
 * <p>A loadable bridge is used to perform actions on scripts when they are loaded and unloaded.  Loadable bridges by 
 * themselves do not add anything to the sleep language at all.  In conjunction with a ScriptLoader loadable bridges make 
 * it easy to process the environment of scripts as they are loaded and unloaded.</p>
 * 
 * <p>A loadable bridge is installed into the language by adding it to a script loader class. </p>
 * 
 * <p>An example of a loadable bridge in action:</p>
 * 
 * <pre>
 * public class MyBridge implements Loadable
 * {
 *    public void scriptLoaded(ScriptInstance script)
 *    {
 *       Hashtable environment = script.getScriptEnvironment().getEnvironment();
 *       environment.put("&function",  new MyFunction());
 *       environment.put("-predicate", new MyPredicate());
 *    }
 * 
 *    public void scriptUnloaded(ScriptInstance script)
 *    {
 *    }
 * 
 *    private static class MyFunction implements Function
 *    {
 *       public Scalar evaluate(String name, ScriptInstance si, Stack args)
 *       {
 *           // code for MyFunction
 *       }
 *    }
 * 
 *    private static class MyPredicate implements Predicate
 *    {
 *       public boolean decide(String name, ScriptInstance si, Stack args)
 *       {
 *           // code for MyPredicate
 *       }
 *    }
 * }
 * </pre>
 * 
 * <p>An example of adding the above loadable bridge to a script loader:</p>
 * 
 * <pre>
 * ScriptLoader loader = new ScriptLoader()
 * loader.addSpecificBridge(new MyBridge());
 * </pre>
 * 
 * <p>Bottom line: Loadable bridges ARE used to install other bridges into a script environment.  Using a loadable bridge 
 * is the easiest way to make sure actions are always performed on a script as it loads.  Loadable bridges in conjunction 
 * with a script loader are used to perform cleanup actions when a script is unloaded.</p>
 * 
 * @see sleep.runtime.ScriptLoader
 */
public interface Loadable
{
    /** called when a script is loaded */
    public void scriptLoaded (ScriptInstance script);

    /** called when a script is unloaded */
    public void scriptUnloaded (ScriptInstance script);
}
