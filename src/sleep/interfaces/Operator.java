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

import sleep.runtime.Scalar;
import sleep.runtime.ScriptInstance;

import java.util.Stack;

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
