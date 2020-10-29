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
using  sleep.interfaces;
using  sleep.runtime;
using  sleep.parser;

namespace sleep.bridges{
 
/** provides basic string parsing facilities */
public class BasicStrings : Loadable, sleep.interfaces.Predicate
{
    static BasicStrings()
    {
        // if an operator followed by an expression could be mistaken for
        // a function call, then we need to register the operator as a keyword.
        // :)
        ParserConfig.addKeyword("x");
        ParserConfig.addKeyword("eq");
        ParserConfig.addKeyword("ne");
        ParserConfig.addKeyword("lt");
        ParserConfig.addKeyword("gt");
        ParserConfig.addKeyword("isin");
        ParserConfig.addKeyword("iswm");
        ParserConfig.addKeyword("cmp");
    }

    public void scriptUnloaded(ScriptInstance aScript)
    {
    }

    public void scriptLoaded (ScriptInstance aScript)
    {
        java.util.Hashtable<Object,Object> temp = aScript.getScriptEnvironment().getEnvironment();

        // functions
        temp.put("&left",   new func_left());
        temp.put("&right",  new func_right());

        temp.put("&charAt",  new func_charAt());
        temp.put("&byteAt",  temp.get("&charAt"));
        temp.put("&uc",      new func_uc());
        temp.put("&lc",      new func_lc());

        func_substr f_substr = new func_substr();
        temp.put("&substr",  f_substr);
        temp.put("&mid",  f_substr);

        temp.put("&indexOf",  new func_indexOf());
        temp.put("&lindexOf", temp.get("&indexOf"));
        temp.put("&strlen",  new func_strlen());
        temp.put("&strrep",  new func_strrep());
        temp.put("&replaceAt",  new func_replaceAt());

        temp.put("&tr",      new func_tr());

        temp.put("&asc",     new func_asc());
        temp.put("&chr",     new func_chr());

        temp.put("&sort",    new func_sort());

        func_sorters funky = new func_sorters();
        temp.put("&sorta",   funky);
        temp.put("&sortn",   funky);
        temp.put("&sortd",   funky);

        // predicates
        temp.put("eq", this);
        temp.put("ne", this);
        temp.put("lt", this);
        temp.put("gt", this);

        temp.put("-isletter", this);
        temp.put("-isnumber", this);

        temp.put("-isupper", this);
        temp.put("-islower", this);

        temp.put("isin", this);
        temp.put("iswm", new pred_iswm());  // I couldn't resist >)

        // operators
        temp.put(".", new oper_concat());
        temp.put("x", new oper_multiply());
        temp.put("cmp", new oper_compare());
        temp.put("<=>", new oper_spaceship());
    }

    public bool decide(String n, ScriptInstance i, java.util.Stack<Object> l)
    {
        if (l.size() == 1)
        {
           String a = BridgeUtilities.getString(l, "");

           if (n.equals("-isupper"))
           {
              return a.toUpperCase().equals(a);                   
           }
           else if (n.equals("-islower"))
           {
              return a.toLowerCase().equals(a);
           }
           else if (n.equals("-isletter"))
           {
              if (a.length() <= 0)
                 return false;

              for (int x = 0; x < a.length(); x++)
              {
                 if (! java.lang.Character.isLetter(a.charAt(x)) )
                 {
                    return false;
                 }
              }

              return true;
           }
           else if (n.equals("-isnumber"))
           {
              if (a.length() <= 0)
                 return false;

              if (a.indexOf('.') > -1 && a.indexOf('.') != a.lastIndexOf('.'))
                 return false;
   
              for (int x = 0; x < a.length(); x++)
              {
                 if (! java.lang.Character.isDigit(a.charAt(x)) && (a.charAt(x) != '.' || (x+1) >= a.length()) )
                 {
                    return false;
                 }
              } 
 
              return true;
           }
        }
        else
        {
           String b = BridgeUtilities.getString(l, "");
           String a = BridgeUtilities.getString(l, "");

           if (n.equals("eq"))
           {
              return a.equals(b);
           }
           else if (n.equals("ne"))
           {
              return ! a.equals(b);
           }
           else if (n.equals("isin"))
           {
              return b.indexOf(a) > -1;
           }
           else if (n.equals("gt"))
           {
              return a.compareTo(b) > 0;
           }
           else if (n.equals("lt"))
           {
              return a.compareTo(b) < 0;
           }
        }

        return false;
    }

