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
using java=biz.ritter.javapi;

using  sleep.runtime;

namespace sleep.interfaces{

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
   public bool decide(String predicateName, ScriptInstance anInstance, java.util.Stack<Object> passedInTerms);   
}
}