/*
   SLEEP - Simple Language for Environment Extension Purposes
 .------------------------------.
 | sleep.interfaces.Environment |_____________________________________________
 |                                                                            |
   Author: Raphael Mudge (rsmudge@mtu.edu)
           http://www.csl.mtu.edu/~rsmudge/
 
   Description: An interface for a class that defines a environment for user
     defined functions.   

   Documentation: 
 
   * This software is distributed under the artistic license, see license.txt
     for more information. *
 
 |____________________________________________________________________________|
 */

package sleep.interfaces;
 
import java.util.*;

import sleep.runtime.ScriptInstance;
import sleep.engine.Block;

import sleep.engine.atoms.Check;

/**
 * <p>Predicate environments are similar to normal keyword environments except instead of binding commands to an identifier 
 * they are bound to a predicate condition.</p>
 * 
 * <p>In general the sleep syntax for declaring a predicate environment is:</p>
 * 
 * <code>keyword (condition) { commands; }</code>
 *  
 * <p>Script predicate environment bridge keywords should be registered with the script parser before any scripts are 
 * loaded.  This can be accomplished as follows:</p>
 * 
 * <code>ParserConfig.addKeyword("keyword");</code>
 * 
 * <p>To install a new predicate environment into the script environment:</p>
 * 
 * <pre>
 * ScriptInstance script;              // assume
 * Environment    myEnvironmentBridge; // assume
 * 
 * Hashtable environment = script.getScriptEnvironment().getEnvironment();
 * environment.put("keyword", myEnvironmentBridge);
 * </pre>
 * 
 * <p>Predicate environments are a powerful way to create environments that are triggered selectively.  Predicate 
 * environments can also be used to add new constructs to the sleep language such as an unless (comparison) { } 
 * construct.</p>
 * 
 * @see sleep.interfaces.Environment
 * @see sleep.parser.ParserConfig#addKeyword(String)
 */
public interface PredicateEnvironment
{
   /**
    * binds a function (functionName) of a certain type (typeKeyword) to the defined functionBody.
    *
    * @param typeKeyword the keyword for the function. (i.e. sub)
    * @param condition the condition under which this can / should be executed.
    * @param functionBody the compiled body of the function (i.e. code to add 2 numbers)
    */
   public abstract void bindPredicate(ScriptInstance si, String typeKeyword, Check condition, Block functionBody);
}
