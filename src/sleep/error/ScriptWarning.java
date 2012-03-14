package sleep.error;

import sleep.runtime.ScriptInstance;
import java.io.File;

/** A package for all information related to a runtime script warning.  A runtime script warning occurs whenever something bad 
  * happens while executing a script.  Something bad could include an exception being thrown by a bridge, a script trying to 
  * execute a non-existant function, a script trying to make a comparison with a non-existant predicate etc. 
  *
  * @see sleep.error.RuntimeWarningWatcher
  */
public class ScriptWarning
{
   protected ScriptInstance script;
   protected String         message; 
   protected int            line;
   protected boolean        trace;
   protected String         source;

   public ScriptWarning(ScriptInstance _script, String _message, int _line)
   {
      this(_script, _message, _line, false);
   }

   public ScriptWarning(ScriptInstance _script, String _message, int _line, boolean _trace)
   {
      script  = _script;
      message = _message;
      line    = _line;
      trace   = _trace;
      source  = script.getScriptEnvironment().getCurrentSource();
   }

   /** is this a trace message for one of the trace debug options */
   public boolean isDebugTrace()
   {
      return trace;
   }

   /** returns the ScriptInstance object that was the source of this runtime error */
   public ScriptInstance getSource()
   {
      return script;
   }
 
   /** returns a nicely formatted string representation of this runtime warning. */
   public String toString()
   {
      if (isDebugTrace())
      {
         return "Trace: " + getMessage() + " at " + getNameShort() + ":" + getLineNumber();
      }
      else
      {
         return "Warning: " + getMessage() + " at " + getNameShort() + ":" + getLineNumber();
      }
   }

   /** returns a short synopsis of what the warnng is */
   public String getMessage()
   {
      return message;
   }

   /** returns the line number in the source script where the runtime error/warning occured */
   public int getLineNumber()
   {
      return line;
   }

   /** returns the full path for the source script */
   public String getScriptName()
   {
      return source;
   }

   /** returns just the filename of the source script */
   public String getNameShort()
   {
      return new File(getScriptName()).getName();
   }
}

