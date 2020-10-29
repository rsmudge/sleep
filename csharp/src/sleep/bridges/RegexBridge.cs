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

using  sleep.taint;

using  sleep.parser;


namespace sleep.bridges{
 /** Provides a bridge between Java's regex API and sleep.  Rock on */
public class RegexBridge : Loadable
{
    private static java.util.Map<Object,Object> patternCache = java.util.Collections<Object>.synchronizedMap(new Cache(128));

    private class Cache : java.util.LinkedHashMap<Object,Object>
    {
       protected int count;

       public Cache(int _count) :
          base(11, 0.75f, true){
          this.count = _count;
       }

       protected bool removeEldestEntry(java.util.MapNS.Entry<Object,Object> eldest)
       {
          return (size() >= count);
       }
    }
 
    static RegexBridge()
    {
       ParserConfig.addKeyword("ismatch");
       ParserConfig.addKeyword("hasmatch");
    }

    private static java.util.regex.Pattern getPattern(String pattern)
    {
       java.util.regex.Pattern temp = (java.util.regex.Pattern)patternCache.get(pattern);

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
        java.util.Hashtable<Object,Object> temp = aScript.getScriptEnvironment().getEnvironment();

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

    private class ffind : Function
    {
       public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
       {
          String stringJ = BridgeUtilities.getString(l, "");
          String patterns = BridgeUtilities.getString(l, "");

          java.util.regex.Pattern pattern  = RegexBridge.getPattern(patterns);
          java.util.regex.Matcher matchit  = pattern.matcher(stringJ);
          int     start    = BridgeUtilities.normalize(BridgeUtilities.getInt(l, 0), stringJ.length());
          
          bool check    = matchit.find(start);

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

    private static String key(String text, java.util.regex.Pattern p)
    {
       java.lang.StringBuffer buffer = new java.lang.StringBuffer(text.length() + p.pattern().length() + 1);
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
    private static Scalar getMatcher(ScriptEnvironment env, String key, String text, java.util.regex.Pattern p)
    {
       java.util.Map<Object,Object> matchers = (java.util.Map<Object,Object>)env.getContextMetadata("matchers");

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

    private class isMatch : sleep.interfaces.Predicate, Function
    {
       public bool decide(String n, ScriptInstance i, java.util.Stack<Object> l)
       {
          bool rv;

          /* do some tainter checking plz */
          Scalar bb = (Scalar)l.pop(); // PATTERN
          Scalar aa = (Scalar)l.pop(); // TEXT TO MATCH AGAINST

          java.util.regex.Pattern pattern = RegexBridge.getPattern(bb.toString());

          Scalar  container = null;
          java.util.regex.Matcher matcher   = null;

          if (n.equals("hasmatch"))
          {
              String key_ = key(aa.toString(), pattern);

              container = getMatcher(i.getScriptEnvironment(), key_, aa.toString(), pattern);
              matcher  = (java.util.regex.Matcher)container.objectValue();

              rv = matcher.find();

              if (!rv)
              {
                 java.util.Map<Object,Object> matchers = (java.util.Map<Object,Object>)i.getScriptEnvironment().getContextMetadata("matchers");
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

       public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
       {
          Scalar value = SleepUtils.getArrayScalar();            

          Scalar container = getLastMatcher(i.getScriptEnvironment());

          if (!SleepUtils.isEmptyScalar(container))
          {
             java.util.regex.Matcher matcher = (java.util.regex.Matcher)container.objectValue();

             int count = matcher.groupCount();  

             for (int x = 1; x <= count; x++)
             {
                value.getArray().push(SleepUtils.getScalar(matcher.group(x)));
             }
          }

          return TaintUtils.isTainted(container) ? TaintUtils.taintAll(value) : value;
       }
    }

    private class getMatches : Function
    {
       public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
       {
          String a = ((Scalar)l.pop()).toString();
          String b = ((Scalar)l.pop()).toString();
          int    c = BridgeUtilities.getInt(l, -1);
          int    d = BridgeUtilities.getInt(l, c);

          java.util.regex.Pattern pattern = RegexBridge.getPattern(b);
          java.util.regex.Matcher matcher = pattern.matcher(a);
   
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

    private class split : Function
    {
       public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
       {
          String a = ((Scalar)l.pop()).toString();
          String b = ((Scalar)l.pop()).toString();

          java.util.regex.Pattern pattern  = RegexBridge.getPattern(a);

          String[] results = l.isEmpty() ? pattern.split(b) : pattern.split(b, BridgeUtilities.getInt(l, 0));
          
          Scalar array = SleepUtils.getArrayScalar();

          for (int x = 0; x < results.Length; x++)
          {
             array.getArray().push(SleepUtils.getScalar(results[x]));
          }

          return array;
       }
    }

    private class join : Function
    {
       public Scalar evaluate(String n, ScriptInstance script, java.util.Stack<Object> l)
       {
          String      a = ((Scalar)l.pop()).toString();
          java.util.Iterator<Object>    i = BridgeUtilities.getIterator(l, script);

          java.lang.StringBuffer result = new java.lang.StringBuffer();

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

    private class rreplace : Function
    {
       public Scalar evaluate(String n, ScriptInstance script, java.util.Stack<Object> l)
       {
          String a = BridgeUtilities.getString(l, ""); // current
          String b = BridgeUtilities.getString(l, ""); // old
          String c = BridgeUtilities.getString(l, ""); // new
          int    d = BridgeUtilities.getInt(l, -1);

          java.lang.StringBuffer rv = new java.lang.StringBuffer();

          java.util.regex.Pattern pattern = RegexBridge.getPattern(b);
          java.util.regex.Matcher matcher = pattern.matcher(a);
       
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
}