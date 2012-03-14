package sleep.parser;

/**
 * <p>This class offers access to modify some settings within the sleep parser.</p>
 * 
 * <h2>Install an Escape Constant</h2>
 * 
 * <p>In sleep a character prefixed by a \ backslash within a "double quoted" string is said to be escaped.  Typically an 
 * escaped character is just skipped over during processing.  It is possible in sleep to add meaning to different 
 * characters by installing an escape.   For example to add the escape \r to mean the new line character one would do the 
 * following:</p>
 * 
 * <code>ParserConfig.installEscapeConstant('m', "MONKEY");</code>
 * 
 * <p>Once the above code is executed the value "blah\m" inside of sleep would be equivalent in java to "blahMONKEY".</p>
 * 
 * <h2>Register a Keyword</h2>
 * 
 * <p>The sleep parser requires that all environment "keywords" be registered before any scripts are parsed.  Bridges
 * that should register their keywords are Environment, PredicateEnvironment, FilterEnvironment, Operator, and Predicate.</p>
 * 
 * @see sleep.interfaces.Environment
 * @see sleep.interfaces.PredicateEnvironment
 * 
 */
import java.io.*;

public class ParserConfig
{
   /** Installs an escape constant into the sleep parser.  Any time the escape constant escape is encountered inside of a 
       parsed literal with a \ backslash before it, sleep will substitute that string with the value specified here. */
   public static void installEscapeConstant(char escape, String value)
   {
      CodeGenerator.installEscapeConstant(escape, value);
   }

   /** registers "keyword" as a keyword with the parser.  This is a necessity if you have environment bridges in sleep */
   public static void addKeyword(String keyword)
   {
      Checkers.addKeyword(keyword);
   }

   /** Query the Sleep classpath.  This is a semi-colon separated list of paths where sleep
       should search for jar files that scripts attempt to import */
   public static String getSleepClasspath()
   {
      return System.getProperty("sleep.classpath", ".");
   }

   /** Set the Sleep classpath.  A semi-colon separated list of paths where sleep should search for
       jar files that scripts attempt to import */
   public static void setSleepClasspath(String path)
   {
      System.setProperty("sleep.classpath", path);
   }

   /** Search the sleep classpath for the specified file.  Returns a File object reflecting where the
       file was found.  This method does not return null.  If the file does not exist then a File object
       constructed with just the passed in name is returned */
   public static File findJarFile(String name)
   {
       File cp = new File(name);

       if (cp.exists()) { return cp; }

       String[] paths = System.getProperty("sleep.classpath", ".").replace(':', ';').split(";");

       for (int x = 0; x < paths.length; x++)
       {
          File temp = new File(paths[x], name);
          if (temp.exists())
          {
             return temp;
          }
       }

       return cp;
   }
}
