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
 
using  sleep.engine;
using  sleep.engine.types;
using  sleep.interfaces;
using  sleep.runtime;

namespace sleep.bridges{
 
/** The Sleep Closure class.  This class represents a Function object that is also a self contained closure */
public class SleepClosure : Function, java.lang.Runnable
{
    private static int ccount = -1;
    private int id;

    private class ClosureIterator : java.util.Iterator<Object>
    {
       protected Scalar            current;
       protected java.util.Stack<Object>             locals = new java.util.Stack<Object>();

       public bool hasNext()
       {
          current = callClosure("eval", null, locals);
          return !SleepUtils.isEmptyScalar(current);
       }

       public Object next()
       {
          return current;
       }

       public void remove()
       {
       }
    }

    public java.util.Iterator<Object> scalarIterator()
    {
       return new ClosureIterator();
    }

    /** the block of code associated with this sleep closure */
    Block                code;

    /** the owning script associated with this sleep closure */
    ScriptInstance      owner;

    /** the saved context of this closure */
    java.util.Stack<Object>             context;

    /** the meta data for this closure context */
    java.util.HashMap<Object,Object>          metadata; 

    /** the closure variables referenced by this closure */
    Variable         variables;

    /** put some value into the metadata store associated with this closure. */
    public void putMetadata(Object key, Object value)
    {
       metadata.put(key, value);
    }

    /** obtain a key from the metadata store associated with this closure */
    public Object getAndRemoveMetadata(Object key, Object defaultv)
    {
       Object temp = metadata.remove(key);
       if (temp == null) { return defaultv; }
       return temp;
    }

    /** saves the top level context; may throw an exception if an error is detected... be sure to move critical cleanup prior to this function. */
    private void saveToplevelContext(java.util.Stack<Object> _context, java.util.LinkedList<Object> localLevel)
    {
       if (!_context.isEmpty())
       {
          _context.push(localLevel); /* push the local vars on to the top of the context stack,
                                        this better be popped before use!!! */
          context.push(_context);
       }
       else if (localLevel.size() != 1)
       {
          throw new java.lang.RuntimeException((localLevel.size() - 1) + " unaccounted local stack frame(s) in " + toString() + " (perhaps you forgot to &popl?)");
       }
    }

    /** returns the top most context stack... */
    private java.util.Stack<Object> getToplevelContext()
    {
       if (context.isEmpty())
       {
          return new java.util.Stack<Object>();
       }
       return (java.util.Stack<Object>)context.pop();
    }

    /** Returns a generic string version of this closure without id information */
    public String toStringGeneric()
    {
       return "&closure[" + code.getSourceLocation() + "]";
    }

    
    /** Information about this closure in the form of &closure[<source file>:<line range>]#<instance number> */
    public String toString()
    {
       return toStringGeneric() + "#" + id;
    }

    /** This is here for the sake of serialization */
    private SleepClosure()
    {

    }

    /** Creates a new Sleep Closure, with a brand new set of internal variables.  Don't be afraid, you can call this constructor from your code. */
    public SleepClosure(ScriptInstance si, Block _code)
    {
       this(si, _code, si.getScriptVariables().getGlobalVariables().createInternalVariableContainer());
    }
  
    /** Creates a new Sleep Closure that uses the specified variable container for its internal variables */
    public SleepClosure(ScriptInstance si, Block _code, Variable _var)
    {
       code      = _code;
       owner     = si;
       context   = new java.util.Stack<Object>();
       metadata  = new java.util.HashMap<Object,Object>();

       _var.putScalar("$this", SleepUtils.getScalar(this));
       setVariables(_var);

       ccount = (ccount + 1) % Short.MAX_VALUE;

       id = ccount;
    }

    /** Returns the owning script instance */
    public ScriptInstance getOwner()
    {
       return owner;
    }

    /** Returns the runnable block of code associated with this closure */
    public Block getRunnableCode()
    {
       return code;
    }

    /** Returns the variable container for this closures */
    public Variable getVariables()
    {
       return variables;
    }

    /** Sets the variable environment for this closure */
    public void setVariables(Variable _variables)
    {
       variables = _variables; 
    }

    /** "Safely" calls this closure. */
    public void run()
    {
       callClosure("run", null, null);
    }

