package sleep.parser;

import java.util.*;
import java.lang.reflect.*;
import java.net.*;
import java.io.*;

/** This class mantains a cache of imported package names and resolve classes for a Sleep parser.
    The existence of this class also allows these imports to be shared between parser instances.  Value is allowing
    dynamically parsed code like eval, expr, compile_clousre etc.. to inherit their parents imported class
    information. */
public class ImportManager
{
   protected List       imports   = new LinkedList();
   protected HashMap    classes   = new HashMap();

   /** Used by Sleep to import statement to save an imported package name. */
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

          if (returnValue == null || !returnValue.exists()) { throw new RuntimeException("jar file to import package from was not found!"); }

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
             throw new RuntimeException("imported class was not found");
       }
   }

   /** This method is used by Sleep to resolve a specific class (or at least try) */
   private Class resolveClass(String pack, String clas)
   {
       StringBuffer name = new StringBuffer();
       if (pack != null) { name.append(pack); name.append("."); }
       name.append(clas);

       try
       {
          return Class.forName(name.toString());
       }
       catch (Exception ex) { }

       return null;
   }

   /** A hack to add a jar to the system classpath courtesy of Ralph Becker. */
   private void addFile(File f)
   {
        try
        {
            URL url = f.toURL();

            URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class sysclass = java.net.URLClassLoader.class;

            Method method = sysclass.getDeclaredMethod( "addURL", new Class[] { URL.class } );
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
   public Class findImportedClass(String name)
   {
       if (classes.get(name) == null)
       {
          Class rv = null;
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

