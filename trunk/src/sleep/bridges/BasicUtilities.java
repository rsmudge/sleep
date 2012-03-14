/*
   SLEEP - Simple Language for Environment Extension Purposes
 .------------------------------.
 | sleep.bridges.BasicUtilities |_____________________________________________
 |                                                                            |
   Author: Raphael Mudge (raffi@hick.org)
           http://www.hick.org/~raffi/

   Description:
      provides some of the basic built in utility functions.

   Documentation:

   Changelog:

   * This software is distributed under the artistic license, see license.txt
     for more information. *

 |____________________________________________________________________________|
 */


package sleep.bridges;
 
import java.util.*;

import sleep.engine.*;
import sleep.interfaces.*;
import sleep.runtime.*;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;

import sleep.engine.types.*;
import java.lang.reflect.*; // for array casting stuff

import sleep.taint.*;

import sleep.parser.*;
import sleep.error.YourCodeSucksException;

/** implementation of basic utility functions */
public class BasicUtilities implements Function, Loadable, Predicate
{
    static
    {
       ParserConfig.addKeyword("isa");
       ParserConfig.addKeyword("in");
       ParserConfig.addKeyword("=~");
    }

    public void scriptUnloaded (ScriptInstance i)
    {
    }

    public void scriptLoaded (ScriptInstance i)
    {
        Hashtable temp = i.getScriptEnvironment().getEnvironment();
        //
        // functions
        //

        Function f_array = new array();
        Function f_hash  = new hash();

        temp.put("&array", f_array); 
        temp.put("&hash", f_hash);
        temp.put("&ohash", f_hash);
        temp.put("&ohasha", f_hash);
        temp.put("&@", f_array);
        temp.put("&%", f_hash);  

        // array & hashtable related
        temp.put("&concat", this);

        temp.put("&keys",  this);      // &keys(%hash) = @array
        temp.put("&size",  this);      // &size(@array) = <int>
        temp.put("&push",  this);      // &push(@array, $value) = $scalar
        temp.put("&pop",   this);      // &pop(@array) = $scalar
        temp.put("&add",   this);      // &pop(@array) = $scalar
        temp.put("&flatten",   this);      // &pop(@array) = $scalar
        temp.put("&clear", this);
        temp.put("&splice", this);
        temp.put("&subarray", this);
        temp.put("&sublist", this);
        temp.put("&copy",  new copy());
        temp.put("&setRemovalPolicy", this);
        temp.put("&setMissPolicy", this);

        temp.put("&untaint", TaintUtils.Sanitizer(this));
        temp.put("&taint", TaintUtils.Tainter(this));
 
        map map_f = new map();

        temp.put("&map",    map_f);
        temp.put("&filter",    map_f);

        Function f_cast = new f_cast();
        temp.put("&cast",    f_cast);
        temp.put("&casti",   f_cast);

        temp.put("&putAll", this);

        temp.put("&addAll", this);
        temp.put("&removeAll", this);
        temp.put("&retainAll", this);

        temp.put("&pushl", this);
        temp.put("&popl", this);
      
        temp.put("&search", this);
        temp.put("&reduce", this);
        temp.put("&values", this);
        temp.put("&remove", this);     // not safe within foreach loops (since they use an iterator, and remove throws an exception)
        temp.put("-istrue", this);    // predicate -istrue <Scalar>, determine wether or not the scalar is null or not.
        temp.put("-isarray", this);   
        temp.put("-ishash",  this); 
        temp.put("-isfunction", this);
        temp.put("-istainted", this);
        temp.put("isa", this);
        temp.put("in", this);
        temp.put("=~", this);
        temp.put("&setField", this);
        temp.put("&typeOf", this);
        temp.put("&newInstance", this);
        temp.put("&scalar", this);

        temp.put("&exit", this);
     
        SetScope scopeFunctions = new SetScope();

        temp.put("&local",    scopeFunctions);
        temp.put("&this",     scopeFunctions);
        temp.put("&global",     scopeFunctions);

        temp.put("&watch", this);

        temp.put("&debug", this);
        temp.put("&warn", this);
        temp.put("&profile", this);
        temp.put("&getStackTrace", this);

        temp.put("&reverse",  new reverse());      // @array2 = &reverse(@array) 
        temp.put("&removeAt", new removeAt());   // not safe within foreach loops yada yada yada...
        temp.put("&shift",    new shift());   // not safe within foreach loops yada yada yada...

        temp.put("&systemProperties",    new systemProperties());
        temp.put("&use",     TaintUtils.Sensitive(new f_use()));
        temp.put("&include", TaintUtils.Sensitive((Function)temp.get("&use")));
        temp.put("&checkError", this);

        // closure / function handle type stuff
        temp.put("&lambda",    new lambda());
        temp.put("&compile_closure", TaintUtils.Sensitive((Function)temp.get("&lambda")));
        temp.put("&let",    temp.get("&lambda"));

        function funcs = new function();
        temp.put("&function",  TaintUtils.Sensitive(funcs));
        temp.put("function",   temp.get("&function")); /* special form used by the compiler */
        temp.put("&setf",      funcs);
        temp.put("&eval",      TaintUtils.Sensitive(new eval()));
        temp.put("&expr",      TaintUtils.Sensitive((Function)temp.get("&eval")));

        // synchronization primitives...
        SyncPrimitives sync = new SyncPrimitives();
        temp.put("&semaphore", sync);
        temp.put("&acquire",   sync);
        temp.put("&release",   sync);

        temp.put("&invoke",    this);
        temp.put("&inline",    this);

        temp.put("=>",       new HashKeyValueOp());
    }

