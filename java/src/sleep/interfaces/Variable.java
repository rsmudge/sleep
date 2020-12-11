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
package sleep.interfaces;
 
import sleep.runtime.Scalar;
import sleep.runtime.ScriptInstance;

/**
 * <p>A variable bridge is a container for storing scalars.  A variable bridge is nothing more than a container.  It is 
 * possible to use a new variable container to alter how scalars are stored and accessed.  All scalars, scalar arrays, and 
 * scalar hashes are stored using this system. </p>
 *  
 * <p>A Variable bridge is installed by creating a new script variable manager with the new variable bridge.   The variable 
 * manager is then installed into a given script.</p>
 * 
 * <pre>
 * ScriptVariables variableManager = new ScriptVariable(new MyVariable());
 * script.setScriptVariables(variableManager);
 * </pre>
 * 
 * <p>Sleep scripts can share variables by using the same instance of ScriptVariables.  A Variable bridge can be used to 
 * create built in variables.  Every time a certain scalar is accessed the bridge might call a method and return the value 
 * of the method as the value of the accessed scalar.</p>
 * 
 */
public interface Variable extends java.io.Serializable
{
    /** true if a scalar named key exists in this variable environment */
    public boolean    scalarExists(String key); 

    /** returns the specified scalar, if scalarExists says it is in the environment, this method has to return a scalar */
    public Scalar     getScalar(String key);

    /** put a scalar into this variable environment */
    public Scalar     putScalar(String key, Scalar value);
 
    /** remove a scalar from this variable environment */
    public void       removeScalar(String key);

    /** returns which variable environment is used to temporarily store local variables.  */
    public Variable createLocalVariableContainer();

    /** returns which variable environment is used to store non-global / non-local variables.  this is also used to create the global scope for a forked script environment. */
    public Variable createInternalVariableContainer();
}
