package org.hick.blah;

public class SqueezeBox
{
   public static String aStringField = "this is a string field";
   public static double aDoubleField = 3.0;

   public String  instanceStringField  = "this is also a string field";
   public boolean instanceBooleanField = true;

   protected int sq = 33;

   public void printValues()
   {
      System.out.println("static members:");
      System.out.println("aStringField '" + aStringField + "' and aDoubleField = " + aDoubleField);
      System.out.println("instance members:");
      System.out.println("instanceStringField '" + instanceStringField + "' instanceBooleanField = " + instanceBooleanField);
   }

   public int squeeze()
   { 
      sq++;
      return sq;
   }

   public void doStuff(double[][] matrix)
   {
      System.out.println("Printing the table:");

      for (int x = 0; x < matrix.length; x++)
      {
         for (int y = 0; y < matrix[x].length; y++)
         {
            System.out.print(matrix[x][y] + "; ");
         } 

         System.out.println("");
      }
   } 
}
