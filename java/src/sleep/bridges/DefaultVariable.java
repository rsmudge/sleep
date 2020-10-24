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
package sleep.bridges;

import sleep.runtime.Scalar;
import sleep.runtime.ScriptInstance;
import sleep.interfaces.Variable;
import sleep.interfaces.Loadable;

import java.util.Hashtable;

public class DefaultVariable implements Variable, Loadable
{
    protected Hashtable values = new Hashtable();

    public boolean scalarExists(String key)
    {
        return values.containsKey(key);
    }

    public Scalar getScalar(String key)
    {
        return (Scalar)values.get(key);
    }

    public Scalar putScalar(String key, Scalar value)
    {
        return (Scalar)values.put(key, value);
    }

    public void removeScalar(String key)
    {
        values.remove(key);
    }

    public Variable createLocalVariableContainer()
    {
        return new DefaultVariable();
    }

    public Variable createInternalVariableContainer()
    {
        return new DefaultVariable();
    }

    public void scriptLoaded (ScriptInstance script)
    {
    }

    public void scriptUnloaded (ScriptInstance script)
    {
    }

    public DefaultVariable()
    {

    }
}
