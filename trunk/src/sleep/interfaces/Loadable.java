/*
   SLEEP - Simple Language for Environment Extension Purposes
 .---------------------------.
 | sleep.interfaces.Loadable |________________________________________________
 |                                                                            |
   Author: Raphael Mudge (rsmudge@mtu.edu)
           http://www.csl.mtu.edu/~rsmudge/
 
   Description: An interface for a class that knows how to load itself into
     the SLEEP environment.  Basically used in a bridge that wants to add all
     of its Functions, Predicates, and Operators itself rather than making the
     poor programmer do it manually.
 
   Documentation:
 
   * This software is distributed under the artistic license, see license.txt
     for more information. *
 
 |____________________________________________________________________________|
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
