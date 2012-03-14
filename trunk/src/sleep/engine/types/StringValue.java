package sleep.engine.types;

import sleep.runtime.ScalarType;

public class StringValue implements ScalarType
{
   protected String value;

   public StringValue(String _value)
   {
      value = _value;
   }

   public ScalarType copyValue()
   {
      return this;
   }

   /** does nothing for now... */
   private String numberOnlyString()
   {
      return value;
   }

   public int intValue()
   {
      try
      {
         return Integer.parseInt(numberOnlyString());
      }
      catch (Exception ex)
      {
         return 0;
      }
   }

   public long longValue()
   {
      try
      {
         return Long.parseLong(numberOnlyString());
      }
      catch (Exception ex)
      {
         return 0L;
      }
   }

   public double doubleValue()
   {
      try
      {
         return Double.parseDouble(numberOnlyString());
      }
      catch (Exception ex)
      {
         return 0.0;
      }
   }

   public String toString()
   {
      return value;
   }

   public Object objectValue()
   {
      return value;
   }

   public Class getType() { return this.getClass(); }
}
