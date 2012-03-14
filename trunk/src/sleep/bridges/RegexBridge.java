/*
   SLEEP - Simple Language for Environment Extension Purposes
 .---------------------------.
 | sleep.bridges.RegexBridge |_______________________________________________
 |                                                                            |
   Author: Raphael Mudge (raffi@hick.org)
           http://www.hick.org/~raffi/

   Description:
      bridges java's regex API into sleep...

   Documentation:

   Changelog:
      1.8.04 - initially created.

   * This software is distributed under the artistic license, see license.txt
     for more information. *

 |____________________________________________________________________________|
 */

package sleep.bridges;
 
import java.util.*;
import java.util.regex.*;

import sleep.engine.*;
import sleep.engine.types.*;

import sleep.interfaces.*;
import sleep.runtime.*;

import sleep.taint.*;

import sleep.parser.ParserConfig;

/** Provides a bridge between Java's regex API and sleep.  Rock on */
public class RegexBridge implements Loadable
{
    private static Map patternCache = Collections.synchronizedMap(new Cache(128));

    private static class Cache extends LinkedHashMap
    {
       protected int count;

       public Cache(int count)
       {
          super(11, 0.75f, true);
          this.count = count;
       }

       protected boolean removeEldestEntry(Map.Entry eldest)
       {
          return (size() >= count);
       }
    }
 
    static
    {
       ParserConfig.addKeyword("ismatch");
       ParserConfig.addKeyword("hasmatch");
    }

    private static Pattern getPattern(String pattern)
    {
       Pattern temp = (Pattern)patternCache.get(pattern);

       if (temp != null)
       {
          return temp;
       }
       else
       {
          temp = Pattern.compile(pattern);
          patternCache.put(pattern, temp);

          return temp;
       }
    }

    public void scriptUnloaded(ScriptInstance aScript)
    {
    }

    public void scriptLoaded (ScriptInstance aScript)
    {
        Hashtable temp = aScript.getScriptEnvironment().getEnvironment();

        isMatch matcher = new isMatch();

        // predicates
        temp.put("ismatch", matcher);
        temp.put("hasmatch", matcher);

        // functions
        temp.put("&matched", matcher);
        temp.put("&split", new split());
        temp.put("&join",  new join());
        temp.put("&matches", new getMatches());
        temp.put("&replace", new rreplace());
        temp.put("&find", new ffind());
    }

    private static class ffind implements Function
    {
       public Scalar evaluate(String n, ScriptInstance i, Stack l)
       {
          String string = BridgeUtilities.getString(l, "");
          String patterns = BridgeUtilities.getString(l, "");

          Pattern pattern  = RegexBridge.getPattern(patterns);
          Matcher matchit  = pattern.matcher(string);
          int     start    = BridgeUtilities.normalize(BridgeUtilities.getInt(l, 0), string.length());
          
          boolean check    = matchit.find(start);

          if (check)
          {
             i.getScriptEnvironment().setContextMetadata("matcher", SleepUtils.getScalar(matchit));
          }
          else
          {
             i.getScriptEnvironment().setContextMetadata("matcher", null);
          }

          return check ? SleepUtils.getScalar(matchit.start()) : SleepUtils.getEmptyScalar();
       }
    }

    private static String key(String text, Pattern p)
    {
       StringBuffer buffer = new StringBuffer(text.length() + p.pattern().length() + 1);
       buffer.append(text);
       buffer.append(p.pattern());

       return buffer.toString();
    }

    private static Scalar getLastMatcher(ScriptEnvironment env)
    {
       Scalar temp = (Scalar)env.getContextMetadata("matcher");
       return temp == null ? SleepUtils.getEmptyScalar() : temp;    
    }

    /** a helper utility to get the matcher out of the script environment */
    private static Scalar getMatcher(ScriptEnvironment env, String key, String text, Pattern p)
    {
       Map matchers = (Map)env.getContextMetadata("matchers");

       if (matchers == null)
       {
          matchers = new Cache(16);
          env.setContextMetadata("matchers", matchers);
       }       

       /* get our value */

       Scalar temp = (Scalar)matchers.get(key);

       if (temp == null)
       {
          temp = SleepUtils.getScalar(p.matcher(text));
          matchers.put(key, temp);
          return temp;
       }
       else
       {
          return temp;
       }
    }

