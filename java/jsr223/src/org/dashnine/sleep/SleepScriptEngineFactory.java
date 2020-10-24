/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved. 
 * Use is subject to license terms.
 *
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met: Redistributions of source code 
 * must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other materials 
 * provided with the distribution. Neither the name of the Sun Microsystems nor the names of 
 * is contributors may be used to endorse or promote products derived from this software 
 * without specific prior written permission. 

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY 
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER 
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * SleepScriptEngineFactory.java
 * @author A. Sundararajan
 */

package org.dashnine.sleep;

import javax.script.*;
import java.util.*;

import sleep.runtime.*;

public class SleepScriptEngineFactory implements ScriptEngineFactory 
{
    public String getEngineName() 
    { 
        return "sleep";
    }

    public String getEngineVersion() 
    {
        return SleepUtils.SLEEP_RELEASE + "";
    }

    public List<String> getExtensions() 
    {
        return extensions;
    }

    public String getLanguageName() 
    {
        return "Sleep";
    }

    public String getLanguageVersion() 
    {
        return "2.1";
    }

    public String getMethodCallSyntax(String obj, String m, String... args) 
    {
        StringBuilder buf = new StringBuilder();
        buf.append('[');
        buf.append(obj);
        buf.append(' ');
        buf.append(m);
        buf.append(':');
        if (args.length != 0) 
        {
            int i = 0;
            for (; i < args.length - 1; i++) 
            {
                buf.append(args[i] + ", ");
            }
            buf.append(args[i]);
        }        
        buf.append(")");
        buf.append(']');
        return buf.toString();
    }

    public List<String> getMimeTypes() 
    {
        return mimeTypes;
    }

    public List<String> getNames() 
    {
        return names;
    }

    public String getOutputStatement(String toDisplay) 
    {
        StringBuilder buf = new StringBuilder();
        buf.append("print('");
        int len = toDisplay.length();
        for (int i = 0; i < len; i++) 
        {
            char ch = toDisplay.charAt(i);
            switch (ch) {
            case '\'':
                buf.append("\'");
                break;
            default:
                buf.append(ch);
                break;
            }
        }
        buf.append("')");
        return buf.toString();
    }

    public String getParameter(String key) 
    {
        if (key.equals(ScriptEngine.ENGINE)) {
            return getEngineName();
        } else if (key.equals(ScriptEngine.ENGINE_VERSION)) {
            return getEngineVersion();
        } else if (key.equals(ScriptEngine.NAME)) {
            return getEngineName();
        } else if (key.equals(ScriptEngine.LANGUAGE)) {
            return getLanguageName();
        } else if (key.equals(ScriptEngine.LANGUAGE_VERSION)) {
            return getLanguageVersion();
        } else if (key.equals("THREADING")) {
            return "MULTITHREADED";
        } else {
            return null;
        }
    } 

    public String getProgram(String... statements) 
    {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < statements.length; i++) {
            buf.append(statements[i]);
            buf.append(";\n");
        }
        return buf.toString();
    }

    public ScriptEngine getScriptEngine() {
        SleepScriptEngine engine = new SleepScriptEngine();
   	  engine.setFactory(this);
        return engine;
    }

    private static List<String> names;
    private static List<String> extensions;
    private static List<String> mimeTypes;
    static {
        names = new ArrayList<String>(3);
        names.add("sleep");
        names.add("Sleep");
        names.add("sl");
        names = Collections.unmodifiableList(names);
        extensions = new ArrayList<String>(1);
        extensions.add("sl");
        extensions = Collections.unmodifiableList(extensions);
        mimeTypes = new ArrayList<String>(0);
        mimeTypes = Collections.unmodifiableList(mimeTypes);
    }
}
