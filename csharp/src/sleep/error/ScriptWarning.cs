/*
 * Copyright 2002-2020 Raphael Mudge
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 *    conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 *    of conditions and the following disclaimer in the documentation and/or other materials
 *    provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without specific prior
 *    written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
using System;
using java = biz.ritter.javapi;

using  sleep.runtime;

namespace sleep.error{

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
   protected bool        trace;
   protected String         source;

   public ScriptWarning(ScriptInstance _script, String _message, int _line)
   {
      this(_script, _message, _line, false);
   }

   public ScriptWarning(ScriptInstance _script, String _message, int _line, bool _trace)
   {
      script  = _script;
      message = _message;
      line    = _line;
      trace   = _trace;
      source  = script.getScriptEnvironment().getCurrentSource();
   }

   /** is this a trace message for one of the trace debug options */
   public bool isDebugTrace()
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

}