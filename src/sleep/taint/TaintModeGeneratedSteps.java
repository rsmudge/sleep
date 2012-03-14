package sleep.taint;

import sleep.engine.*;
import sleep.engine.types.*;
import sleep.engine.atoms.*;

import sleep.runtime.*;
import sleep.interfaces.*;

import java.util.*;

/** A replacement factory that generates Sleep interpreter instructions that honor and spread the taint mode. */
public class TaintModeGeneratedSteps extends GeneratedSteps
{
   public Step Call(String function)
   {
      return new TaintCall(function, super.Call(function));
   }

   public Step PLiteral(List doit)
   {
      return new PermeableStep(super.PLiteral(doit));
   }

   public Step Operate(String oper)
   {
      return new TaintOperate(oper, super.Operate(oper));
   }

   public Step ObjectNew(Class name)
   {
      return new PermeableStep(super.ObjectNew(name));
   }

   public Step ObjectAccess(String name)     
   {
      return new TaintObjectAccess(super.ObjectAccess(name), name, null);
   }

   public Step ObjectAccessStatic(Class aClass, String name)
   {
      return new TaintObjectAccess(super.ObjectAccessStatic(aClass, name), name, aClass);
   }
}
