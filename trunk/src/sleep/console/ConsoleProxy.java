package sleep.console;

/** a necessary interface for creating a front end to the sleep console.  all messages read in or written out from the console 
are done through a ConsoleProxy implementation. */
public interface ConsoleProxy
{
   /**
    * print a message to the console with no newline
    * @param message the message to print
    */
   public void consolePrint (String message);

   /**
    * print a message to the console with a newline
    * @param message the message to print
    */
   public void consolePrintln (Object message);

   /** read a message in from the console.  this method should block until a line is read. */
   public String consoleReadln ();
}