    /** "Safely" calls this closure.  Use this if you are evaluating this closure from your own code. 

        @param message the message to pass to this closure (available as $0)
        @param the calling script instance (null value assumes same as owner)
        @param the local data as a stack object (available as $1 .. $n)

        @return the scalar returned by this closure
     */
    public Scalar callClosure(String message, ScriptInstance si, java.util.Stack<Object> locals)
    {
       if (si == null)
           si = getOwner();

       if (locals == null)
           locals = new java.util.Stack<Object>();

       si.getScriptEnvironment().pushSource("<internal>");
       si.getScriptEnvironment().CreateFrame();
       si.getScriptEnvironment().CreateFrame(locals); /* dump the local vars here plz */

       CallRequest request = new CallRequest.ClosureCallRequest(si.getScriptEnvironment(), -1, SleepUtils.getScalar(this), message);
       request.CallFunction();

       /* get the return value */    
       Scalar rv = si.getScriptEnvironment().getCurrentFrame().isEmpty() ? SleepUtils.getEmptyScalar() : (Scalar)si.getScriptEnvironment().getCurrentFrame().pop();

       /* handle the cleanup */
       si.getScriptEnvironment().KillFrame();
       si.getScriptEnvironment().clearReturn();
       si.getScriptEnvironment().popSource();

       return rv;
    }

    /** Evaluates the closure, use callClosure instead. */
    public Scalar evaluate(String message, ScriptInstance si, java.util.Stack<Object> locals)
    {
       if (owner == null) { owner = si; }

       ScriptVariables   vars = si.getScriptVariables();
       ScriptEnvironment env  = si.getScriptEnvironment();

       Variable          localLevel;

       Scalar temp; // return value of subroutine.

       lock (vars)
       {
          java.util.Stack<Object> toplevel = getToplevelContext();
          env.loadContext(toplevel, metadata);

          vars.pushClosureLevel(getVariables()); 

          if (toplevel.isEmpty()) /* a normal closure call */
          {
             vars.beginToplevel(new LinkedList());
             vars.pushLocalLevel();
          }
          else /* restoring from a coroutine */
          {
             LinkedList levels = (LinkedList)toplevel.pop();             
             vars.beginToplevel(levels);
          }

          localLevel = vars.getLocalVariables();

          //
          // initialize local variables...
          //
          vars.setScalarLevel("$0", SleepUtils.getScalar(message), localLevel);
          BridgeUtilities.initLocalScope(vars, localLevel, locals);

          //
          // call the function, save the scalar that was returned. 
          //
          if (toplevel.isEmpty())
          {
             temp = code.evaluate(env);
          }
          else
          {
             temp = env.evaluateOldContext();
          }

          LinkedList phear = vars.leaveToplevel();        /* this will simultaneously save and remove all local scopes associated with
                                                             the current closure context.  Very sexy */
          vars.popClosureLevel();                         /* still have to do this manually, one day I need to refactor this state saving stuff */

          if (si.getScriptEnvironment().isCallCC())
          {
             SleepClosure tempc = SleepUtils.getFunctionFromScalar(si.getScriptEnvironment().getReturnValue(), si);
             tempc.putMetadata("continuation", SleepUtils.getScalar(this));
             tempc.putMetadata("sourceLine", si.getScriptEnvironment().getCurrentFrame().pop());
             tempc.putMetadata("sourceFile", si.getScriptEnvironment().getCurrentFrame().pop());

             si.getScriptEnvironment().flagReturn(si.getScriptEnvironment().getReturnValue(), ScriptEnvironment.FLOW_CONTROL_PASS); 
          }

          saveToplevelContext(env.saveContext(), phear);  /* saves the top level context *pHEAR*; done last in case there is an error with this */
       }

       return temp;
    }

    private void writeObject(java.io.ObjectOutputStream outJ) //throws IOException
    {
       outJ.writeInt(id);
       outJ.writeObject(code);
       outJ.writeObject(context);
/*       out.writeObject(metadata); */
       outJ.writeObject(variables);
    }

    private void readObject(java.io.ObjectInputStream inJ) //throws IOException, ClassNotFoundException
    {
       id        = inJ.readInt();
       code      = (Block)inJ.readObject();
       context   = (java.util.Stack<Object>)inJ.readObject();
       metadata  = new java.util.HashMap<Object,Object>();
/*       metadata  = (java.util.HashMap<Object,Object>)in.readObject(); */
       variables = (Variable)inJ.readObject();
       owner     = null;
    }
}


}