    private static class SyncPrimitives implements Function 
    {
       public Scalar evaluate(String n, ScriptInstance si, Stack l)
       {
          if (n.equals("&semaphore"))
          {
             int initial = BridgeUtilities.getInt(l, 1);
             return SleepUtils.getScalar(new Semaphore(initial));
          }
          else if (n.equals("&acquire"))
          {
             Semaphore sem = (Semaphore)BridgeUtilities.getObject(l);
             sem.P();
          }
          else if (n.equals("&release"))
          {
             Semaphore sem = (Semaphore)BridgeUtilities.getObject(l);
             sem.V();
          }

          return SleepUtils.getEmptyScalar();
       }
    }

    private static class HashKeyValueOp implements Operator
    {
        public Scalar operate(String name, ScriptInstance script, Stack locals)
        {
            Scalar identifier = (Scalar)locals.pop();
            Scalar value      = (Scalar)locals.pop();

            return SleepUtils.getScalar(new KeyValuePair(identifier, value));
        }
    }

    public boolean decide(String predName, ScriptInstance anInstance, Stack terms)
    {

       if (predName.equals("isa"))
       {
          Class  blah = BridgeUtilities.getClass(terms, null);
          Object bleh = BridgeUtilities.getObject(terms);
          return blah != null && blah.isInstance(bleh);          
       }
       else if (predName.equals("=~"))
       {
          Scalar right = BridgeUtilities.getScalar(terms);
          Scalar left  = BridgeUtilities.getScalar(terms);

          return left.sameAs(right);
       }
       else if (predName.equals("in"))
       {
          Scalar temp = BridgeUtilities.getScalar(terms);
 
          if (temp.getHash() != null)
          {
             String key = BridgeUtilities.getString(terms, "");
             return temp.getHash().getData().containsKey(key) && !SleepUtils.isEmptyScalar((Scalar)(temp.getHash().getData().get(key)));
          }
          else
          {
             Iterator iter = SleepUtils.getIterator(temp, anInstance);
             Scalar   left = BridgeUtilities.getScalar(terms);

             while (iter.hasNext())
             {
                Scalar right = (Scalar)iter.next();

                if (left.sameAs(right))
                {          
                   return true;
                }             
             }

             return false;
          }
       }
 
       Scalar value = (Scalar)terms.pop();
 
       // Times when a scalar is considered true:
       // - its value is not equal to 0
       // - its not null (string value is not "")
       //
       // Scalar - String intValue
       //   0       "0"      0         - false
       //  null     ""       0         - false
       //  "blah"   "blah"   0         - true
       //  "3"      "3"      3         - true
       //   
       if (predName.equals("-istrue"))
       {
          return SleepUtils.isTrueScalar(value);
       }

       if (predName.equals("-isfunction"))
          return SleepUtils.isFunctionScalar(value);

       if (predName.equals("-istainted"))
          return TaintUtils.isTainted(value);

       if (predName.equals("-isarray"))
          return value.getArray() != null;

       if (predName.equals("-ishash"))
          return value.getHash() != null;

       return false;
    }

    private static class f_use implements Function
    {
       private HashMap bridges = new HashMap();

       public Scalar evaluate(String n, ScriptInstance si, Stack l)
       {
          File   parent    = null;
          String className = "";
          Class  bridge    = null;

          if (l.size() == 2)
          {
             parent    = sleep.parser.ParserConfig.findJarFile(l.pop().toString());
             className = BridgeUtilities.getString(l, "");
          }
          else
          {
             Scalar obj = (Scalar)l.pop();
             if (obj.objectValue() instanceof Class && n.equals("&use"))
             {
                bridge = (Class)obj.objectValue();
             }
             else
             {
                File a      = sleep.parser.ParserConfig.findJarFile(obj.toString());

                parent      = a.getParentFile();
                className   = a.getName();
             }
          }

          if (parent != null && !parent.exists())
          {
             throw new IllegalArgumentException(n + ": could not locate source '" + parent + "'");
          }

          try
          {
             if (n.equals("&use"))
             {
                if (bridge == null)
                {
                   if (parent != null)
                   {
                      URLClassLoader loader = new URLClassLoader(new URL[] { parent.toURL() });
                      bridge = Class.forName(className, true, loader);
                   }
                   else
                   {
                      bridge = Class.forName(className);
                   }
                }

                Loadable temp;

                if (bridges.get(bridge) == null)
                {
                   temp = (Loadable)bridge.newInstance();
                   bridges.put(bridge, temp);
                }
                else
                {
                   temp = (Loadable)bridges.get(bridge);
                }

                temp.scriptLoaded(si);
             }
             else
             {
                Block          script;
                ScriptLoader   sloader = (ScriptLoader)si.getScriptEnvironment().getEnvironment().get("(isloaded)");
                InputStream    istream;
         
                Scalar incz = si.getScriptVariables().getScalar("$__INCLUDE__");
                if (incz == null)
                {
                   incz = SleepUtils.getEmptyScalar();
                   si.getScriptVariables().getGlobalVariables().putScalar("$__INCLUDE__", incz);
                }

                if (parent != null)
                {
                   File theFile = parent.isDirectory() ? new File(parent, className) : parent;

                   URLClassLoader loader = new URLClassLoader(new URL[] { parent.toURL() });
                   sloader.touch(className, theFile.lastModified());
                   si.associateFile(theFile); /* associate this included script with the current script instance */

                   istream = loader.getResourceAsStream(className);
                   incz.setValue( SleepUtils.getScalar(theFile) );
                }
                else
                {
                   File tempf = BridgeUtilities.toSleepFile(className, si);
                   sloader.touch(className, tempf.lastModified());
                   si.associateFile(tempf); /* associate this included script with the current script instance */

                   istream = new FileInputStream(tempf);
                   incz.setValue( SleepUtils.getScalar(tempf) );
                }

                if (istream != null)
                {
                   script = sloader.compileScript(className, istream);
                   SleepUtils.runCode(script, si.getScriptEnvironment());
                }
                else
                {
                   throw new IOException("unable to locate " + className + " from: " + parent);
                }
             }
          }
          catch (YourCodeSucksException yex)
          {
             si.getScriptEnvironment().flagError(yex);             
          }
          catch (Exception ex)
          {
             si.getScriptEnvironment().flagError(ex);             
          }

          return SleepUtils.getEmptyScalar();
       }
    }

