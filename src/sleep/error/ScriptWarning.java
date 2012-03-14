/* 
 * Copyright (C) 2002-2012 Raphael Mudge (rsmudge@gmail.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
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

