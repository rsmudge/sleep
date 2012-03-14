package sleep.taint;

import sleep.engine.*;
import sleep.interfaces.*;
import sleep.runtime.*;

import java.util.*;

/** A tainted scalar value *pHEAR* */
public class TaintedValue implements ScalarType
{
   protected ScalarType value = null;

   /** construct a tainted scalar */
   public TaintedValue(ScalarType _value)
   {
      value = _value;
   }

   public ScalarType copyValue()
   {
      return new TaintedValue(value.copyValue());
   }

   public ScalarType untaint()
   {
      return value;
   }

   public int intValue()
   {
      return value.intValue();
   }

   public long longValue()
   {
      return value.longValue();
   }

   public double doubleValue()
   {
      return value.doubleValue();
   }

   public String toString()
   {
      return value.toString();
   }

   public Object objectValue()
   {
      return value.objectValue();
   }

   public Class getType()
   {
      return value.getType();
   }
}


