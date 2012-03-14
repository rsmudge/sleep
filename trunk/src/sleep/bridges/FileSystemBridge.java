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
package sleep.bridges;
 
import java.util.*;
import java.util.regex.*;

import sleep.engine.*;
import sleep.engine.types.*;

import sleep.interfaces.*;
import sleep.runtime.*;

import java.io.*;

/** provides a bridge for accessing the local file system */
public class FileSystemBridge implements Loadable, Function, Predicate
{
    public void scriptUnloaded(ScriptInstance aScript)
    {
    }

    public void scriptLoaded (ScriptInstance aScript)
    {
        Hashtable temp = aScript.getScriptEnvironment().getEnvironment();

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

    public Scalar evaluate(String n, ScriptInstance i, Stack l)
    {
        if (n.equals("&createNewFile"))
        {
           try
           {
              File a = BridgeUtilities.getFile(l, i);
              if (a.createNewFile())
              {
                 return SleepUtils.getScalar(1);
              }
           }
           catch (Exception ex) { i.getScriptEnvironment().flagError(ex); }
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
           File a = BridgeUtilities.getFile(l, i);
           if (a.delete())
           {
              return SleepUtils.getScalar(1);
           }
        }
        else if (n.equals("&mkdir"))
        {
           File a = BridgeUtilities.getFile(l, i);
           if (a.mkdirs())
           {
              return SleepUtils.getScalar(1);
           }
        }
        else if (n.equals("&rename"))
        {
           File a = BridgeUtilities.getFile(l, i);
           File b = BridgeUtilities.getFile(l, i);
           if (a.renameTo(b))
           {
              return SleepUtils.getScalar(1);
           }
        }
        else if (n.equals("&setLastModified"))
        {
           File a = BridgeUtilities.getFile(l, i);
           long b = BridgeUtilities.getLong(l);

           if (a.setLastModified(b))
           {
              return SleepUtils.getScalar(1);
           }
        }
        else if (n.equals("&setReadOnly"))
        {
           File a = BridgeUtilities.getFile(l, i);

           if (a.setReadOnly())
           {
              return SleepUtils.getScalar(1);
           }
           return SleepUtils.getEmptyScalar();
        }

        return SleepUtils.getEmptyScalar();
    }

    private static class getFileName implements Function
    {
       public Scalar evaluate(String n, ScriptInstance i, Stack l)
       {
           File a = BridgeUtilities.getFile(l, i);
           return SleepUtils.getScalar(a.getName());
       }
    }

    private static class getFileProper implements Function
    {
       public Scalar evaluate(String n, ScriptInstance i, Stack l)
       {
           File start = BridgeUtilities.getFile(l, i);

           while (!l.isEmpty())
           {
              start = new File(start, l.pop().toString());
           }

           return SleepUtils.getScalar(start.getAbsolutePath());
       }
    }

    private static class getFileParent implements Function
    {
       public Scalar evaluate(String n, ScriptInstance i, Stack l)
       {
           File a = BridgeUtilities.getFile(l, i);
           return SleepUtils.getScalar(a.getParent());
       }
    }

    private static class lastModified implements Function
    {
       public Scalar evaluate(String n, ScriptInstance i, Stack l)
       {
           File a = BridgeUtilities.getFile(l, i);
           return SleepUtils.getScalar(a.lastModified());
       }
    }

    private static class lof implements Function
    {
       public Scalar evaluate(String n, ScriptInstance i, Stack l)
       {
           File a = BridgeUtilities.getFile(l, i);
           return SleepUtils.getScalar(a.length());
       }
    }

    private static class listFiles implements Function
    {
       public Scalar evaluate(String n, ScriptInstance i, Stack l)
       {
           File[] files;
 
           if (l.isEmpty() && n.equals("&listRoots"))
           {
              files = File.listRoots();
           }
           else 
           {
              File a = BridgeUtilities.getFile(l, i);
              files = a.listFiles();
           }

           LinkedList temp = new LinkedList();

           if (files != null)
           {
              for (int x = 0; x < files.length; x++)
              {
                 temp.add(files[x].getAbsolutePath());
              }
           }

           return SleepUtils.getArrayWrapper(temp);
       }
    }

    public boolean decide(String n, ScriptInstance i, Stack l)
    {
       File a = BridgeUtilities.getFile(l, i);

       if (n.equals("-canread")) { return a.canRead(); }
       else if (n.equals("-canwrite")) { return a.canWrite(); }
       else if (n.equals("-exists")) { return a.exists(); }
       else if (n.equals("-isDir")) { return a.isDirectory(); }
       else if (n.equals("-isFile")) { return a.isFile(); }
       else if (n.equals("-isHidden")) { return a.isHidden(); }

       return false;
    }
}