    private class pred_iswm : sleep.interfaces.Predicate
    {
        public bool decide(String name, ScriptInstance script, java.util.Stack<Object> locals)
        {
           String b = locals.pop().toString();
           String a = locals.pop().toString();

           try
           {
           if ((a.length() == 0 || b.length() == 0) && a.length() != b.length())
              return false;

           int aptr = 0, bptr = 0, cptr;

           while (aptr < a.length())
           {
              if (a.charAt(aptr) == '*')
              {
                 bool greedy = ((aptr + 1) < a.length() && a.charAt(aptr + 1) == '*');

                 while (a.charAt(aptr) == '*')
                 {
                    aptr++;
                    if (aptr == a.length())
                    {
                       return true;
                    }
                 }

                 for (cptr = aptr; cptr < a.length() && a.charAt(cptr) != '?' && a.charAt(cptr) != '\\' && a.charAt(cptr) != '*'; cptr++) { } // body intentionally left empty.

                 if (cptr != aptr) // don't advance our bptr unless there is some non-wildcard pattern to look for next in the string
                 {
                    if (greedy)
                       cptr = b.lastIndexOf(a.substring(aptr, cptr)); 
                    else
                       cptr = b.indexOf(a.substring(aptr, cptr), bptr);

                    if (cptr == -1 || cptr < bptr) // < - require 0 or more chars, <= - requires 1 or more chars
                    {
                       return false;
                    }

                    bptr = cptr;
                 }

                 if (a.charAt(aptr) == '?') // if the current aptr is a ?, decrement so the loop can deal with it on the next round
                 {
                    aptr--;
                 }
              }
              else if (bptr >= b.length())
              {
                 return false;
              }
              else if (a.charAt(aptr) == '\\')
              {
                 aptr++;

                 if (aptr < a.length() && a.charAt(aptr) != b.charAt(bptr))
                    return false;
              }
              else if (a.charAt(aptr) != '?' && a.charAt(aptr) != b.charAt(bptr))
              {
                 return false;
              }

              aptr++;
              bptr++;
           }
           return (bptr == b.length());
           }
           catch (java.lang.Exception ex) { ex.printStackTrace(); }
     

           return false;
        }
     
    }

    private class func_left : Function
    {
        public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
        {
           String temp  = l.pop().toString();
           int    value = ((Scalar)l.pop()).intValue();

           return SleepUtils.getScalar(substring(n, temp, 0, value));
        }
    }

    private class func_tr : Function
    {
        public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
        {
           String old       = BridgeUtilities.getString(l, "");
           String pattern   = BridgeUtilities.getString(l, "");
           String mapper    = BridgeUtilities.getString(l, "");
           String optstr    = BridgeUtilities.getString(l, "");

           int options = 0; 

           if (optstr.indexOf('c') > -1) { options = options | Transliteration.OPTION_COMPLEMENT; }
           if (optstr.indexOf('d') > -1) { options = options | Transliteration.OPTION_DELETE; }
           if (optstr.indexOf('s') > -1) { options = options | Transliteration.OPTION_SQUEEZE; }

           Transliteration temp = Transliteration.compile(pattern, mapper, options);

           return SleepUtils.getScalar(temp.translate(old));
        }
    }

    private class func_right : Function
    {
        public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
        {
           String temp  = l.pop().toString();
           int    value = ((Scalar)l.pop()).intValue();

           return SleepUtils.getScalar(substring(n, temp, 0 - value, temp.length()));
        }
    }

    private class func_asc : Function
    {
        public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
        {
           return SleepUtils.getScalar((int)(BridgeUtilities.getString(l, "\u0000").charAt(0)));
        }
    }

    private class func_chr : Function
    {
        public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
        {
           return SleepUtils.getScalar(((char)BridgeUtilities.getInt(l))+"");
        }
    }


    private class func_uc : Function
    {
        public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
        {
           return SleepUtils.getScalar(l.pop().toString().toUpperCase());
        }
    }

    private class func_lc : Function
    {
        public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
        {
           return SleepUtils.getScalar(l.pop().toString().toLowerCase());
        }
    }

    private class func_strlen : Function
    {
        public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
        {
           return SleepUtils.getScalar(l.pop().toString().length());
        }
    }

