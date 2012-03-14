package sleep.parser;

import java.util.*;

public class Statement extends TokenList
{
   protected int type;
   
   public int getType() 
   {
      return type;
   }

   public void setType(int t)
   {
      type = t;
   }

   public String toString()
   {
      return "[" + type + "] " + super.toString();
   }
}
