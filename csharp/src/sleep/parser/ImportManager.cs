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

namespace sleep.parser{

/** This class mantains a cache of imported package names and resolve classes for a Sleep parser.
    The existence of this class also allows these imports to be shared between parser instances.  Value is allowing
    dynamically parsed code like eval, expr, compile_clousre etc.. to inherit their parents imported class
    information. */
public class ImportManager
{
   protected java.util.List<Object>       imports   = new java.util.LinkedList<Object>();
   protected java.util.HashMap<Object,Object>    classes   = new java.util.HashMap<Object,Object>();

   /** Used by Sleep to using  statement to save an imported package name. */
   public void importPackage(String packagez, String from)
   {
       String pack, clas;

       if (packagez.indexOf(".") > -1)
       {
          clas = packagez.substring(packagez.lastIndexOf(".") + 1, packagez.length());
          pack = packagez.substring(0, packagez.lastIndexOf("."));
       }
       else
       {
          clas = packagez;
          pack = null;
       }

       /* resolve and setup our class loader for the specified jar file */

       if (from != null)
       {
          File returnValue = null;
          returnValue = ParserConfig.findJarFile(from);

          if (returnValue == null || !returnValue.exists()) { throw new RuntimeException("jar file to using  package from was not found!"); }

          addFile(returnValue);
       }

       /* handle importing our package */

       if (clas.equals("*"))
       {
          imports.add(pack);
       }
       else if (pack == null)
       {
          imports.add(packagez);
          Class found = resolveClass(null, packagez); /* try with no package to see if we have an anonymous class */
          classes.put(packagez, found);

          if (found == null)
             throw new RuntimeException("imported class was not found");
       }
       else
       {
          imports.add(packagez);
         
          Class found = findImportedClass(packagez);
          classes.put(clas, found);

          if (found == null)
             throw new java.lang.RuntimeException("imported class was not found");
       }
   }

   /** This method is used by Sleep to resolve a specific class (or at least try) */
   private Type resolveClass(String pack, String clas)
   {
       java.lang.StringBuffer name = new java.lang.StringBuffer();
       if (pack != null) { name.append(pack); name.append("."); }
       name.append(clas);

       try
       {
          return Class.forName(name.toString());
       }
       catch (java.lang.Exception ex) { }

       return null;
   }

   /** A hack to add a jar to the system classpath courtesy of Ralph Becker. */
   private void addFile(java.io.File f)
   {
        try
        {
            URL url = f.toURL();

            URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class sysclass = typeof(java.net.URLClassLoader);

            Method method = sysclass.getDeclaredMethod( "addURL", new Class[] { typeof(URL) } );
            method.setAccessible( true );
            method.invoke( sysloader, new Object[] { url } );
        }    
        catch(Throwable t)
        {    
            t.printStackTrace();
            throw new RuntimeException("Error, could not add "+f+" to system classloader - " + t.getMessage());
        }
   }

   /** Attempts to find a class, starts out with the passed in string itself, if that doesn't resolve then the string is 
       appended to each imported package to see where the class might exist */
   public Type findImportedClass(String name)
   {
       if (classes.get(name) == null)
       {
          Type rv = null;
          String clas, pack;

          if (name.indexOf(".") > -1)
          {
             clas = name.substring(name.lastIndexOf(".") + 1, name.length());
             pack = name.substring(0, name.lastIndexOf("."));

	     rv   = resolveClass(pack, clas);
          }
          else
          {
             rv = resolveClass(null, name); /* try with no package to see if we have an anonymous class */

             Iterator i = imports.iterator();
             while (i.hasNext() && rv == null)
             {
                rv = resolveClass((String)i.next(), name);
             }
          }

          // some friendly (really) debugging
/*          if (rv == null)
          {
             System.err.println("Argh: " + name + " is not an imported class");
             Thread.dumpStack();
          } */

          classes.put(name, rv);
       }
     
       return (Class)classes.get(name);
   }
}

}