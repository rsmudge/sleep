/*
 * Copyright 2002-2020 Raphael Mudge
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
package sleep.interfaces;
 
import java.util.*;

import sleep.runtime.ScriptInstance;
import sleep.engine.Block;

/**
 * <p>Blocks of code associated with an identifier are processed by their environment.  An example of an environment is the 
 * subroutine environment.   To declare a subroutine in sleep you use:</p>
 * 
 * <code>sub identifier { commands; }</code>
 * 
 * <p>When sleep encounters this code it looks for the environment bound to the keyword "sub".  It passes the environment 
 * for "sub" a copy of the script instance, the identifier, and the block of executable code.  The environment can do 
 * anything it wants with this information.   The subroutine environment simply creates a Function object with the block of code and 
 * installs it into the environment.  Thus allowing scripts to declare custom subroutines.</p>
 * 
 * <p>In general a block of code is associated with an environment using the following syntax:</p>
 * 
 * <code>keyword identifier { commands; } # sleep code</code>
 * 
 * <p>Script environment bridge keywords should be registered with the script parser before any scripts are loaded.  This 
 * can be accomplished as follows:</p>
 * 
 * <code>ParserConfig.addKeyword("keyword");</code>
 * 
 * <p>To install a new environment into the script environment:</p>
 * <pre>
 * ScriptInstance script;              // assume
 * Environment    myEnvironmentBridge; // assume
 * 
 * Hashtable environment = script.getScriptEnvironment().getEnvironment();
 * environment.put("keyword", myEnvironmentBridge);
 * </pre>
 * 
 * <p>The Block object passed to the environment can be executed using:</p>
 * 
 * <code>SleepUtils.runCode(commands, instance.getScriptEnvironment());</code>
 * 
 * <p>Environment bridges are great for implementing different types of paradigms. I've used this feature to add everything 
 * from event driven scripting to popup menu structures to my application. Environments are a very powerful way to get the 
 * most out of integrating your application with the sleep language.</p>
 * 
 * @see sleep.parser.ParserConfig#addKeyword(String)
 */
public interface Environment
{
   /**
    * binds a function (functionName) of a certain type (typeKeyword) to the defined functionBody.
    *
    * @param typeKeyword the keyword for the function. (i.e. sub)
    * @param functionName the function name (i.e. add)
    * @param functionBody the compiled body of the function (i.e. code to add 2 numbers)
    */
   public abstract void bindFunction(ScriptInstance si, String typeKeyword, String functionName, Block functionBody);
}