    private static class array implements Function
    {
       public Scalar evaluate(String n, ScriptInstance si, Stack l)
       {
          Scalar value = SleepUtils.getArrayScalar();
           
          while (!l.isEmpty())
          {
             value.getArray().push(SleepUtils.getScalar(BridgeUtilities.getScalar(l)));
          }

          return value;
       }
    }

    private static class f_cast implements Function
    {
       public Scalar evaluate(String n, ScriptInstance si, Stack l)
       {
          Scalar value      = BridgeUtilities.getScalar(l);
          Scalar type       = BridgeUtilities.getScalar(l);

          if (n.equals("&casti"))
          {
             Class  atype = ObjectUtilities.convertScalarDescriptionToClass(type);

             if (atype != null)
             {
                Object tempo = ObjectUtilities.buildArgument(atype, value, si);
                return SleepUtils.getScalar(tempo);
             }
             else
             {
                throw new RuntimeException("&casti: '" + type + "' is an invalid primitive cast identifier");
             }
          }

          if (value.getArray() == null)
          {
             if (type.toString().charAt(0) == 'c')
             {
                return SleepUtils.getScalar((Object)value.toString().toCharArray());
             }             
             else if (type.toString().charAt(0) == 'b')
             {
                return SleepUtils.getScalar((Object)BridgeUtilities.toByteArrayNoConversion(value.toString()));
             }             

             return SleepUtils.getEmptyScalar();
          }

          if (l.size() == 0) { l.push(SleepUtils.getScalar(value.getArray().size())); }

          int dimensions[] = new int[l.size()];
          int totaldim     = 1;

          for (int x = 0; !l.isEmpty(); x++)
          {
             dimensions[x] = BridgeUtilities.getInt(l, 0);

             totaldim *= dimensions[x];
          }

          Object rv;

          Class atype = ObjectUtilities.convertScalarDescriptionToClass(type);

          if (atype == null)
              atype = ObjectUtilities.getArrayType(value, Object.class);

          Scalar flat = BridgeUtilities.flattenArray(value, null);

          if (totaldim != flat.getArray().size())
          {
             throw new RuntimeException("&cast: specified dimensions " + totaldim + " is not equal to total array elements " + flat.getArray().size());
          }

          rv = Array.newInstance(atype, dimensions);

          int current[] = new int[dimensions.length]; // defaults at 0, 0, 0

          /* special case, we're casting an empty array */
          if (flat.getArray().size() == 0)
          {
             return SleepUtils.getScalar(rv);
          }

          for (int x = 0; true; x++)
          {
             Object tempa = rv;

             //
             // find our index
             //
             for (int z = 0; z < (current.length - 1); z++)
             {
                tempa = Array.get(tempa, current[z]);
             }

             //
             // set our value
             //
             Object tempo = ObjectUtilities.buildArgument(atype, flat.getArray().getAt(x), si);
             Array.set(tempa, current[current.length - 1], tempo);

             //
             // increment our index step...
             //
             current[current.length - 1] += 1;

             for (int y = current.length - 1; current[y] >= dimensions[y]; y--)
             {
                if (y == 0)
                {
                   return SleepUtils.getScalar(rv); // we're done building the array at this point...
                }

                current[y] = 0;
                current[y-1] += 1;
             }
          }

       }
    }

    private static class function implements Function
    {
       public Scalar evaluate(String n, ScriptInstance si, Stack l)
       {
          if (n.equals("&function") || n.equals("function"))
          {
             String temp = BridgeUtilities.getString(l, "");

             if (temp.length() == 0 || temp.charAt(0) != '&')
                throw new IllegalArgumentException(n + ": requested function name must begin with '&'");

             return SleepUtils.getScalar(si.getScriptEnvironment().getFunction(temp));
          }
          else if (n.equals("&setf"))
          {
             String   temp = BridgeUtilities.getString(l, "&eh");
             Object   o    = BridgeUtilities.getObject(l);

             if (temp.charAt(0) == '&' && (o == null || o instanceof Function))
             {
                if (o == null)
                {
                   si.getScriptEnvironment().getEnvironment().remove(temp);
                }
                else
                {
                   si.getScriptEnvironment().getEnvironment().put(temp, o);
                }
             }
             else if (temp.charAt(0) != '&')
             {
                throw new IllegalArgumentException("&setf: invalid function name '" + temp + "'");
             }
             else if (o != null)
             {
                throw new IllegalArgumentException("&setf: can not set function " + temp + " to a " + o.getClass());
             }
          }

          return SleepUtils.getEmptyScalar();
       }
    }

