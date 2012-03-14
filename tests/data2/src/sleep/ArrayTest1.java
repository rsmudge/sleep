package sleep;

public class ArrayTest1
{
   public void foo(int[] a)
   {
      System.out.println("int[] a");
   }

   public void foo(double[] a)
   {
      System.out.println("double[] a");
   }

   public void foo(long[] a)
   {
      System.out.println("long[] a");
   }

   public void foo(float[] a)
   {
      System.out.println("float[] a");
   }

   public void foo(boolean[] a)
   {
      System.out.println("boolean[] a");
   }

   public void foo(Object[] a)
   {
      System.out.println("Object[] a");
   }

   public void foo(String[] a)
   {
      System.out.println("String[] a");
   }

   public void bar(Object[] a)
   {
      System.out.println("Object[] a: " + a.getClass());
      for (int x = 0; x < a.length; x++)
      {
         System.out.println("a["+x+"] - " + a[x] + " - " + a[x].getClass());
      }
   }

   public void bar(Object a)
   {
      System.out.println("Object a: " + a.getClass());
   }

   public void car(int[] a)
   {
      System.out.println("int[] a");
   }

   public void car(Object a)
   {
      System.out.println("Object a");
   }

   public void mar(int[] a)
   {
      System.out.println("int[] a");
   }

   public void mar(java.util.Collection a)
   {
      System.out.println("Collection a");
   }

   public void tar(java.util.Collection a)
   {
      System.out.println("Collection a: " + a.getClass());
   }

   public void tar(java.util.List a)
   {
      System.out.println("List a: " + a.getClass());
   }
}
