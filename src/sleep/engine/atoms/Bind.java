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
import sleep.runtime.*;

public class Bind extends Step
{
   String funcenv;
   Block code, name;
 
   public String toString(String prefix)
   {
      StringBuffer temp = new StringBuffer();

      temp.append(prefix);
      temp.append("[Bind Function]: \n");

      temp.append(prefix);
      temp.append("   [Name]:       \n");

      temp.append(prefix);
      temp.append(name.toString(prefix + "      "));

      temp.append(prefix);
      temp.append("   [Code]:       \n");

      temp.append(prefix);
      temp.append(code.toString(prefix + "      "));

      return temp.toString();
   }

   public Bind(String e, Block n, Block c)
   {
      funcenv = e;
      name = n;
      code = c;
   }

   //
   // no stack pre/post conditions for this step
   //

   public Scalar evaluate(ScriptEnvironment e)
   {
      Environment temp = e.getFunctionEnvironment(funcenv);
      
      if (temp != null)
      { 
         e.CreateFrame();
            name.evaluate(e);
            Scalar funcname = (Scalar)e.getCurrentFrame().pop();
         e.KillFrame();

         temp.bindFunction(e.getScriptInstance(), funcenv, funcname.getValue().toString(), code);
      }
      else
      {
         e.getScriptInstance().fireWarning("Attempting to bind code to non-existent environment: " + funcenv, getLineNumber());
      }

      return null;
   }
}



