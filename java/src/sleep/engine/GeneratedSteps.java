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
package sleep.engine;

import java.util.*;
import sleep.interfaces.*;
import sleep.engine.atoms.*;
import sleep.runtime.*;

import java.io.Serializable;

/** A class providing methods for constructing an atomic step of a specific type.  Feel free to extend this class and specify your own factory to the CodeGenerator class. */
public class GeneratedSteps
{
    public Step PopTry()
    {
       Step temp = new PopTry();
       return temp;
    }
 
    public Step Try(Block owner, Block handler, String var)
    {
       Step temp = new Try(owner, handler, var);
       return temp;
    }

    public Step Operate(String oper)
    {
       Step temp = new Operate(oper);
       return temp;
    }

    public Step Return(int type)
    {
       Step temp = new Return(type);
       return temp;
    }

    public Step SValue(Scalar value)
    {
       Step temp = new SValue(value);
       return temp;
    }

    public Step IteratorCreate(String key, String value)
    {
       return new Iterate(key, value, Iterate.ITERATOR_CREATE);
    }

    public Step IteratorNext()
    {
       return new Iterate(null, null, Iterate.ITERATOR_NEXT);
    }

    public Step IteratorDestroy()
    {
       return new Iterate(null, null, Iterate.ITERATOR_DESTROY);
    }

    public Check Check(String nameOfOperator, Block setupOperands)
    {
       Check temp = new CheckEval(nameOfOperator, setupOperands);
       return temp;
    }

    public Check CheckAnd(Check left, Check right)
    {
       Check temp = new CheckAnd(left, right);
       return temp;
    }

    public Check CheckOr(Check left, Check right)
    {
       Check temp = new CheckOr(left, right);
       return temp;
    }

    public Step Goto(Check conditionForGoto, Block ifTrue, Block increment)
    {
       Goto temp = new Goto(conditionForGoto);
       temp.setChoices(ifTrue);
       temp.setIncrement(increment);
       return temp;
    }

    public Step Decide(Check conditionForGoto, Block ifTrue, Block ifFalse)
    {
       Decide temp = new Decide(conditionForGoto);
       temp.setChoices(ifTrue, ifFalse);
       return temp;
    }
 
    public Step PLiteral(List doit)
    {
       Step temp = new PLiteral(doit);
       return temp;
    }

    public Step Assign(Block variable)
    {
       Step temp = new Assign(variable);
       return temp;
    }

    public Step AssignAndOperate(Block variable, String operator)
    {
       Step temp = new Assign(variable, this.Operate(operator));
       return temp;
    }

    public Step AssignT()
    {
       Step temp = new AssignT();
       return temp;
    }

    public Step AssignTupleAndOperate(String operator)
    {
       Step temp = new AssignT(this.Operate(operator));
       return temp;
    }

    public Step CreateFrame()
    {
       Step temp = new CreateFrame();
       return temp;
    }

    public Step Get(String value)
    {
       Step temp = new Get(value);
       return temp;
    }

    public Step Index(String value, Block index)
    {
       Step temp = new Index(value, index);
       return temp;
    }

    public Step Call(String function)
    {
       Step temp = new Call(function);
       return temp;
    }

    public Step CreateClosure(Block code)
    {
       Step temp = new CreateClosure(code);
       return temp;
    }

    public Step Bind(String functionEnvironment, Block name, Block code)
    {
       Step temp = new Bind(functionEnvironment, name, code);
       return temp;
    }

    public Step BindPredicate(String functionEnvironment, Check predicate, Block code)
    {
       Step temp = new BindPredicate(functionEnvironment, predicate, code);
       return temp;
    }

    public Step BindFilter(String functionEnvironment, String name, Block code, String filter)
    {
       Step temp = new BindFilter(functionEnvironment, name, code, filter);
       return temp;
    }

    public Step ObjectNew(Class name)
    {
       Step temp = new ObjectNew(name);
       return temp;
    }

    public Step ObjectAccess(String name)
    {
       Step temp = new ObjectAccess(name, null);
       return temp;
    }

    public Step ObjectAccessStatic(Class aClass, String name)
    {
       Step temp = new ObjectAccess(name, aClass);
       return temp;
    }
}
