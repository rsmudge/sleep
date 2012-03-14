/*
   SLEEP - Simple Language for Environment Extension Purposes
 .-------------------------------.
 | sleep.bridges.DefaultVariable |____________________________________________
 |                                                                            |
   Author: Raphael Mudge (rsmudge@mtu.edu)
           http://www.csl.mtu.edu/~rsmudge/
 
   Description: A default variable class.  Ain't nothin but a party.
 
   Documentation:
 
   * This software is distributed under the artistic license, see license.txt
     for more information. *
 
 |____________________________________________________________________________|
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
