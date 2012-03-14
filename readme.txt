 ------- -------------------- ---------   ---------------  ------- ---------
Sleep 2.1 - README   
-  ------------- -------- -   ---------------- --       -    -     -     - - -

"You got the language, all you need now is the O'Reilly book".  That is
what my friend Luke said to me upon closing out a weekend of possessed
coding.

A weekend of possessed coding that yielded a scripting language.  Sleep is a 
Java-based scripting language heavily inspired by Perl. Sleep provides advanced 
programming features including:

   - continuations
   - first-class functions
   - a built-in debugger 
   - taint mode security
   - access to the Java class library
   - ... and cryptic Perl syntax.

The core of sleep was produced in one weekend in early April of 2002.  I just 
wanted something I could integrate into an application I was writing.  
Sometime in 2004 (or 2005?) I was possessed to expand the language to include 
closures and access to the Java class library. 

After Sleep 2.0 I let the project sit and collect bug reports for about a 
year.  At some point I added coroutines into Sleep 2.0 and thus Sleep 2.1 
was born.  This release is the result of a 2 year polishing effort on the Sleep 
2.0 codebase. 

Sleep is compatible with Java 1.4.2+

Sleep Project Homepage: http://sleep.dashnine.org/

 ------- -------------------- ---------   ---------------  ------- ---------
Documentation
-  ------------- -------- -   ---------------- --       -    -     -     - - -

An open source project just wouldn't be a good open source project without
a documentation deficiency.  Sleep is no different. 

Contained in the docs/ directory:

sleepmanual.url
   The Sleep 2.1 Manual includes a tutorial on Sleep, a chapter on how to
   embed and extend Sleep from Java, and a reference of all built-in 
   functionality.  

   The manual is available both online and in print under a creative 
   commons license.

   Find the manual online at: http://sleep.dashnine.org/manual/

   Optionally you can purchase the Manual at:
   http://www.amazon.com/dp/143822723X/

common.htm
   Common embedding techniques cheat sheet.  A very short example oriented 
   document on common techniques for embedding sleep in your application.
   Includes information such as how to catch and process a syntax error,
   load a script, call a function etc.  To truly get in depth though you
   need to read sleepguide.pdf.  

console.txt
   A quick reference on the sleep console.  The sleep console is a simple
   console for interacting with the sleep library.

parser.htm
   A little overview on how the sleep parser works for the curious.

You also have the option of generating the JavaDoc API's for the sleep 
language.  I recommend either generating these or downloading them from 
the sleep website.  Javadoc is your friend when working with this project.
     
 ------- -------------------- ---------   ---------------  ------- ---------
Build Instructions
-  ------------- -------- -   ---------------- --       -    -     -     - - -

You will need Apache Ant to compile this source code. I use version 1.7.0. 
Ant is easy to install and is available at http://ant.apache.org 

To compile sleep:

[raffi@beardsley ~/sleep]$ ant clean
[raffi@beardsley ~/sleep]$ ant all

If you made any changes or just want to make sure nothing is broken you can
run a series of regression tests on sleep.

[raffi@beardsley ~/sleep]$ java -jar sleep.jar runtests.sl

If the runtests.so script won't run then you're really in trouble.

To Build JavaDoc for Sleep (dumped to the docs/api/ directory):

[raffi@beardsley ~/sleep]$ ant docs

To build full JavaDoc for Sleep (all classes):

[raffi@beardsley ~/sleep]$ ant docs-full

To launch the sleep console (see docs/console.txt for more information):

[raffi@beardsley ~/sleep/bin]$ java -jar sleep.jar

For Sleep interpreter usage information:

[raffi@beardsley ~/sleep/bin]$ java -jar sleep.jar --help

To launch a sleep script from the command line:

[raffi@beardsley ~/sleep/bin]$ java -jar sleep.jar filename.sl

When sleep scripts are run directly on the command line, arguments are
placed into the @ARGV variable.  Also the executed script name is
stored in the $__SCRIPT__ variable.

To launch a sleep script from the command line (without -jar):

[raffi@beardsley ~/sleep/bin]$ java -classpath sleep.jar sleep.console.TextConsole filename.sl

 ------- -------------------- ---------   ---------------  ------- ---------
Feedback
-  ------------- -------- -   ---------------- --       -    -     -     - - -

Feedback is always welcome.  Suggestions/comments/questions can directed to
me via email: rsmudge@gmail.com

I do respond to most feedback.  For example, this message posted to a public
forum:

   Subject: re: better name would be STOP

   Dear User,
   Thank you for your suggestion on a new name for sleep.  I feel many 
   users recognize the name sleep and changing names at this point might
   do more harm than good.  Thank you for your suggestion and glad to hear
   that you are enjoying the language.

                    -- Raffi

   Subject: better name would be STOP
   From:    NothingPersonal (62.132.1.121) 
   Date:    September 27, 2004 at 10:08:34

   Please. Just STOP. stop stop stop. don't inflict yet another half-baked, 
   il-concieved abortion of a scripting language onto unsuspecting 
   developers. Yes, you had fun writing it, but the only niche it fills is 
   the one in your head that renders you incapable of mastering any of the 
   other numerous scripting options already available to you.

   I know I'm supposed to be nice to you because hey who are you really 
   bothering, and selection of the fittest will surely see sleep sleep it's 
   way to a quiet and peaceful death. But while on it's way to it's 
   inevitable demise, sleep is bound to take with it some hapless developers, 
   who will in turn inflict it on numerous doomed projects, and all that 
   spells misery for all concerned.

   While I'm at it, I also have to point out that the very last thing I want 
   to read when browsing a language reference is pathetic, self-important 
   humour.

   I'm urging you to do the honourable thing. stop sleeping, and wake the 
   f*** up. Take down your cargo-cult website (it even has a wiki! it's 
   _bound_ to be a success!) and spend (alot) more time researching your 
   foundations before embarking on such follies again.


 ------- -------------------- ---------   ---------------  ------- ---------
Legal Garbage
-  ------------- -------- -   ---------------- --       -    -     -     - - -

Sleep is (c) 2002, 2003, 2004, 2005, 2006, 2007, 2008, and 2009 (wow, been at 
this awhile) Raphael Mudge (rsmudge@gmail.com).  All of the source is released 
under the GNU Lesser Public License.

The scripts and library files supplied as input to or produced as output 
from the Sleep library do not automatically fall under the copyright of the
Sleep project, but belong to whomever generated them, and may be sold 
commercially, and may be aggregated with this library.

Java or sleep subroutines supplied by you and linked into this library 
shall not be considered part of this library.

See license.txt for more information.
