/*
   SLEEP - Simple Language for Environment Extension Purposes
 .---------------------------.
 | sleep.interfaces.Variable |________________________________________________
 |                                                                            |
   Author: Raphael Mudge (rsmudge@mtu.edu)
           http://www.csl.mtu.edu/~rsmudge/
 
   Description: An interface to allow management of scalars itself.  Sleep only
     allows one variable interface to be used per session.  But this is a good 
     way to create global variables and such in Sleep.
 
   Documentation:
 
   * This software is distributed under the artistic license, see license.txt
     for more information. *
 
 |____________________________________________________________________________|
 */

package sleep.interfaces;
 
import sleep.runtime.Scalar;
import sleep.runtime.ScriptInstance;

/**
 * <p>A variable bridge is a container for storing scalars.  A variable bridge is nothing more than a container.  It is 
 * possible to use a new variable container to alter how scalars are stored and accessed.  All scalars, scalar arrays, and 
 * scalar hashes are stored using this system. </p>
 *  
 * <p>A Variable bridge is installed by creating a new script variable manager with the new variable bridge.   The variable 
 * manager is then installed into a given script.</p>
 * 
 * <pre>
 * ScriptVariables variableManager = new ScriptVariable(new MyVariable());
 * script.setScriptVariables(variableManager);
 * </pre>
 * 
 * <p>Sleep scripts can share variables by using the same instance of ScriptVariables.  A Variable bridge can be used to 
 * create built in variables.  Every time a certain scalar is accessed the bridge might call a method and return the value 
 * of the method as the value of the accessed scalar.</p>
 * 
 */
public interface Variable extends java.io.Serializable
{
    /** true if a scalar named key exists in this variable environment */
    public boolean    scalarExists(String key); 

    /** returns the specified scalar, if scalarExists says it is in the environment, this method has to return a scalar */
    public Scalar     getScalar(String key);

    /** put a scalar into this variable environment */
    public Scalar     putScalar(String key, Scalar value);
 
    /** remove a scalar from this variable environment */
    public void       removeScalar(String key);

    /** returns which variable environment is used to temporarily store local variables.  */
    public Variable createLocalVariableContainer();

    /** returns which variable environment is used to store non-global / non-local variables.  this is also used to create the global scope for a forked script environment. */
    public Variable createInternalVariableContainer();
}
