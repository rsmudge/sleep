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

using  sleep.runtime.ScriptInstance;
using  sleep.runtime.Scalar;

using  java.util.Stack;

namespace sleep.interfaces{

/**
 * <p>A function bridge is used to define a built-in function.  Once a function bridge is installed into the script 
 * environment, it can be called from user created scripts.</p>
 * 
 * <p>An example of a function bridge:</p>
 * 
 * <pre>
 * public class MyAddFunction implements Function
 * {
 *    public Scalar evaluate(String name, ScriptInstance script, Stack arguments) 
 *    {
 *       if (name.equals("&add"))
 *       {
 *          int a = BridgeUtilities.getInt(arguments, 0);  
 *          int b = BridgeUtilities.getInt(arguments, 0); 
 *  
 *          return SleepUtils.getScalar(a + b); 
 *       }
 * 
 *       return SleepUtils.getEmptyScalar();
 *    }
 * }
 * </pre>
 * 
 * <p>To install a function into a script environment:</p>
 * 
 * <pre>
 * ScriptInstance script;           // assume
 * 
 * Function  myFunctionBridge = new MyAddFunction();
 * Hashtable environment      = script.getScriptEnvironment().getEnvironment();
 * 
 * environment.put("&add", myFunctionBridge);
 * </pre>
 * 
 * <p>In the above code snippet the script environment is extracted from the ScriptInstance object script.  The function 
 * name is the key with the instance of our Function bridge as the value.   The function name must begin with & ampersand 
 * for sleep to know it is a function.</p>
 * 
 * <p>Once a function bridge is installed into the script environment.  The installed function(s) can be called as normal
 * sleep functions i.e.</p>
 * 
 * <code>$var = add(3, 4); # value of $var is now 7</code>
 * 
 * <p>To evaluate a Function object (should you ever need to directly evaluate one):</p>
 *
 *
 * <pre>// assume Function func; ScriptInstance script; Stack locals
 *
 * Scalar value = SleepUtils.runCode(func, "&name", script, locals);</pre>
 *
 * <p>The above is important because it clears the return value in the script environment once the function finishes executing.
 * Failing to clear the return value can result in other sleep code not executing at all.  It is not a fun bug to track down.</p>
 *
 * @see sleep.bridges.BridgeUtilities
 * @see sleep.runtime.ScriptInstance
 * @see sleep.runtime.SleepUtils
 */
 [Serializable]
public interface Function : java.io.Serializable
{
   /**
    * Evaluate a function and return the resulting scalar.  Only the sleep interpreter should ever call this function.  If you have
    * a maddening desire to call this Function object yourself, then use the convienence method in SleepUtils.
    *
    * @param functionName the function being called.
    * @param anInstance an instance of the script calling this function.
    * @param passedInLocals a stack containing the locals passed to this function.  The locals are Scalar values passed in reverse order i.e. [arg n, arg n-1, ..., arg 1, arg 0]
    *
    * @see sleep.runtime.SleepUtils#runCode(Function, String, ScriptInstance, Stack)
    *
    * @return an instance of Scalar containing the return value of this function.
    */
   public Scalar evaluate(String functionName, ScriptInstance anInstance, Stack passedInLocals);
}
}