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

using  java.util;
using  sleep.error;
using  sleep.engine.Block;

using  sleep.engine.GeneratedSteps;

using  java.io;
using  java.net;

namespace sleep.parser{

public class Parser
{
   private static sealed bool DEBUG_ITER     = false;
   private static sealed bool DEBUG_LEX      = false;
   private static sealed bool DEBUG_COMMENTS = false;
   private static sealed bool DEBUG_TPARSER  = false;

   protected String     code; /** the actual "code" for the script file. */
   protected String     name; /** an identifier for the script file. */

   protected LinkedList comments   = new LinkedList(); /** a list of all of the comments from the script file */
   protected LinkedList errors     = new LinkedList(); /** a list of all of the parser errors */
   protected LinkedList warnings   = new LinkedList(); /** a list of all of the parser warnings */

   protected TokenList  tokens     = new TokenList();
   protected LinkedList statements = new LinkedList(); /** a list of all of the statements */

   protected Block      executeMe;  // runnable block   
 
   public    char       EndOfTerm  = ';';

   protected ImportManager imports;

   /** obtain the using  manager, used for managing imported packages. */
   public ImportManager getImportManager()
   {
      return imports;
   }

   /** the factory to use when generating Sleep code */
   protected GeneratedSteps factory = null;

   /** set the code factory to be used to generated Sleep code */
   public void setCodeFactory(GeneratedSteps s)
   {
      factory = s;
   }

   /** Used by Sleep to using  statement to save an imported package name. */
   public void importPackage(String packagez, String from)
   {
      imports.importPackage(packagez, from);
   }   

   /** Attempts to find a class, starts out with the passed in string itself, if that doesn't resolve then the string is
       appended to each imported package to see where the class might exist */
   public Class findImportedClass(String name)
   {
      return imports.findImportedClass(name);
   }

   public void setEndOfTerm(char c)
   {
      EndOfTerm = c;
   }

   /** initialize the parser with the code you want me to work with */
   public Parser(String _code)
   {
      this("unknown", _code);
   }

   /** initialize the parser with the code you want me to work with */
   public Parser(String _name, String _code)
   {
      this(_name, _code, null);
   }

   /** initialize the parser with the code you want me to work with plus a shared using  manager */
   public Parser(String _name, String _code, ImportManager imps)
   {
      if (imps == null)
      {
         imps = new ImportManager();
      }
      imports = imps;

      importPackage("java.lang.*", null);
      importPackage("java.util.*", null);
      importPackage("sleep.runtime.*", null);

      code = _code;
      name = _name;
   }

   public void addStatement(Statement state)
   {
      statements.add(state);
   }

   public LinkedList getStatements()
   {
      return statements;
   }

   /** returns the identifier representing the source of the script we're parsing */
   public String getName()
   {
      return name;
   }

   public void parse() //throws YourCodeSucksException
   {
      parse(new StringIterator(code));
   }

   public void parse(StringIterator siter) //throws YourCodeSucksException
   {
      TokenList tokens = LexicalAnalyzer.GroupBlockTokens(this, siter);

      /** debug the tokenizer */
      if (DEBUG_LEX)
      {
         Token[] all = tokens.getTokens();
         for (int x = 0; x < all.length; x++)
         {
            SystemJ.outJ.println(x + ": " + all[x].toString() + " at " + all[x].getHint());
         }
      }

      if (hasErrors())
      {
         errors.addAll(warnings);
         throw new YourCodeSucksException(errors);      
      }

      LinkedList statements = TokenParser.ParseBlocks(this, tokens);

      if (DEBUG_TPARSER && statements != null)
      {
         Iterator i = statements.iterator();
         while (i.hasNext())
         {
            SystemJ.outJ.println("Block\n"+i.next());
         }
      }

      if (hasErrors())
      {
         errors.addAll(warnings);
         throw new YourCodeSucksException(errors);      
      }

      CodeGenerator codegen = new CodeGenerator(this, factory);
      codegen.parseBlock(statements);

      if (hasErrors())
      {
         errors.addAll(warnings);
         throw new YourCodeSucksException(errors);      
      }

      executeMe = codegen.getRunnableBlock();

      if (DEBUG_COMMENTS)
      {
         Iterator i = comments.iterator();
         while (i.hasNext()) { SystemJ.outJ.print("Comment: " + i.next()); }
      }
   }

   public void reportError(String description, Token responsible)
   {
      errors.add(new SyntaxError(description, responsible.toString(), responsible.getHint()));
   }

   public void reportErrorWithMarker(String description, Token responsible)
   {
      errors.add(new SyntaxError(description, responsible.toString(), responsible.getHint(), responsible.getMarker()));
   }

   public void reportError(SyntaxError error)
   {
      errors.add(error);
   }

   public Block getRunnableBlock()
   {
      return executeMe;
   }

   public void reportWarning(String description, Token responsible)
   {
      warnings.add(new SyntaxError(description, responsible.toString(), responsible.getHint()));
   }

   public bool hasErrors()
   {
      return errors.size() > 0;
   }

   public bool hasWarnings()
   {
      return warnings.size() > 0;
   }

   public void addComment(String text)
   {
      comments.add(text);
   }

   public static void main(String [] args)
   {
      try
      {
         File afile = new File(args[0]);
         BufferedReader temp = new BufferedReader(new InputStreamReader(new FileInputStream(afile)));

         StringBuffer data = new StringBuffer();

         String text;
         while ((text = temp.readLine()) != null)
         {
            data.append(text);
            data.append('\n');
         }

         Parser p = new Parser(data.toString());
         p.parse();
         SystemJ.outJ.println(p.getRunnableBlock());
      }
      catch (YourCodeSucksException yex)
      {
         LinkedList errors = yex.getErrors();
         Iterator i = errors.iterator();
         while (i.hasNext())
         {
            SyntaxError anError = (SyntaxError)i.next();
            SystemJ.outJ.println("Error: " + anError.getDescription() + " at line " + anError.getLineNumber());
            SystemJ.outJ.println("       " + anError.getCodeSnippet());
            if (anError.getMarker() != null)
               SystemJ.outJ.println("       " + anError.getMarker());
         }
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }
   }
}
}