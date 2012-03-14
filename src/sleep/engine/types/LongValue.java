package sleep.engine.types;

import sleep.runtime.ScalarType;

public class LongValue implements ScalarType
{
   protected long value;

   public LongValue(long _value)
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
      return value;
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
      return new Long(value);
   }

   public Class getType() { return this.getClass(); }
}
