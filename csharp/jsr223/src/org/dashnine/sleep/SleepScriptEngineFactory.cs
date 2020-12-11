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
using System;
using java = biz.ritter.javapi;
using javax = biz.ritter.javapix;

using  sleep.runtime;

namespace org.dashnine.sleep{

public class SleepScriptEngineFactory : javax.script.ScriptEngineFactory 
{
    public String getEngineName() 
    { 
        return "sleep";
    }

    public String getEngineVersion() 
    {
        return SleepUtils.SLEEP_RELEASE + "";
    }

    public java.util.List<String> getExtensions() 
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

    public String getMethodCallSyntax(String obj, String m, String args = "") 
    {
        java.lang.StringBuilder buf = new java.lang.StringBuilder();
        buf.append('[');
        buf.append(obj);
        buf.append(' ');
        buf.append(m);
        buf.append(':');
        if (args.Length != 0) 
        {
            int i = 0;
            for (; i < args.Length - 1; i++) 
            {
                buf.append(args[i] + ", ");
            }
            buf.append(args[i]);
        }        
        buf.append(")");
        buf.append(']');
        return buf.toString();
    }

    public java.util.List<String> getMimeTypes() 
    {
        return mimeTypes;
    }

    public java.util.List<String> getNames() 
    {
        return names;
    }

    public String getOutputStatement(String toDisplay) 
    {
        java.lang.StringBuilder buf = new java.lang.StringBuilder();
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
        if (key.equals(javax.script.ScriptEngine.ENGINE)) {
            return getEngineName();
        } else if (key.equals(javax.script.ScriptEngine.ENGINE_VERSION)) {
            return getEngineVersion();
        } else if (key.equals(javax.script.ScriptEngine.NAME)) {
            return getEngineName();
        } else if (key.equals(javax.script.ScriptEngine.LANGUAGE)) {
            return getLanguageName();
        } else if (key.equals(javax.script.ScriptEngine.LANGUAGE_VERSION)) {
            return getLanguageVersion();
        } else if (key.equals("THREADING")) {
            return "MULTITHREADED";
        } else {
            return null;
        }
    } 

    public String getProgram(String statements = "") 
    {
        java.lang.StringBuilder buf = new java.lang.StringBuilder();
        for (int i = 0; i < statements.Length; i++) {
            buf.append(statements[i]);
            buf.append(";\n");
        }
        return buf.toString();
    }

    public javax.script.ScriptEngine getScriptEngine() {
        SleepScriptEngine engine = new SleepScriptEngine();
   	  engine.setFactory(this);
        return engine;
    }

    private static java.util.List<String> names;
    private static java.util.List<String> extensions;
    private static java.util.List<String> mimeTypes;
    static SleepScriptEngineFactory(){
        names = new java.util.ArrayList<String>(3);
        names.add("sleep");
        names.add("Sleep");
        names.add("sl");
        names = java.util.Collections<String>.unmodifiableList(names);
        extensions = new java.util.ArrayList<String>(1);
        extensions.add("sl");
        extensions = java.util.Collections<String>.unmodifiableList(extensions);
        mimeTypes = new java.util.ArrayList<String>(0);
        mimeTypes = java.util.Collections<String>.unmodifiableList(mimeTypes);
    }
}
}