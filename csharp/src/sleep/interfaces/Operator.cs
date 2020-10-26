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


using  sleep.runtime.Scalar;
using  sleep.runtime.ScriptInstance;

using  java.util.Stack;

namespace sleep.interfaces{
/**
 * <p>An operator in sleep parlance is anything used to operate on two variables inside of an expression.  For example 2 + 3 
 * is the expression add 2 and 3.  The + plus sign is the operator.</p>
 * 
 * <p>Creating an Operator class and installing it into the environment makes the operator available for use within 
 * expressions.</p>
 * 
 * <p>To install an operator into a script environment:</p>
 * 
 * <pre>
 * ScriptInstance script;           // assume
 * Operator       myOperatorBridge; // assume
 * 
 * Hashtable environment = script.getScriptEnvironment().getEnvironment();
 * environment.put("operator", myOperatorBridge);
 * </pre>
 * 
 * <p>Operator bridges probably won't be as common as other bridges.  Operator bridges can be used for adding new math 
 * operators or new string manipulation operators.</p>
 * 
 * 
 */
public interface Operator
{
   /**
    * apply operator operatorName on the values in the stack.
    *
    * @param operatorName the name of the operator, for example the String "+"
    * @param anInstance instance of the script calling this operator
    * @param passedInLocals a stack containing values the operator is to be applied to: [left hand side, right hand side]
    *
    * @return a Scalar containing the result of the operatorName applied to the passedInLocals, in the case of "+" applied to [4, 3] we would get a Scalar containing the integer 7.
    */
   public Scalar operate(String operatorName, ScriptInstance anInstance, Stack passedInLocals);   
}
}