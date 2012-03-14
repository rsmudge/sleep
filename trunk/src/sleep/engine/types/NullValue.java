package sleep.engine.types;

import sleep.runtime.ScalarType;

public class NullValue implements ScalarType
{
   public NullValue()
   {
   }

   public ScalarType copyValue()
   {
      return this;
   }

   public int intValue()
   {
      return 0;
   }

   public long longValue()
   {
      return 0;
   }

   public double doubleValue()
   {
      return 0;
   }

   public String toString()
   {
      return "";
   }

   public Object objectValue()
   {
      return null;
   }

   public Class getType() { return this.getClass(); }
}
