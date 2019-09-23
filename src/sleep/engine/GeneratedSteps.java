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
