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
