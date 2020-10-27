/*
 * Copyright 2002-2020 Raphael Mudge
 * Copyright 2020 Sebastian Ritter
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 *    conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 *    of conditions and the following disclaimer in the documentation and/or other materials
 *    provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without specific prior
 *    written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
using System;
using java = biz.ritter.javapi;

using  sleep.runtime; 
using  sleep.engine;
using  sleep.bridges;
using  sleep.interfaces;
using  sleep.parser;

namespace sleep.runtime{

/** Maintains variables and variable scopes for a script instance.  If you want to change the way variables are handled do not 
  * override this class.  This class handles all accessing of variables through an object that implements the Variable 
  * interface.  
  *
  * <p><b>Set/Get a Variable without Parsing</b></p>
  * 
  * <code>script.getScriptVariables().putScalar("$var", SleepUtils.getScalar("value"));</code>
  * 
  * <p>The ScriptVariables object is the entry point for installing variables into
  * a script's runtime environment.  The above example illustrates how to set a
  * variable named $var to a specified Scalar value.</p>
  *
  * <code>Scalar value  = script.getScriptVariables().getScalar("$var");</code>
  *
  * <p>The code above illustrates how to retrieve a Scalar named $var from a 
  * script instance object.</p>
  *
  * <p>Sleep has 3 levels of scope.  They are (in order of precedence):</p>
  * <li>Local   - discarded after use</li>
  * <li>Closure - specific to the current executing closure</li>
  * <li>Global  - global to all scripts sharing this script variables instance</li>
  * 
  * @see sleep.runtime.Scalar
  * @see sleep.runtime.ScriptInstance
  * @see sleep.interfaces.Variable
  */
  [Serializable]
public class ScriptVariables : java.io.Serializable
{
    protected Variable    global;   /* global variables */
    protected java.util.LinkedList<Object>  closure;  /* closure specific variables :) */
    protected java.util.LinkedList<Object>  locals;   /* local variables--can be stacked into a closure thanks to pushl, popl, and inline functions */

    protected java.util.Stack<Object>       marks;    /* mark the beginning of a stack for fun and profit */

    /** called when a closure is entered, allows an old stack of local scopes to be restored easily */
    public void beginToplevel(java.util.LinkedList<Object> l)
    {
       marks.push(locals);
       locals = l;
    }
    
    /** called when a closure is exited, returns local var scope for later restoration if desired */
    public java.util.LinkedList<Object> leaveToplevel()
    {
       java.util.LinkedList scopes = locals;
       locals = (java.util.LinkedList)marks.pop(); 
       return scopes;
    }

    /** used to check if other local scopes exist after the next pop */
    public bool haveMoreLocals()
    {
        return locals.size() > 1;
    }

    /** Initializes this ScriptVariables container using a DefaultVariable object for default variable storage */
    public ScriptVariables()
    {
        this(new DefaultVariable());
    }

    /** Initializes this class with your version of variable storage */
    public ScriptVariables(Variable aVariableClass)
    {
       global   = aVariableClass;
       closure  = new java.util.LinkedList<Object>();
       locals   = new java.util.LinkedList<Object>();
       marks    = new java.util.Stack<Object>();

//       pushLocalLevel();
    }

    /** puts a scalar into the global scope */ 
    public void putScalar(String key, Scalar value)
    {
       global.putScalar(key, value);
    }

    /** retrieves a scalar */
    public Scalar getScalar(String key)
    {
       return getScalar(key, null);
    }

    /** retrieves the appropriate Variable container that has the specified key.  Precedence is in the order of the current
        local variable container, the script specific container, and then the global container */
    public Variable getScalarLevel(String key, ScriptInstance i)
    {
       Variable temp;

       //
       // check local variables for an occurence of our variable
       //
       temp = getLocalVariables();
       if (temp != null && temp.scalarExists(key))
       {
          return temp; 
       }

       //
       // check closure specific variables for an occurence of our variable
       //
       temp = getClosureVariables();
       if (temp != null && temp.scalarExists(key))
       {
          return temp;
       }

       //
       // check the global variables
       //
       temp = getGlobalVariables();
       if (temp.scalarExists(key))
       {
          return temp;
       }

       return null;
    }

    /** Returns the specified scalar, looking at each scope in order.  It is worth noting that only one local variable level is    
        qeuried.  If a variable is not local, the previous local scope is not checked.  */
    public Scalar getScalar(String key, ScriptInstance i)
    {     
       Variable temp = getScalarLevel(key, i);

       if (temp != null)
          return temp.getScalar(key);

       return null;
    }

    /** Puts the specified scalar in a specific scope
      * @param level the Variable container from the scope we want to store this scalar in.
      */
    public void setScalarLevel(String key, Scalar value, Variable level)
    {
       level.putScalar(key, value);
    }

    /** returns the current local variable scope */
    public Variable getLocalVariables()
    {
       if (locals.size() == 0)
          return null;

       return (Variable)locals.getFirst();
    }

    /** returns the current closure variable scope */
    public Variable getClosureVariables()
    {
       if (closure.size() == 0)
           return null;

       return (Variable)closure.getFirst();
    }

    /** returns the global variable scope */
    public Variable getGlobalVariables()
    {
       return global;
    }

    /** returns the closure level variables for this specific script environment */
    public Variable getClosureVariables(SleepClosure closure)
    {
       return closure.getVariables();
    }

    /** returns the closure level variables for this specific script environment */
    public void setClosureVariables(SleepClosure closure, Variable variables)
    {
       closure.setVariables(variables);
    }

    /** pushes the specified variables into this closures level, once the closure has executed this should be popped */
    public void pushClosureLevel(Variable variables)
    {
       closure.addFirst(variables);
    }

    /** discards the current closure variable scope */
    public void popClosureLevel()
    {
       closure.removeFirst();
    }

    /** makes the specified variable container active for the local scope.  once the code that is using this has finished, it really should be popped. */
    public void pushLocalLevel(Variable localVariables)
    {
       locals.addFirst(localVariables);
    }

    /** starts a new local variable scope.  once the code that is using this has finished, it should be popped */
    public void pushLocalLevel()
    {
       locals.addFirst(global.createLocalVariableContainer());
    }

    /** discards the current local variable scope, making the previous local scope the current local scope again */
    public void popLocalLevel()
    {
       locals.removeFirst();
    }
}
}