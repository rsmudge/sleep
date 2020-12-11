/*
 * Copyright 2002-2020 Raphael Mudge
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
package sleep.bridges;

import java.util.*;
import java.text.*;

import java.util.regex.PatternSyntaxException;

/**
 * <p>This class provides a character translation utility similar to the UNIX tr command.  Essentially
 * a pattern is compiled defining characters and their appropriate substitutions.  Once compiled a 
 * Transliteration pattern can be compared against a string.  Each character in the string is compared
 * to each character in the pattern.  If a match is found the character is either replaced or deleted
 * as specified in the patterns replacement.</p>
 *
 * <p>Transliteration is not the same as regular expressions.  Transliteration has a single character
 * scope.</p>
 *
 * <b>Example Usage:</b>
 *
 * <pre> // A simple ROT13 Translator (for extra security run it twice...)
 * Transliteration rot13 = Transliteration.compile("a-z", "n-za-m");
 * String ciphertext = rot13.translate("this is a mad cool test");
 *
 * System.out.println("Cipher text: " + ciphertext);
 *
 * Sring plaintext = rot13.translate(ciphertext);
 * System.out.println("Plain text: " + plaintext);</pre>
 *
 * <p>Replacement patterns and Matcher patterns may both contain ranges.  Any range specified in either of these places will be
 * expanded to all of the characters.  A range is specified as <code><i>n</i>-</i>m</i></code> where <i>n</i> is the
 * starting character (A-Z, a-z, 0-9) and <i>m</i> is the ending character.  Backwards ranges are allowed as well.</p>
 *
 * <p>If an expanded replacement pattern is shorter than the matcher pattern, the last character of the replacement pattern
 * will be used to map to all remaining characters in the matcher pattern.  The <code>OPTION_DELETE</code> option changes this
 * behavior to delete those matches that don't have a replacement pattern character explicitly mapped to them.</p>
 *
 * <p>Matcher patterns may contain the following character classes:</p>
 *
 * <table width="80%"> 
 *  <tr><th width="100" align="left">Sequence</th><th align="left">Meaning</th></tr>
 *  <tr><td>.</td><td>Matches any character</td></tr>
 *  <tr><td>\d</td><td>Matches any digit 0-9</td></tr>
 *  <tr><td>\D</td><td>Matches any non-digit</td></tr>
 *  <tr><td>\s</td><td>Matches any whitespace character</td></tr>
 *  <tr><td>\S</td><td>Matches any non-whitespace character</td></tr>
 *  <tr><td>\w</td><td>Matches any letter</td></tr>
 *  <tr><td>\W</td><td>Matches any non-letter</td></tr>
 *  <tr><td>\\</td><td>Matches a literal backslash</td></tr>
 *  <tr><td>\.</td><td>Matches a literal period</td></tr>
 *  <tr><td>\-</td><td>Matches a literal dash</td></tr>
 * </table>
 *
 * <p>Any other escape sequence is considered an error and an exception will be thrown.</p>
 *
 * <p>Transliteration patterns have several options that can change the behavior of the matcher/translator.</p>
 *
 * <p><code>OPTION_DELETE</code> tells the translator to delete matching characters if there is no mapped character specified
 * in the replacement pattern.</p>
 *
 * <p><code>OPTION_COMPLEMENT</code> negates the compiled pattern.  When this flag is set all characters and meta-characters will
 * match their compliments.</p>
 *
 * <p><code>OPTION_SQUEEZE</code> is used to force the translator to squeeze together matches right next to eachother.  Essentially
 * this option will delete repeated characters that match a pattern character.</p>
 *
 * <p>This class is released into the public domain.  Do with it as you wish (but please give credit where credit is due). 
 * Created by <a href="mailto:raffi@hick.org">Raphael Mudge</a> for the <a href="http://sleep.hick.org/">Sleep scripting language</a>.</p>
 **/
public class Transliteration
{
    /** Forces any matches of non-mapped pattern characters to be deleted */
    public static final int OPTION_DELETE     = 1;

    /** Negates the pattern */
    public static final int OPTION_COMPLEMENT = 2; 

    /** Deletes duplicates of all matched characters */
    public static final int OPTION_SQUEEZE    = 4;

    private static String AvailableOptions = "dDsSwW.\\-"; 

    private int options = 0;

    /** the head of a linked list of pattern elements */
    private Element pattern = null;

    private static class Element 
    {
        public char     item = 'x';
        public char     replacement = 'x';
        public boolean  isSpecial   = false; 
        public boolean  isWildcard  = false;

        public Element next = null;
    }

    /** Returns a string representation of this transliteration pattern... */
    public String toString()
    {
        StringBuffer a = new StringBuffer();
        StringBuffer b = new StringBuffer();
        Element temp = pattern;
        while (temp != null)
        {
           if (temp.isSpecial)
           {
              switch (temp.item)
              {
                 case '.':
                   a.append("[:ANY:]");
                   break;
                 case 'd':
                   a.append("[:digit:]");
                   break;
                 case 'D':
                   a.append("[:non-digit:]");
                   break;
                 case 's':
                   a.append("[:whitespace:]");
                   break;
                 case 'S':
                   a.append("[:non-whitespace:]");
                   break;
                 case 'w':
                   a.append("[:word character:]");
                   break;
                 case 'W':
                   a.append("[:non-word character:]");
                   break;
                 default:
                   a.append(temp.item);
              }
           }
           else
           {
              a.append(temp.item);
           }
           b.append(temp.replacement);
           temp = temp.next;
        }

        return "tr/" + a + "/" + b + "/";
    }
 
