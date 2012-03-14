package sleep.engine.types;

import sleep.runtime.ScalarType;

public class ObjectValue implements ScalarType
{
   protected Object value;

   public ObjectValue(Object _value)
   {
      value = _value;
   }

   public ScalarType copyValue()
   {
      return this;
   }

   public int intValue()
   {
      String str = toString();

      if (str.length() == 0) { return 0; }
      if (str.equals("true")) { return 1; }
      if (str.equals("false")) { return 0; }

      try
      {
         return Integer.decode(str).intValue();
      }
      catch (Exception ex)
      {
         return 0;
      }
   }

   public long longValue()
   {
      String str = toString();

      if (str.length() == 0) { return 0L; }
      if (str.equals("true")) { return 1L; }
      if (str.equals("false")) { return 0L; }

      try
      {
         return Long.decode(str).longValue();
      }
      catch (Exception ex)
      {
         return 0L;
      }
   }

   public double doubleValue()
   {
      String str = toString();

      if (str.length() == 0) { return 0.0; }
      if (str.equals("true")) { return 1.0; }
      if (str.equals("false")) { return 0.0; }

      try
      {
         return Double.parseDouble(str);
      }
      catch (Exception ex)
      {
         return 0;
      }
   }

   public String toString()
   {
      return value.toString();
   }

   public Object objectValue()
   {
      return value;
   }

   public Class getType()
   {
      return this.getClass();
   }
}
