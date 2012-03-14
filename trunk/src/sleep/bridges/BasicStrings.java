/*
   SLEEP - Simple Language for Environment Extension Purposes
 .----------------------------.
 | sleep.bridges.BasicStrings |_______________________________________________
 |                                                                            |
   Author: Raphael Mudge (raffi@hick.org)
           http://www.hick.org/~raffi/

   Description:
      provides some of the basic parsing facilities.

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

import sleep.parser.ParserConfig;

/** provides basic string parsing facilities */
public class BasicStrings implements Loadable, Predicate
{
    static 
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
        Hashtable temp = aScript.getScriptEnvironment().getEnvironment();

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

    public boolean decide(String n, ScriptInstance i, Stack l)
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
                 if (! Character.isLetter(a.charAt(x)) )
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
                 if (! Character.isDigit(a.charAt(x)) && (a.charAt(x) != '.' || (x+1) >= a.length()) )
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

    private static class pred_iswm implements Predicate
    {
        public boolean decide(String name, ScriptInstance script, Stack locals)
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
                 boolean greedy = ((aptr + 1) < a.length() && a.charAt(aptr + 1) == '*');

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
           catch (Exception ex) { ex.printStackTrace(); }
     

           return false;
        }
     
    }

    private static class func_left implements Function
    {
        public Scalar evaluate(String n, ScriptInstance i, Stack l)
        {
           String temp  = l.pop().toString();
           int    value = ((Scalar)l.pop()).intValue();

           return SleepUtils.getScalar(substring(n, temp, 0, value));
        }
    }

    private static class func_tr implements Function
    {
        public Scalar evaluate(String n, ScriptInstance i, Stack l)
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

    private static class func_right implements Function
    {
        public Scalar evaluate(String n, ScriptInstance i, Stack l)
        {
           String temp  = l.pop().toString();
           int    value = ((Scalar)l.pop()).intValue();

           return SleepUtils.getScalar(substring(n, temp, 0 - value, temp.length()));
        }
    }

    private static class func_asc implements Function
    {
        public Scalar evaluate(String n, ScriptInstance i, Stack l)
        {
           return SleepUtils.getScalar((int)(BridgeUtilities.getString(l, "\u0000").charAt(0)));
        }
    }

    private static class func_chr implements Function
    {
        public Scalar evaluate(String n, ScriptInstance i, Stack l)
        {
           return SleepUtils.getScalar(((char)BridgeUtilities.getInt(l))+"");
        }
    }


    private static class func_uc implements Function
    {
        public Scalar evaluate(String n, ScriptInstance i, Stack l)
        {
           return SleepUtils.getScalar(l.pop().toString().toUpperCase());
        }
    }

    private static class func_lc implements Function
    {
        public Scalar evaluate(String n, ScriptInstance i, Stack l)
        {
           return SleepUtils.getScalar(l.pop().toString().toLowerCase());
        }
    }

    private static class func_strlen implements Function
    {
        public Scalar evaluate(String n, ScriptInstance i, Stack l)
        {
           return SleepUtils.getScalar(l.pop().toString().length());
        }
    }

    private static class func_strrep implements Function
    {
        public Scalar evaluate(String n, ScriptInstance i, Stack l)
        {
           StringBuffer work    = new StringBuffer(BridgeUtilities.getString(l, ""));

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

    private static class func_replaceAt implements Function
    {
        public Scalar evaluate(String n, ScriptInstance i, Stack l)
        {
           StringBuffer work    = new StringBuffer(BridgeUtilities.getString(l, ""));
           String       nstr    = BridgeUtilities.getString(l, "");
           int          index   = BridgeUtilities.normalize(BridgeUtilities.getInt(l, 0), work.length());
           int          nchar   = BridgeUtilities.getInt(l, nstr.length());

           work.delete(index, index + nchar);
           work.insert(index, nstr);

           return SleepUtils.getScalar(work.toString());
        }
    }

    private static class func_substr implements Function
    {
        public Scalar evaluate(String n, ScriptInstance i, Stack l)
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

    private static class func_indexOf implements Function
    {
        public Scalar evaluate(String n, ScriptInstance i, Stack l)
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

    private static class func_charAt implements Function
    {
        public Scalar evaluate(String n, ScriptInstance i, Stack l)
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

    private static class func_sort implements Function
    {
        public Scalar evaluate(String n, ScriptInstance i, Stack l)
        {
           if (l.size() != 2)
           {
              throw new IllegalArgumentException("&sort requires a function to specify how to sort the data");
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

    private static class func_sorters implements Function
    {
        public Scalar evaluate(String n, ScriptInstance i, Stack l)
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

    private static class CompareFunction implements Comparator
    {
        protected SleepClosure   func;
        protected ScriptInstance script;
        protected Stack          locals;

        public CompareFunction(Function _func, ScriptInstance _script)
        {
           func     = (SleepClosure)_func;
           script   = _script;
           locals   = new Stack();
        }

        public int compare(Object a, Object b)
        {
           locals.push(b);
           locals.push(a);

           Scalar temp = func.callClosure("&sort", script, locals);
           return temp.intValue();
        }
    }

    private static class CompareNumbers implements Comparator
    {
        public int compare(Object a, Object b)
        {
           long aa = ((Scalar)a).longValue();
           long bb = ((Scalar)b).longValue();

           return (int)(aa - bb);
        }
    }

    private static class CompareDoubles implements Comparator
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
    }

    private static class CompareStrings implements Comparator
    {
        public int compare(Object a, Object b)
        {
           return a.toString().compareTo(b.toString());
        }
    }

    private static class oper_concat implements Operator
    {
        public Scalar operate(String o, ScriptInstance i, Stack l)
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

    private static class oper_multiply implements Operator
    {
        public Scalar operate(String o, ScriptInstance i, Stack l)
        {
           Scalar left = (Scalar)(l.pop());
           Scalar right = (Scalar)(l.pop());

           String str = left.toString();
           int    num = right.intValue();

           StringBuffer value = new StringBuffer();
         
           for (int x = 0; x < num; x++)
           {
              value.append(str);
           }

           return SleepUtils.getScalar(value);
        }
    }

    private static class oper_compare implements Operator
    {
        public Scalar operate(String o, ScriptInstance i, Stack l)
        {
           Scalar left = (Scalar)(l.pop());
           Scalar right = (Scalar)(l.pop());

           return SleepUtils.getScalar(left.toString().compareTo(right.toString()));
        }
    }

    private static class oper_spaceship implements Operator
    {
        public Scalar operate(String o, ScriptInstance i, Stack l)
        {
           ScalarType left  = BridgeUtilities.getScalar(l).getActualValue();
           ScalarType right = BridgeUtilities.getScalar(l).getActualValue();

           if (left.getType() == sleep.engine.types.DoubleValue.class || right.getType() == sleep.engine.types.DoubleValue.class)
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
   private static final String substring(String func, String str, int _start, int _end)
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
         throw new IllegalArgumentException(func + ": illegal substring('" + str + "', " + _start + " -> " + start + ", " + _end + " -> " + end + ") indices");
      }

      return str.substring(start, end);
   }

   /** Normalizes the start parameter based on the length of the string and returns a character.  Functions with
       parameters normalized in this way will be able to accept nagative indices for their parameters */
   private static final char charAt(String str, int start)
   {
      return str.charAt(BridgeUtilities.normalize(start, str.length()));
   }
}
