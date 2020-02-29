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
package sleep.engine.atoms;

import java.util.*;
import sleep.interfaces.*;
import sleep.engine.*;
import java.io.Serializable;
import sleep.runtime.*;

public class CheckAnd implements Check, Serializable
{
   private Check   left;
   private Check   right;

   /** Converts this object to a string, used by the sleep engine for constructing an AST like thing */
   public String toString(String prefix)
   {
       StringBuffer temp = new StringBuffer();
       temp.append(prefix);
       temp.append("[AND]:\n");
       temp.append(left.toString(prefix+"      "));
       temp.append(right.toString(prefix+"      "));
       return temp.toString();
   }

   /** Returns a string representation of this object */
   public String toString()
   {
       return toString("");
   }

   /** Constructs a check object, call by the sleep engine. */
   public CheckAnd(Check left, Check right) {
      this.left = left;
      this.right = right;
   }

   /** Performs this "check".  Returns the value of the condition that is checked. */
   public boolean check(ScriptEnvironment env)
   {
      return left.check(env) && right.check(env);
   }

   public void setInfo(int _hint) {}
}



