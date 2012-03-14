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