    private class func_strrep : Function
    {
        public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
        {
           java.lang.StringBuffer work    = new java.lang.StringBuffer(BridgeUtilities.getString(l, ""));

           while (!l.isEmpty())
           {
              String       oldstr  = BridgeUtilities.getString(l, "");
              String       newstr  = BridgeUtilities.getString(l, "");
 
              if (oldstr.length() == 0) { continue; }

              int x      = 0;
              int oldlen = oldstr.length();
              int newlen = newstr.length();

              while ((x = work.indexOf(oldstr, x)) > -1)
              {
                 work.replace(x, x + oldlen, newstr);
                 x += newstr.length();
              }
           }

           return SleepUtils.getScalar(work.toString());
        }
    }

    private class func_replaceAt : Function
    {
        public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
        {
           java.lang.StringBuffer work    = new java.lang.StringBuffer(BridgeUtilities.getString(l, ""));
           String       nstr    = BridgeUtilities.getString(l, "");
           int          index   = BridgeUtilities.normalize(BridgeUtilities.getInt(l, 0), work.length());
           int          nchar   = BridgeUtilities.getInt(l, nstr.length());

           work.delete(index, index + nchar);
           work.insert(index, nstr);

           return SleepUtils.getScalar(work.toString());
        }
    }

    private class func_substr : Function
    {
        public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
        {
           String value = BridgeUtilities.getString(l, "");

           int start, stop;
           start = BridgeUtilities.getInt(l);

           if (n.equals("&mid"))
           {
              stop  = BridgeUtilities.getInt(l, value.length() - start) + start;
           }
           else
           {
              stop  = BridgeUtilities.getInt(l, value.length());
           }
                    
           return SleepUtils.getScalar(substring(n, value, start, stop));
        }
    }

    private class func_indexOf : Function
    {
        public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
        {
           String value = l.pop().toString();
           String item  = l.pop().toString();
                  
           int rv;
           
           if (n.equals("&lindexOf"))
           {
              int start = BridgeUtilities.normalize(BridgeUtilities.getInt(l, value.length()), value.length());
              rv = value.lastIndexOf(item, start);
           }
           else
           {
              int start = BridgeUtilities.normalize(BridgeUtilities.getInt(l, 0), value.length());
              rv = value.indexOf(item, start);
           }

           if (rv == -1) { return SleepUtils.getEmptyScalar(); }
           return SleepUtils.getScalar(rv);
        }
    }

    private class func_charAt : Function
    {
        public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
        {
           String value = l.pop().toString();
           int    start = BridgeUtilities.getInt(l);
          
           if (n.equals("&charAt"))
           {
              return SleepUtils.getScalar(charAt(value, start) + "");
           }
           else
           {
              return SleepUtils.getScalar((int)charAt(value, start));
           }
        }
    }

    private class func_sort : Function
    {
        public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
        {
           if (l.size() != 2)
           {
              throw new java.lang.IllegalArgumentException("&sort requires a function to specify how to sort the data");
           }

           Function     my_func   = BridgeUtilities.getFunction(l, i);
           ScalarArray  array     = BridgeUtilities.getWorkableArray(l);

           if (my_func == null)
           {
              return SleepUtils.getArrayScalar();
           }

           array.sort(new CompareFunction(my_func, i));
           return SleepUtils.getArrayScalar(array);
        }
    }

    private class func_sorters : Function
    {
        public Scalar evaluate(String n, ScriptInstance i, java.util.Stack<Object> l)
        {
           ScalarArray  array     = BridgeUtilities.getWorkableArray(l);

           if (n.equals("&sorta"))
           {
              array.sort(new CompareStrings());
           }
           else if (n.equals("&sortn"))
           {
              array.sort(new CompareNumbers());
           }
           else if (n.equals("&sortd"))
           {
              array.sort(new CompareDoubles());
           }

           return SleepUtils.getArrayScalar(array);
        }
    }

    private class CompareFunction : java.util.Comparator<Object>
    {
        protected SleepClosure   func;
        protected ScriptInstance script;
        protected java.util.Stack<Object>          locals;

        public CompareFunction(Function _func, ScriptInstance _script)
        {
           func     = (SleepClosure)_func;
           script   = _script;
           locals   = new java.util.Stack<Object>();
        }

        public int compare(Object a, Object b)
        {
           locals.push(b);
           locals.push(a);

           Scalar temp = func.callClosure("&sort", script, locals);
           return temp.intValue();
        }