    private static class hash implements Function
    {
       public Scalar evaluate(String n, ScriptInstance si, Stack l)
       {
          Scalar value = null; 
          if (n.equals("&ohash"))
          {
              value = SleepUtils.getOrderedHashScalar();
          }
          else if (n.equals("&ohasha"))
          {
              value = SleepUtils.getAccessOrderedHashScalar();
          }
          else
          {
              value = SleepUtils.getHashScalar();
          }
           
          while (!l.isEmpty())
          {
             KeyValuePair kvp = BridgeUtilities.getKeyValuePair(l);
 
             Scalar blah = value.getHash().getAt(kvp.getKey());
             blah.setValue(kvp.getValue());
          }

          return value;
       }
    }

    private static class lambda implements Function
    {
       public Scalar evaluate(String n, ScriptInstance si, Stack l)
       {
          SleepClosure value;
          SleepClosure temp;

          if (n.equals("&lambda"))
          {
             temp  = BridgeUtilities.getFunction(l, si);           
             value = new SleepClosure(si, temp.getRunnableCode());
          }
          else if (n.equals("&compile_closure"))
          {
             String code  = l.pop().toString();

             try 
             {
                 temp  = new SleepClosure(si, SleepUtils.ParseCode(code));
                 value = temp;
             }
             catch (YourCodeSucksException ex)
             {
                si.getScriptEnvironment().flagError(ex);
                return SleepUtils.getEmptyScalar();
             }
          }
          else
          {
             temp  = BridgeUtilities.getFunction(l, si);           
             value = temp;
          }
           
          Variable vars = value.getVariables();

          while (!l.isEmpty())
          {
             KeyValuePair kvp = BridgeUtilities.getKeyValuePair(l);

             if (kvp.getKey().toString().equals("$this"))
             {
                SleepClosure c = (SleepClosure)kvp.getValue().objectValue();
                value.setVariables(c.getVariables());
                vars = c.getVariables();
             }
             else
             {
                vars.putScalar(kvp.getKey().toString(), SleepUtils.getScalar(kvp.getValue()));
             }
          }

          return SleepUtils.getScalar(value);
       }
    }

    private static class map implements Function
    {
       public Scalar evaluate(String n, ScriptInstance si, Stack l)
       {
          SleepClosure temp  = BridgeUtilities.getFunction(l, si);           
          Iterator     i     = BridgeUtilities.getIterator(l, si);

          Scalar       rv     = SleepUtils.getArrayScalar();
          Stack        locals = new Stack();

          while (i.hasNext())
          {
             locals.push(i.next());

             Scalar val = temp.callClosure("eval", si, locals);

             if (!SleepUtils.isEmptyScalar(val) || n.equals("&map"))
             {
                rv.getArray().push(SleepUtils.getScalar(val));
             }

             locals.clear();
          }

          return rv;
       }
    }

    private static class copy implements Function
    {
       public Scalar evaluate(String n, ScriptInstance si, Stack l)
       {
          Scalar doit = BridgeUtilities.getScalar(l);

          if (doit.getArray() != null || SleepUtils.isFunctionScalar(doit))
          {
             Scalar      value = SleepUtils.getArrayScalar();
             Iterator    i     = doit.getArray() == null ? SleepUtils.getFunctionFromScalar(doit, si).scalarIterator() : doit.getArray().scalarIterator();

             while (i.hasNext())
             {
                value.getArray().push(SleepUtils.getScalar((Scalar)i.next()));
             }

             return value;
          }
          else if (doit.getHash() != null)
          {
              Scalar value = SleepUtils.getHashScalar();
              Iterator i = doit.getHash().keys().scalarIterator();
              while (i.hasNext())
              {
                 Scalar key = (Scalar)i.next();
                 Scalar temp = value.getHash().getAt(key);
                 temp.setValue(doit.getHash().getAt(key));
              }

              return value;
          }
          else
          {
              return SleepUtils.getScalar(doit);
          }
       }
    }

    private static class removeAt implements Function
    {
       public Scalar evaluate(String n, ScriptInstance si, Stack l)
       {
          Scalar value = (Scalar)l.pop();

          if (value.getArray() != null)
          {
             while (!l.isEmpty())
             {             
                value.getArray().remove(BridgeUtilities.normalize(BridgeUtilities.getInt(l, 0), value.getArray().size()));
             }
          }
          else if (value.getHash() != null)
          {
             while (!l.isEmpty())
             {
                Scalar remove = value.getHash().getAt((Scalar)l.pop()); /* set each key to null to remove */
                remove.setValue(SleepUtils.getEmptyScalar());
             }
          }

          return SleepUtils.getEmptyScalar();
       }
    }

    private static class shift implements Function
    {
       public Scalar evaluate(String n, ScriptInstance si, Stack l)
       {
          ScalarArray value = BridgeUtilities.getArray(l);
          return value.remove(0);
       }
    }

