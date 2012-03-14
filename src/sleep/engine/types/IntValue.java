package sleep.engine.types;

import sleep.runtime.ScalarType;

public class IntValue implements ScalarType
{
   protected int value;

   public IntValue(int _value)
   {
      value = _value;
   }

   public ScalarType copyValue()
   {
      return this;
   }

   public int intValue()
   {
      return value;
   }

   public long longValue()
   {
      return (long)value;
   }

   public double doubleValue()
   {
      return (double)value;
   }

   public String toString()
   {
      return value+"";
   }

   public Object objectValue()
   {
      return new Integer(value);
   }

   public Class getType()
   {
      return this.getClass();
   }
}