        /// FIXME: remove with VampireApi 0.11
        public bool equals (Object o) { 
           return this.Equals(o);
        }

    }

    private class CompareNumbers : java.util.Comparator<Object>
    {
        public int compare(Object a, Object b)
        {
           long aa = ((Scalar)a).longValue();
           long bb = ((Scalar)b).longValue();

           return (int)(aa - bb);
        }

        /// FIXME: remove with VampireApi 0.11
        public bool equals (Object o) { 
           return this.Equals(o);
        }

    }

    private class CompareDoubles : java.util.Comparator<Object>
    {
        public int compare(Object a, Object b)
        {
           double aa = ((Scalar)a).doubleValue();
           double bb = ((Scalar)b).doubleValue();

           if (aa == bb)
              return 0;

           if (aa < bb)
              return -1;

           return 1;
        }

        /// FIXME: remove with VampireApi 0.11
        public bool equals (Object o) { 
           return this.Equals(o);
        }
    }

    private class CompareStrings : java.util.Comparator<Object>
    {
        public int compare(Object a, Object b)
        {
           return a.toString().compareTo(b.toString());
        }

        /// FIXME: remove with VampireApi 0.11
        public bool equals (Object o) { 
           return this.Equals(o);
        }

    }

    private class oper_concat : Operator
    {
        public Scalar operate(String o, ScriptInstance i, java.util.Stack<Object> l)
        {
           Scalar left = (Scalar)(l.pop());
           Scalar right = (Scalar)(l.pop());

           if (o.equals("."))
           {
              return SleepUtils.getScalar(left.toString() + right.toString());
           }
 
           return null;
        }
    }

    private class oper_multiply : Operator
    {
        public Scalar operate(String o, ScriptInstance i, java.util.Stack<Object> l)
        {
           Scalar left = (Scalar)(l.pop());
           Scalar right = (Scalar)(l.pop());

           String str = left.toString();
           int    num = right.intValue();

           java.lang.StringBuffer value = new java.lang.StringBuffer();
         
           for (int x = 0; x < num; x++)
           {
              value.append(str);
           }

           return SleepUtils.getScalar(value);
        }
    }

    private class oper_compare : Operator
    {
        public Scalar operate(String o, ScriptInstance i, java.util.Stack<Object> l)
        {
           Scalar left = (Scalar)(l.pop());
           Scalar right = (Scalar)(l.pop());

           return SleepUtils.getScalar(left.toString().compareTo(right.toString()));
        }
    }

    private class oper_spaceship : Operator
    {
        public Scalar operate(String o, ScriptInstance i, java.util.Stack<Object> l)
        {
           ScalarType left  = BridgeUtilities.getScalar(l).getActualValue();
           ScalarType right = BridgeUtilities.getScalar(l).getActualValue();

           if (left.getType() == typeof(sleep.engine.types.DoubleValue) || right.getType() == typeof(sleep.engine.types.DoubleValue))
           {        
              if (left.doubleValue() > right.doubleValue())
              {
                 return SleepUtils.getScalar(1);
              }
              else if (left.doubleValue() < right.doubleValue())
              {
                 return SleepUtils.getScalar(-1);
              }
           }
           else
           {
              if (left.longValue() > right.longValue())
              {
                 return SleepUtils.getScalar(1);
              }
              else if (left.longValue() < right.longValue())
              {
                 return SleepUtils.getScalar(-1);
              }
           }
           return SleepUtils.getScalar(0);
        }
    }

   /** Normalizes the start/end parameters based on the length of the string and returns a substring.  Strings normalized
       in this way will be able to accept negative indices for their parameters. */
   private static String substring(String func, String str, int _start, int _end)
   {
      int length = str.length();
      int start, end;

      start = BridgeUtilities.normalize(_start, length);
      end   = (_end < 0 ? _end + length : _end);
      end   = end <= length ? end : length;

      if (start == end)
      {
         return "";
      }
      else if (start > end)
      {
         throw new java.lang.IllegalArgumentException(func + ": illegal substring('" + str + "', " + _start + " -> " + start + ", " + _end + " -> " + end + ") indices");
      }

      return str.substring(start, end);
   }

   /** Normalizes the start parameter based on the length of the string and returns a character.  Functions with
       parameters normalized in this way will be able to accept nagative indices for their parameters */
   private static char charAt(String str, int start)
   {
      return str.charAt(BridgeUtilities.normalize(start, str.length()));
   }
}
}