    private static class reverse implements Function
    {
       public Scalar evaluate(String n, ScriptInstance si, Stack l)
       {
          Scalar value = SleepUtils.getArrayScalar();
          Iterator  i  = BridgeUtilities.getIterator(l, si);

          while (i.hasNext())
          {
             value.getArray().add(SleepUtils.getScalar((Scalar)i.next()), 0);
          }

          return value;
       }          
    }

    private static class SetScope implements Function
    {
       private java.util.regex.Pattern splitter = java.util.regex.Pattern.compile("\\s+");

       public Scalar evaluate(String n, ScriptInstance i, Stack l)
       {
          Variable level = null;

          if (n.equals("&local")) { level = i.getScriptVariables().getLocalVariables(); }
          else if (n.equals("&this")) { level = i.getScriptVariables().getClosureVariables(); }
          else if (n.equals("&global")) { level = i.getScriptVariables().getGlobalVariables(); }

          String temp = l.pop().toString();

          if (level == null)
              return SleepUtils.getEmptyScalar(); 

          String vars[] = splitter.split(temp); 
          for (int x = 0; x < vars.length; x++)
          {
             if (level.scalarExists(vars[x]))
             {
                // do nothing...
             }
             else if (vars[x].charAt(0) == '$')
             {
                i.getScriptVariables().setScalarLevel(vars[x], SleepUtils.getEmptyScalar(), level);
             }
             else if (vars[x].charAt(0) == '@')
             {
                i.getScriptVariables().setScalarLevel(vars[x], SleepUtils.getArrayScalar(), level);
             }
             else if (vars[x].charAt(0) == '%')
             {
                i.getScriptVariables().setScalarLevel(vars[x], SleepUtils.getHashScalar(), level);
             }
             else
             {
                throw new IllegalArgumentException(n + ": malformed variable name '" + vars[x] + "' from '" + temp + "'");
             }
          }

          return SleepUtils.getEmptyScalar();
       }
    }

    private static class systemProperties implements Function
    {
       public Scalar evaluate(String n, ScriptInstance i, Stack l)
       {
          return SleepUtils.getHashWrapper(System.getProperties());
       }
    }

    private static class eval implements Function
    {
       public Scalar evaluate(String n, ScriptInstance i, Stack l)
       {
          String code  = l.pop().toString();

          try 
          {
             if (n.equals("&eval"))
             {
                Scalar temp = SleepUtils.getScalar(i.getScriptEnvironment().evaluateStatement(code));
                return temp;
             }
             else
             {
                Scalar temp = SleepUtils.getScalar(i.getScriptEnvironment().evaluateExpression(code));
                return temp;
             }
          }
          catch (YourCodeSucksException ex)
          {
             i.getScriptEnvironment().flagError(ex);
             return SleepUtils.getEmptyScalar();
          }
       }
    }

