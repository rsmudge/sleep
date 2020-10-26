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

using  java.util;

using  sleep.runtime.ScriptInstance;
using  sleep.runtime.Scalar;
using  sleep.engine.Block;

using  sleep.engine.atoms.Check;


namespace sleep.interfaces{
 
/**
 * <p>Filtered environments are similar to normal keyword environments except they also allow a parameter 
 * specified by the user.  The identifier and parameter are both sent to the bridge when a block of code 
 * is bound to a particular filtered environment keyword.</p>
 * 
 * <p>In general the syntax for binding a filtered environment is:</p>
 * 
 * <code>keyword identifier "parameter" { code; }</code>
 * 
 * <p>One thing worth noting is that when the "parameter" string gets passed to the class implementing this interface, 
the 
 * "parameter" will be unevaluated.  So if the "parameter" was an expression i.e. (2 + 2), the value passed back to the
 * interface would be the string "(2 + 2)".  To evaluate the parameter use the following code:</p>
 * 
 * <pre>ScriptEnvironment environment = script.getScriptEnvironment();
 * Scalar value = environment.evaluateExpression(parameter);</pre>
 * 
 * <p>Script filtered environment bridge keywords should be registered with the script parser before any scripts are
 * loaded.  This can be accomplished as follows:</p>
 * 
 * <code>ParserConfig.addKeyword("keyword");</code>
 * 
 * <p>To install a new filtered environment into the script environment:</p>
 * 
 * <pre>
 * ScriptInstance script;              // assume
 * FilterEnvironment    myEnvironmentBridge; // assume
 * 
 * Hashtable environment = script.getScriptEnvironment().getEnvironment();
 * environment.put("keyword", myEnvironmentBridge);
 * </pre>
 * 
 * <p>Filter environments are really just an extension of the normal environment bridges.  In particular filter 
 * environments can be used to implement event listener mechanisms or as an expansion fornormal environment bridges that 
 * might require a parameter.</p>
 * 
 * @see sleep.interfaces.Environment
 * @see sleep.parser.ParserConfig#addKeyword(String)
 */
public interface FilterEnvironment
{
   /**
    * binds a function (functionName) of a certain type (typeKeyword) to the defined functionBody.
    *
    * @param keyword     the keyword for the function.
    * @param identifier  the identifier attached to this keyword
    * @param parameter   the parameter specified with this declaration.  The parameter keyword is passed in unparsed to allow you, the bridge writer, choice in the matter of what to do with the parameter.  Depending on the purpose of the bridge some maywant to evaluate the parameter when the block of code is first bound.  Others may want to evaluate the parameter each time the bridge carries out its actions. 
    * @param functionBody the compiled body of the function (i.e. code to add 2 numbers)
    */
   public abstract void bindFilteredFunction(ScriptInstance si, String keyword, String identifier, String parameter, Block functionBody);
}
}