package sleep.error;

/** A class containing syntax error information.  A SyntaxError object is passed by a YourCodeSucksException.   
  *
  * @see sleep.error.YourCodeSucksException
  */
public class SyntaxError
{
   protected String description;
   protected String code;
   protected String marker;
   protected int    lineNo;

   /** construct a syntax error object, but enough about me... how about you? */
   public SyntaxError(String _description, String _code, int _lineNo)
   {
      this(_description, _code, _lineNo, null);
   }

   /** construct a syntax error object, but enough about me... how about you? */
   public SyntaxError(String _description, String _code, int _lineNo, String _marker)
   {
      description = _description;
      code        = _code;
      lineNo      = _lineNo;
      marker      = _marker;
   }

   /** return a marker string */
   public String getMarker()
   {
      return marker;
   }

   /** return a best guess description of what the error in the code might actually be */
   public String getDescription()  
   {
      return description;
   }

   /** return an isolated snippet of code from where the error occured */
   public String getCodeSnippet()
   {
      return code;
   }
 
   /** return the line number in the file where the error occured.  */
   public int getLineNumber()
   {
       return lineNo;
   }
}
