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
package sleep.error;

/**
 * Runtime errors are caught by sleep.  Examples of a runtime error include calling a function that doesn't exist, using an 
 * operator that doesn't exist, or causing an exception in the underlying javacode.  Whenever any of these events occurs 
 * the event is isolated and turned into a ScriptWarning object.  The Script Warning object is then propagated to all 
 * registered warning watchers.
 * <br>
 * <br>To create a runtime warning watcher: 
 * <br>
 * <pre>
 * public class Watchdog implements RuntimeWarningWatcher
 * {   
 *    public void processScriptWarning(ScriptWarning warning)    
 *    {
 *       String message = warning.getMessage();      
 *       int    lineNo  = warning.getLineNumber();
 *       String script  = warning.getNameShort(); // name of script 
 *    }
 * }
 * </pre> 
 * To register a warning watcher:
 * <br>
 * <br><code>script.addWarningWatcher(new Watchdog());</code>
 * 
 * @see sleep.runtime.ScriptInstance
 * @see sleep.error.ScriptWarning
 */
public interface RuntimeWarningWatcher
{
   /** fired when a runtime warning has occured.  You might want to display this information to the user in some
       manner as it may contain valuable information as to why something isn't working as it should */
   public void processScriptWarning(ScriptWarning warning);
}