    private static String getRange(char a, char b)
    {
        StringBuffer temp = new StringBuffer();

        if (a < b)
        {
           char c = a;
           while (c < b)
           {
              temp.append(c);
              c = (char)(c + 1);
           }
        }
        else if (a > b)
        {
           char c = a;
           while (c > b)
           {
              temp.append(c);
              c = (char)(c - 1);
           }
        }

        return temp.toString();
    }

    private static String expandRanges(String text) throws PatternSyntaxException
    {
        StringBuffer temp = new StringBuffer(text);

        for (int x = 0; x < temp.length(); x++)
        {
            if (temp.charAt(x) == '\\')
            {
               x++;
            }
            else if (temp.charAt(x) == '-')
            {
               if (x <= 0 || x >= (temp.length() - 1))
                    throw new PatternSyntaxException("Dangling range operator '-'", text, text.length() - 1);

               String range = getRange(temp.charAt(x-1), temp.charAt(x+1));
               temp.replace(x - 1, x + 1, range);

               x += range.length() - 2;
            }
        }

        return temp.toString();        
    }

    private Element buildPattern(String pattern, String changes)
    {
         Element head = null;
         Element temp = null;

         pattern = expandRanges(pattern);
         changes = expandRanges(changes);

         StringCharacterIterator a = new StringCharacterIterator(pattern);
         StringCharacterIterator b = new StringCharacterIterator(changes);

         while (a.current() != StringCharacterIterator.DONE)  // StringCharacterIterator.DONE?!? What kind of high school intern 
         {                                                    // wrote this class...  hello... hasNext() !
             if (temp == null)
             {
                 head = new Element();
                 temp = head;
             }
             else
             {
                 temp.next = new Element();
                 temp      = temp.next;
             }

             if (a.current() == '\\')
             {
                 temp.item        = a.next();
                 temp.replacement = b.current();

                 if (a.current() == StringCharacterIterator.DONE)
                 {
                    throw new PatternSyntaxException("attempting to escape end of pattern string", pattern, a.getEndIndex() - 1);
                 }
                 else if (AvailableOptions.indexOf(temp.item) == -1)
                 {
                    throw new PatternSyntaxException("unrecognized escaped meta-character '" + temp.item + "'", pattern, a.getIndex());
                 }
                 else
                 {
                    // anything escaped is considered special except for a \, a ., and a -
                    temp.isSpecial = (a.current() != '\\' && a.current() != '.' && a.current() != '-');
                 }
             } 
             else
             {
                 temp.item        = a.current();
                 temp.replacement = b.current();

                 temp.isSpecial   = a.current() == '.'; // a . is always considered special unless escaped...
             }
 
             a.next();
             b.next();

             if (b.current() == StringCharacterIterator.DONE && ((options & OPTION_DELETE) != OPTION_DELETE))
             {
                b.last();
             }
         }
            
         return head; 
     }

     /** Compiles the translation pattern.  The matches pattern is what the translator engine looks for.  Each character in the
         expanded matches pattern is mapped to the corresponding character in the expanded replacements pattern.  In theory
         when a string is applied to this Transliteration all characters that match something in the matches pattern will be
         replaced with the corresponding character from the replacements pattern.  

         @throws PatternSyntaxException caused by a bad pattern. */
     public static Transliteration compile(String matches, String replacements) throws PatternSyntaxException
     {
         return compile(matches, replacements, 0);
     }

     /** Compiles the translation pattern.  The matches pattern is what the translator engine looks for.  Each character in the
         expanded matches pattern is mapped to the corresponding character in the expanded replacements pattern.  In theory
         when a string is applied to this Transliteration all characters that match something in the matches pattern will be
         replaced with the corresponding character from the replacements pattern.  

         @throws PatternSyntaxException caused by a bad pattern.*/
     public static Transliteration compile(String matches, String replacements, int options) throws PatternSyntaxException
     {
         Transliteration value = new Transliteration();
         value.options = options;
         value.pattern = value.buildPattern(matches, replacements);
         return value;
    }

    private boolean isMatch(char current, Element element)
    {
        boolean rv = false;

        if (element.isSpecial)
        {
            switch (element.item)
            {
                 case '.':
                   rv = true;
                   break;
                 case 'd':
                   rv = Character.isDigit(current);
                   break;
                 case 'D':
                   rv = !Character.isDigit(current);
                   break;
                 case 's':
                   rv = Character.isWhitespace(current);
                   break;
                 case 'S':
                   rv = !Character.isWhitespace(current);
                   break;
                 case 'w':
                   rv = Character.isLetter(current);
                   break;
                 case 'W':
                   rv = !Character.isLetter(current);
                   break;
            }
        }
        else
        {
            rv = element.item == current;
        }

        if ((options & OPTION_COMPLEMENT) == OPTION_COMPLEMENT)
        {
           rv = !rv;
        }

        return rv;
    }

    /** Applies this Transliteration to the specified text. */
    public String translate(String text)
    {
        StringBuffer rv = new StringBuffer();

        Element temp = null;
        char         current;
        boolean match = false;

        for (int x = 0; x < text.length(); x++)
        {
            current = text.charAt(x);             
            temp = pattern;
            match = false;
            while (temp != null)
            {
                if (isMatch(current, temp))
                {
                    if (temp.replacement != StringCharacterIterator.DONE)
                    {
                       rv.append(temp.replacement);
                    }

                    // perform our squeeze :)
                    while ((options & OPTION_SQUEEZE) == OPTION_SQUEEZE && (x + 1) < text.length() && text.charAt(x + 1) == current)
                    {
                        x++;
                    }
                    match = true;

                    break;
                }
                temp = temp.next;
            }

            if (!match)
                rv.append(current);
        }

        return rv.toString();
    }
}