    public Scalar evaluate(String n, ScriptInstance i, Stack l)
    {
       if (l.isEmpty() && n.equals("&remove"))
       {
          Stack iterators = (Stack)(i.getScriptEnvironment().getContextMetadata("iterators"));

          if (iterators == null || iterators.isEmpty())
          {
             throw new RuntimeException("&remove: no active foreach loop to remove element from");
          }
          else
          {
             sleep.engine.atoms.Iterate.IteratorData d = (sleep.engine.atoms.Iterate.IteratorData)iterators.peek();  
             d.iterator.remove();
             d.count = d.count - 1;
             return d.source;
          }
       }
       else if (n.equals("&watch"))
       {
          Variable level;
          String temp = BridgeUtilities.getString(l, "");       
          String vars[] = temp.split(" "); 
          for (int x = 0; x < vars.length; x++)
          {
             level = i.getScriptVariables().getScalarLevel(vars[x], i);
             if (level != null)
             {
                WatchScalar watch = new WatchScalar(vars[x], i.getScriptEnvironment());
                watch.setValue(level.getScalar(vars[x]));
                i.getScriptVariables().setScalarLevel(vars[x], watch, level);
             }
             else
             {
                throw new IllegalArgumentException(vars[x] + " must already exist in a scope prior to watching");
             }
          }
       }
       else if (n.equals("&scalar"))
       {
          return ObjectUtilities.BuildScalar(true, BridgeUtilities.getObject(l));
       }
       else if (n.equals("&untaint") || n.equals("&taint"))
       {
          /* the actual tainting / untaing of this value takes place in the wrapper specified in the bridge itself */
          return !l.isEmpty() ? (Scalar)l.pop() : SleepUtils.getEmptyScalar();
       }
       else if (n.equals("&newInstance"))
       {
          Scalar top = BridgeUtilities.getScalar(l);
      
          if (top.getArray() != null)
          {
             Class        clz[]   = (Class[])ObjectUtilities.buildArgument(Class[].class, top, i);
             SleepClosure closure = (SleepClosure)BridgeUtilities.getObject(l);          

             return SleepUtils.getScalar(ProxyInterface.BuildInterface(clz, closure, i));
          }
          else
          {
             Class        clz     = (Class)top.objectValue();
             SleepClosure closure = (SleepClosure)BridgeUtilities.getObject(l);          

             return SleepUtils.getScalar(SleepUtils.newInstance(clz, closure, i));
          }
       }
       else if (n.equals("&typeOf"))
       {
          Scalar s = BridgeUtilities.getScalar(l);
          if (s.getArray() != null) { return SleepUtils.getScalar(s.getArray().getClass()); }
          if (s.getHash() != null) { return SleepUtils.getScalar(s.getHash().getClass()); }
          return SleepUtils.getScalar(s.getActualValue().getType());
       }
       else if (n.equals("&inline"))
       {
          SleepClosure c = BridgeUtilities.getFunction(l, i);
          c.getRunnableCode().evaluate(i.getScriptEnvironment());
          return SleepUtils.getEmptyScalar();
       }
       else if (n.equals("&invoke")) 
       {
          Map params = BridgeUtilities.extractNamedParameters(l);

          SleepClosure c    = BridgeUtilities.getFunction(l, i);
          Stack        args = new Stack();
          Iterator iter     = BridgeUtilities.getIterator(l, i);
          while (iter.hasNext()) { args.add(0, iter.next()); }

          String message    = BridgeUtilities.getString(l, null);

          /* parameters option */
          if (params.containsKey("parameters"))
          {
             Scalar   h = (Scalar)params.get("parameters");

             Iterator it = h.getHash().keys().scalarIterator();
             while (it.hasNext())
             {
                Scalar key = (Scalar)it.next();
                KeyValuePair temp = new KeyValuePair(key, h.getHash().getAt(key));
                args.add(0, SleepUtils.getScalar(temp));
             }
          }

          /* message option */
          if (params.containsKey("message"))
          {
             message = params.get("message").toString();
          }
 
          Variable old = c.getVariables();

          /* environment option */
          if (params.containsKey("$this"))
          {
             SleepClosure t = (SleepClosure)((Scalar)params.get("$this")).objectValue();
             c.setVariables(t.getVariables());
          }

          Scalar rv = c.callClosure(message, i, args);
          c.setVariables(old);
          return rv;
       }
       else if (n.equals("&checkError"))
       {
          Scalar value = BridgeUtilities.getScalar(l);
          value.setValue(i.getScriptEnvironment().checkError());           
          return value;
       }
       else if (n.equals("&profile"))
       {
          return SleepUtils.getArrayWrapper(i.getProfilerStatistics());
       }
       else if (n.equals("&getStackTrace"))
       {
          return SleepUtils.getArrayWrapper(i.getStackTrace());
       }
       else if (n.equals("&warn"))
       {
          /* for those looking at how to read current line number from an executing function, you can't.  this function
             is a special case.  the parser looks for &warn and adds an extra argument containing the current line number */
          i.fireWarning(BridgeUtilities.getString(l, "warning requested"), BridgeUtilities.getInt(l, -1));
          return SleepUtils.getEmptyScalar();
       }
       else if (n.equals("&debug"))
       {
          /* allow the script to programatically set the debug level */
          if (!l.isEmpty())
          {
             int flag = BridgeUtilities.getInt(l, 0);
             i.setDebugFlags(flag);
          }

          return SleepUtils.getScalar(i.getDebugFlags());
       }
       else if (n.equals("&flatten"))
       {
          return BridgeUtilities.flattenIterator(BridgeUtilities.getIterator(l, i), null);
       }
       else if (n.equals("&pushl") || n.equals("&popl"))
       {
          ScriptVariables vars = i.getScriptVariables();
 
          if (n.equals("&pushl"))
          {
             vars.pushLocalLevel();
          }
          else if (n.equals("&popl"))
          {
             if (vars.haveMoreLocals())
             {
                vars.popLocalLevel();
             }
             else
             {
                throw new RuntimeException("&popl: no more local frames exist"); 
             }
          }

          if (!l.isEmpty())
          {
             BridgeUtilities.initLocalScope(vars, vars.getLocalVariables(), l);
          }

          return SleepUtils.getEmptyScalar();
       }
       else if (n.equals("&concat"))
       {
          Scalar value = SleepUtils.getArrayScalar();

          while (!l.isEmpty())
          {
             Scalar temp = (Scalar)l.pop();

             if (temp.getArray() != null)
             {
                Iterator iter = temp.getArray().scalarIterator();
                while (iter.hasNext())
                {
                   value.getArray().push(SleepUtils.getScalar((Scalar)iter.next()));
                }
             }
             else
             {
                value.getArray().push(SleepUtils.getScalar(temp));
             }
          }

          return value;
       }

       /** Start of many array functions */

       Scalar value = BridgeUtilities.getScalar(l);

       if (n.equals("&push") && BridgeUtilities.expectArray(n, value))
       {
          Scalar pushed = null;
          while (!l.isEmpty())
          {
             pushed = (Scalar)l.pop();
             value.getArray().push(SleepUtils.getScalar(pushed));
          }
 
          return pushed == null ? SleepUtils.getEmptyScalar() : pushed;
       }
       else if ((n.equals("&retainAll") || n.equals("&removeAll")) && BridgeUtilities.expectArray(n, value))
       {
          ScalarArray a = value.getArray();
          ScalarArray b = BridgeUtilities.getArray(l);
          Scalar temp;    

          HashSet s = new HashSet();
          Iterator iter = b.scalarIterator();
          while (iter.hasNext())
          {
             temp = (Scalar)iter.next();
             s.add(temp.identity());
          }      

          iter = a.scalarIterator();
          while (iter.hasNext())
          {
             temp = (Scalar)iter.next();

             if (!s.contains(temp.identity()))
             {
                if (n.equals("&retainAll"))
                {
                   iter.remove();
                }
             }
             else
             {
                if (n.equals("&removeAll"))
                {
                   iter.remove();
                }
             }
          }

          return SleepUtils.getArrayScalar(a);
       }
       else if (n.equals("&addAll") && BridgeUtilities.expectArray(n, value))
       {
          ScalarArray a = value.getArray();
          ScalarArray b = BridgeUtilities.getArray(l);
    
          HashSet s = new HashSet();
          Iterator iter = a.scalarIterator();
          Scalar temp;

          while (iter.hasNext())
          {
             temp = (Scalar)iter.next();
             s.add(temp.identity());
          }      

          iter = b.scalarIterator();
          while (iter.hasNext())
          {
             temp = (Scalar)iter.next();

             if (!s.contains(temp.identity()))
             {
                a.push(SleepUtils.getScalar(temp));
             }
          }

          return SleepUtils.getArrayScalar(a);
       }
       else if (n.equals("&add") && value.getArray() != null)
       {
          Scalar item = BridgeUtilities.getScalar(l);
          int index   = BridgeUtilities.normalize(BridgeUtilities.getInt(l, 0), value.getArray().size() + 1);
          value.getArray().add(SleepUtils.getScalar(item), index);
          return value;
       }
       else if (n.equals("&add") && value.getHash() != null)
       {
          while (!l.isEmpty())
          {
             KeyValuePair kvp = BridgeUtilities.getKeyValuePair(l);

             Scalar blah = value.getHash().getAt(kvp.getKey());
             blah.setValue(kvp.getValue());
          }

          return value;
       }
       else if (n.equals("&splice") && BridgeUtilities.expectArray(n, value))
       {
          // splice(@old, @stuff, start, n to remove)
          /* normalize all of the parameters please */
       
          ScalarArray insert = BridgeUtilities.getArray(l);
          int         start  = BridgeUtilities.normalize(BridgeUtilities.getInt(l, 0), value.getArray().size());
          int         torem  = BridgeUtilities.getInt(l, insert.size()) + start;

          /* remove the specified elements please */

          int y = start;

          Iterator iter = value.getArray().scalarIterator();
          for (int x = 0; x < start && iter.hasNext(); x++) { iter.next(); }

          while (y < torem)
          {
             if (iter.hasNext())
             {
                iter.next();
                iter.remove();
             }

             y++;
          }

          /* insert some elements */

          ListIterator liter = (ListIterator)value.getArray().scalarIterator();
          for (int x = 0; x < start && liter.hasNext(); x++) { liter.next(); }
          
          Iterator j = insert.scalarIterator();
          while (j.hasNext())
          {
             Scalar ins = (Scalar)j.next();
             liter.add(ins);
          }

          return value;
       }
       else if (n.equals("&pop")  && BridgeUtilities.expectArray(n, value))
       {
          return value.getArray().pop();
       }
       else if (n.equals("&size") && value.getArray() != null) // &size(@array)
       {
          return SleepUtils.getScalar(value.getArray().size());
       }
       else if (n.equals("&size") && value.getHash() != null) // &size(@array)
       {
          return SleepUtils.getScalar(value.getHash().keys().size());
       }
       else if (n.equals("&clear"))
       {
          if (value.getArray() != null)
          {
             Iterator iter = value.getArray().scalarIterator();
             while (iter.hasNext())
             {
                iter.next();
                iter.remove();
             }
          }
          else if (value.getHash() != null)
          {
             value.setValue(SleepUtils.getHashScalar());
          }
          else
          {
             value.setValue(SleepUtils.getEmptyScalar());
          }
       }
       else if (n.equals("&search") && BridgeUtilities.expectArray(n, value))
       {
          SleepClosure f = BridgeUtilities.getFunction(l, i); 
          int start      = BridgeUtilities.normalize(BridgeUtilities.getInt(l, 0), value.getArray().size());
          int count      = 0;
          Stack locals   = new Stack();

          Iterator iter = value.getArray().scalarIterator();
          while (iter.hasNext())
          {
             Scalar temp = (Scalar)iter.next();

             if (start > 0)
             {
                start--;
                count++;
                continue;
             }            

             locals.push(SleepUtils.getScalar(count));
             locals.push(temp);
             Scalar val = f.callClosure("eval", i, locals);

             if (! SleepUtils.isEmptyScalar(val))
             {
                return val;
             }

             locals.clear();
             count++;
          }
       }
       else if (n.equals("&reduce") && SleepUtils.isFunctionScalar(value))
       {
          SleepClosure f    = SleepUtils.getFunctionFromScalar(value, i); 
          Stack locals      = new Stack();

          Iterator iter = BridgeUtilities.getIterator(l, i);

          Scalar a      = iter.hasNext() ? (Scalar)iter.next() : SleepUtils.getEmptyScalar();
          Scalar b      = iter.hasNext() ? (Scalar)iter.next() : SleepUtils.getEmptyScalar();
          Scalar temp   = null;

          locals.push(a);
          locals.push(b);

          a = f.callClosure("eval", i, locals);
 
          locals.clear();

          while (iter.hasNext())
          {
             b = (Scalar)iter.next();

             locals.push(b);
             locals.push(a);
             a = f.callClosure("eval", i, locals);

             locals.clear();
          }

          return a;
       }
       else if ((n.equals("&subarray") || n.equals("&sublist")) && BridgeUtilities.expectArray(n, value))
       {
          return sublist(value, BridgeUtilities.getInt(l, 0), BridgeUtilities.getInt(l, value.getArray().size()));
       }
       else if (n.equals("&remove"))
       {
          while (!l.isEmpty())
          {
             Scalar scalar = (Scalar)l.pop();

             if (value.getArray() != null)
             {
                value.getArray().remove(scalar);
             }
             else if (value.getHash() != null)
             {
                value.getHash().remove(scalar);
             }
          }

          return value;
       }
       else if (n.equals("&keys")) // &keys(%hash)
       {
          if (value.getHash() != null)
          {
             Scalar temp = SleepUtils.getEmptyScalar();
             temp.setValue(value.getHash().keys());
             return temp;
          }
       }
       else if (n.equals("&setRemovalPolicy") || n.equals("&setMissPolicy"))
       { 
          if (value.getHash() == null || !(value.getHash() instanceof OrderedHashContainer))
          {
             throw new IllegalArgumentException(n + ": expected an ordered hash, received: " + SleepUtils.describe(value));
          }
          
          SleepClosure function  = BridgeUtilities.getFunction(l, i);           
          OrderedHashContainer blah = (OrderedHashContainer)(value.getHash());
          if (n.equals("&setMissPolicy"))
          {
             blah.setMissPolicy(function);
          }
          else
          {
             blah.setRemovalPolicy(function);
          }       
       }
       else if (n.equals("&putAll"))
       {
          if (value.getHash() != null)
          {
             Iterator keys   = BridgeUtilities.getIterator(l, i);
             Iterator values = l.isEmpty() ? keys : BridgeUtilities.getIterator(l, i);

             while (keys.hasNext())
             {
                Scalar blah = value.getHash().getAt((Scalar)keys.next());
                if (values.hasNext())
                {
                   blah.setValue((Scalar)values.next());
                }
                else
                {
                   blah.setValue(SleepUtils.getEmptyScalar());
                }
             }
          }
          else if (value.getArray() != null)
          {
             Iterator temp = BridgeUtilities.getIterator(l, i);
             while (temp.hasNext())
             {
                Scalar next = (Scalar)temp.next();
                value.getArray().push(SleepUtils.getScalar(next));
             }
          }

          return value;
       }
       else if (n.equals("&values")) // &values(%hash)
       {
          if (value.getHash() != null)
          {
             Scalar temp = SleepUtils.getArrayScalar();

             if (l.isEmpty())
             {
                Iterator iter = value.getHash().getData().values().iterator();
                while (iter.hasNext())
                {
                   Scalar next = (Scalar)iter.next();

                   if (!SleepUtils.isEmptyScalar(next))
                   {
                      temp.getArray().push(SleepUtils.getScalar(next));
                   }
                }
             }
             else
             {
                Iterator iter = BridgeUtilities.getIterator(l, i);
                while (iter.hasNext())
                {
                   Scalar key = (Scalar)iter.next();
                   temp.getArray().push(SleepUtils.getScalar(value.getHash().getAt(key)));
                }
             }
             return temp;
          }
       }
       else if (n.equals("&exit"))
       {
          i.getScriptEnvironment().flagReturn(null, ScriptEnvironment.FLOW_CONTROL_THROW); /* a null throw will exit the interpreter */
          if (!SleepUtils.isEmptyScalar(value))
          {
             throw new RuntimeException(value.toString());
          }
       }
       else if (n.equals("&setField"))
       {
          // setField(class/object, "field", "value")

          Field  setMe  = null;
          Class  aClass = null;
          Object inst   = null;

          if (value.objectValue() == null)
          {
             throw new IllegalArgumentException("&setField: can not set field on a null object");
          }
          else if (value.objectValue() instanceof Class)
          {
             aClass = (Class)value.objectValue();
             inst   = null;
          }
          else
          {
             inst   = value.objectValue();
             aClass = inst.getClass();
          }

          while (!l.isEmpty())
          {
             KeyValuePair pair = BridgeUtilities.getKeyValuePair(l);

             String name = pair.getKey().toString();
             Scalar arg  = pair.getValue();

             try
             {
                try
                {
                   setMe = aClass.getDeclaredField(name);
                }
                catch (NoSuchFieldException nsfe)
                {
                   setMe = aClass.getField(name);
                }

                if (ObjectUtilities.isArgMatch(setMe.getType(), arg) != 0)
                {
                   setMe.setAccessible(true);
                   setMe.set(inst, ObjectUtilities.buildArgument(setMe.getType(), arg, i));
                }
                else
                {
                   throw new RuntimeException("unable to convert " + SleepUtils.describe(arg) + " to a " + setMe.getType());
                }
             }
             catch (NoSuchFieldException fex)
             {
                throw new RuntimeException("no field named " + name + " in " + aClass);
             }
             catch (RuntimeException rex) { throw (rex); }
             catch (Exception ex)
             {
                throw new RuntimeException("cannot set " + name + " in " + aClass + ": " + ex.getMessage());
             }
          }
       }

       return SleepUtils.getEmptyScalar();
    }

    private static Scalar sublist(Scalar value, int _start, int _end)
    { 
       int length = value.getArray().size();
       int start, end;

       start = BridgeUtilities.normalize(_start, length);
       end   = (_end < 0 ? _end + length : _end);
       end   = end <= length ? end : length;

       if (start > end)
       {
          throw new IllegalArgumentException("illegal subarray(" + SleepUtils.describe(value) + ", " + _start + " -> " + start + ", " + _end + " -> " + end + ")");
       }
  
       return SleepUtils.getArrayScalar(value.getArray().sublist(start, end));
    }
}
