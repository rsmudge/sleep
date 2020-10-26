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
using  sleep.engine.Block;

using  sleep.engine.atoms.Check;

namespace sleep.interfaces{
 

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
}