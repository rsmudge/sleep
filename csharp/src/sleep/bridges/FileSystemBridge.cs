/*
 * Copyright 2002-2020 Raphael Mudge
 * Copyright 2020 Sebastian Ritter
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
 
using  sleep.engine;
using  sleep.engine.types;
using  sleep.interfaces;
using  sleep.runtime;

namespace sleep.bridges{
 
/** provides a bridge for accessing the local file system */
public class FileSystemBridge : Loadable, Function, sleep.interfaces.Predicate
{
    public void scriptUnloaded(ScriptInstance aScript)
    {
    }

    public void scriptLoaded (ScriptInstance aScript)
    {
        java.util.Hashtable<Object,Object> temp = aScript.getScriptEnvironment().getEnvironment();

        // predicates
        temp.put("-exists",   this);
        temp.put("-canread",  this);
        temp.put("-canwrite", this);
        temp.put("-isDir",    this);
        temp.put("-isFile",   this);
        temp.put("-isHidden", this);

        // functions
        temp.put("&createNewFile",   this);
        temp.put("&deleteFile",      this);

        temp.put("&chdir",               this);
        temp.put("&cwd",                 this);
        temp.put("&getCurrentDirectory", this);

        temp.put("&getFileName",     new getFileName());
        temp.put("&getFileProper",   new getFileProper());
        temp.put("&getFileParent",   new getFileParent());
        temp.put("&lastModified",    new lastModified());
        temp.put("&lof",             new lof());
        temp.put("&ls",              new listFiles());
        temp.put("&listRoots",       temp.get("&ls"));
        temp.put("&mkdir",           this);
        temp.put("&rename",          this);
        temp.put("&setLastModified", this);
        temp.put("&setReadOnly",     this);
    }

    public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
    {
        if (n.equals("&createNewFile"))
        {
           try
           {
              java.io.File a = BridgeUtilities.getFile(l, i);
              if (a.createNewFile())
              {
                 return SleepUtils.getScalar(1);
              }
           }
           catch (java.lang.Exception ex) { i.getScriptEnvironment().flagError(ex); }
        }
        else if (n.equals("&cwd") || n.equals("&getCurrentDirectory"))
        {
           return SleepUtils.getScalar(i.cwd());
        }
        else if (n.equals("&chdir"))
        {
           i.chdir(BridgeUtilities.getFile(l, i));
        }
        else if (n.equals("&deleteFile"))
        {
           java.io.File a = BridgeUtilities.getFile(l, i);
           if (a.delete())
           {
              return SleepUtils.getScalar(1);
           }
        }
        else if (n.equals("&mkdir"))
        {
           java.io.File a = BridgeUtilities.getFile(l, i);
           if (a.mkdirs())
           {
              return SleepUtils.getScalar(1);
           }
        }
        else if (n.equals("&rename"))
        {
           java.io.File a = BridgeUtilities.getFile(l, i);
           java.io.File b = BridgeUtilities.getFile(l, i);
           if (a.renameTo(b))
           {
              return SleepUtils.getScalar(1);
           }
        }
        else if (n.equals("&setLastModified"))
        {
           java.io.File a = BridgeUtilities.getFile(l, i);
           long b = BridgeUtilities.getLong(l);

           if (a.setLastModified(b))
           {
              return SleepUtils.getScalar(1);
           }
        }
        else if (n.equals("&setReadOnly"))
        {
           java.io.File a = BridgeUtilities.getFile(l, i);

           if (a.setReadOnly())
           {
              return SleepUtils.getScalar(1);
           }
           return SleepUtils.getEmptyScalar();
        }

        return SleepUtils.getEmptyScalar();
    }

    private class getFileName : Function
    {
       public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
       {
           java.io.File a = BridgeUtilities.getFile(l, i);
           return SleepUtils.getScalar(a.getName());
       }
    }

    private class getFileProper : Function
    {
       public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
       {
           java.io.File start = BridgeUtilities.getFile(l, i);

           while (!l.isEmpty())
           {
              start = new java.io.File(start, l.pop().toString());
           }

           return SleepUtils.getScalar(start.getAbsolutePath());
       }
    }

    private class getFileParent : Function
    {
       public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
       {
           java.io.File a = BridgeUtilities.getFile(l, i);
           return SleepUtils.getScalar(a.getParent());
       }
    }

    private class lastModified : Function
    {
       public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
       {
           java.io.File a = BridgeUtilities.getFile(l, i);
           return SleepUtils.getScalar(a.lastModified());
       }
    }

    private class lof : Function
    {
       public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
       {
           java.io.File a = BridgeUtilities.getFile(l, i);
           return SleepUtils.getScalar(a.length());
       }
    }

    private class listFiles : Function
    {
       public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
       {
           java.io.File[] files;
 
           if (l.isEmpty() && n.equals("&listRoots"))
           {
              files = java.io.File.listRoots();
           }
           else 
           {
              java.io.File a = BridgeUtilities.getFile(l, i);
              files = a.listFiles();
           }

           java.util.LinkedList<Object> temp = new java.util.LinkedList<Object>();

           if (files != null)
           {
              for (int x = 0; x < files.Length; x++)
              {
                 temp.add(files[x].getAbsolutePath());
              }
           }

           return SleepUtils.getArrayWrapper(temp);
       }
    }

    public bool decide(String n, ScriptInstance i, java.util.Stack<Object> l)
    {
       java.io.File a = BridgeUtilities.getFile(l, i);

       if (n.equals("-canread")) { return a.canRead(); }
       else if (n.equals("-canwrite")) { return a.canWrite(); }
       else if (n.equals("-exists")) { return a.exists(); }
       else if (n.equals("-isDir")) { return a.isDirectory(); }
       else if (n.equals("-isFile")) { return a.isFile(); }
       else if (n.equals("-isHidden")) { return a.isHidden(); }

       return false;
    }
}
}