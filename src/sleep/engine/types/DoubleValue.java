package sleep.engine.types;

import sleep.runtime.ScalarType;

public class DoubleValue implements ScalarType
{
   protected double value;

   public DoubleValue(double _value)
   {
      value = _value;
   }

   public ScalarType copyValue()
   {
      return this;
   }

   public int intValue()
   {
      return (int)value;
   }

   public long longValue()
   {
      return (long)value;
   }

   public double doubleValue()
   {
      return value;
   }

   public String toString()
   {
      return value+"";
   }

   public Object objectValue()
   {
      return new Double(value);
   }

   public Class getType() { return this.getClass(); }
}