    private static class isMatch implements Predicate, Function
    {
       public boolean decide(String n, ScriptInstance i, Stack l)
       {
          boolean rv;

          /* do some tainter checking plz */
          Scalar bb = (Scalar)l.pop(); // PATTERN
          Scalar aa = (Scalar)l.pop(); // TEXT TO MATCH AGAINST

          Pattern pattern = RegexBridge.getPattern(bb.toString());

          Scalar  container = null;
          Matcher matcher   = null;

          if (n.equals("hasmatch"))
          {
              String key = key(aa.toString(), pattern);

              container = getMatcher(i.getScriptEnvironment(), key, aa.toString(), pattern);
              matcher  = (Matcher)container.objectValue();

              rv = matcher.find();

              if (!rv)
              {
                 Map matchers = (Map)i.getScriptEnvironment().getContextMetadata("matchers");
                 if (matchers != null) { matchers.remove(key); }
              }
          }
          else
          {
              matcher = pattern.matcher(aa.toString());
              container = SleepUtils.getScalar(matcher);

              rv = matcher.matches();
          }


          /* check our taint value please */ 
          if (TaintUtils.isTainted(aa) || TaintUtils.isTainted(bb))
          {
             TaintUtils.taintAll(container);
          }

          /* set our matcher for retrieval by matched() later */
          i.getScriptEnvironment().setContextMetadata("matcher", rv ? container : null);

          return rv;
       }

       public Scalar evaluate(String n, ScriptInstance i, Stack l)
       {
          Scalar value = SleepUtils.getArrayScalar();            

          Scalar container = getLastMatcher(i.getScriptEnvironment());

          if (!SleepUtils.isEmptyScalar(container))
          {
             Matcher matcher = (Matcher)container.objectValue();

             int count = matcher.groupCount();  

             for (int x = 1; x <= count; x++)
             {
                value.getArray().push(SleepUtils.getScalar(matcher.group(x)));
             }
          }

          return TaintUtils.isTainted(container) ? TaintUtils.taintAll(value) : value;
       }
    }

    private static class getMatches implements Function
    {
       public Scalar evaluate(String n, ScriptInstance i, Stack l)
       {
          String a = ((Scalar)l.pop()).toString();
          String b = ((Scalar)l.pop()).toString();
          int    c = BridgeUtilities.getInt(l, -1);
          int    d = BridgeUtilities.getInt(l, c);

          Pattern pattern = RegexBridge.getPattern(b);
          Matcher matcher = pattern.matcher(a);
   
          Scalar value = SleepUtils.getArrayScalar();            

          int temp = 0;

          while (matcher.find())
          {
             int    count = matcher.groupCount();  

             if (temp == c) { value = SleepUtils.getArrayScalar(); }

             for (int x = 1; x <= count; x++)
             {
                value.getArray().push(SleepUtils.getScalar(matcher.group(x)));
             }

             if (temp == d) { return value; }

             temp++;
          }

          return value;
       }
    }

    private static class split implements Function
    {
       public Scalar evaluate(String n, ScriptInstance i, Stack l)
       {
          String a = ((Scalar)l.pop()).toString();
          String b = ((Scalar)l.pop()).toString();

          Pattern pattern  = RegexBridge.getPattern(a);

          String results[] = l.isEmpty() ? pattern.split(b) : pattern.split(b, BridgeUtilities.getInt(l, 0));
          
          Scalar array = SleepUtils.getArrayScalar();

          for (int x = 0; x < results.length; x++)
          {
             array.getArray().push(SleepUtils.getScalar(results[x]));
          }

          return array;
       }
    }

    private static class join implements Function
    {
       public Scalar evaluate(String n, ScriptInstance script, Stack l)
       {
          String      a = ((Scalar)l.pop()).toString();
          Iterator    i = BridgeUtilities.getIterator(l, script);

          StringBuffer result = new StringBuffer();

          if (i.hasNext())
          {
             result.append(i.next().toString());
          }

          while (i.hasNext())
          {
             result.append(a);
             result.append(i.next().toString());
          }

          return SleepUtils.getScalar(result.toString());
       }
    }

    private static class rreplace implements Function
    {
       public Scalar evaluate(String n, ScriptInstance script, Stack l)
       {
          String a = BridgeUtilities.getString(l, ""); // current
          String b = BridgeUtilities.getString(l, ""); // old
          String c = BridgeUtilities.getString(l, ""); // new
          int    d = BridgeUtilities.getInt(l, -1);

          StringBuffer rv = new StringBuffer();

          Pattern pattern = RegexBridge.getPattern(b);
          Matcher matcher = pattern.matcher(a);
       
          int matches = 0;

          while (matcher.find() && matches != d)
          {
             matcher.appendReplacement(rv, c);
             matches++;
          }

          matcher.appendTail(rv);

          return SleepUtils.getScalar(rv.toString());
       }
    }
}
