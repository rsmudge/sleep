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

import sleep.runtime.ScriptInstance;
import java.util.Stack;

/**
 * <p>A predicate is an operator used inside of comparisons.  Comparisons are used in if statements and loop constructs.  
 * Sleep supports two types of predicates.  A unary predicate which takes one argument.  The other type is a binary 
 * (normal) predicate which takes two arguments.   In the example comparison a == b, a is the left hand side, b is the 
 * right hand side, and == is the predicate.  Predicate bridges are used to add new predicates to the language.</p>
 * 
 * <p>To install a predicate into a script environment:</p>
 * 
 * <pre>
 * ScriptInstance script;           // assume
 * Predicate      myPredicateBridge; // assume
 * 
 * Hashtable environment = script.getScriptEnvironment().getEnvironment();
 * environment.put("isPredicate", myPredicateBridge);
 * </pre>
 * 
 * <p>In the above code snippet the script environment is extracted from the script instance class. 
 * A binary predicate can have any name.  A unary predicate always begins with the - minus symbol.  "isin" would be 
 * considered a binary predicate where as "-isletter" would be considered a unary predicate.</p>
 */
public interface Predicate
{
   /**
    * decides the truthfulness of the proposition predicateName applied to the passedInTerms.  
    *
    * @param predicateName a predicate i.e. ==
    * @param anInstance an instance of the script asking about this predicate.
    * @param passedInTerms a stack of terms i.e. [3, 4].  These arguments are passed in REVERSE ORDER i.e. [right hand side, left hand side]
    *
    * @return a boolean, in the case of a predicate == and the terms [3, 4] we know 3 == 4 is false so return false.
    */
   public boolean decide(String predicateName, ScriptInstance anInstance, Stack passedInTerms);   
}
