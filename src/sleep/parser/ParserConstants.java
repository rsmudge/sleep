/* 
 * Copyright (C) 2002-2012 Raphael Mudge (rsmudge@gmail.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package sleep.parser;

public interface ParserConstants
{
   public static final int EXPR_BLOCK           = 150;       
   public static final int EXPR_WHILE           = 100;
   public static final int EXPR_WHILE_SPECIAL   = 101;       
   public static final int EXPR_ASSIGNMENT      = 200;
   public static final int EXPR_ASSIGNMENT_T    = 202;
   public static final int EXPR_ASSIGNMENT_OP   = 203;
   public static final int EXPR_ASSIGNMENT_T_OP = 204;
   public static final int EXPR_IF              = 300;
   public static final int EXPR_IF_ELSE         = 301;
   public static final int EXPR_FOREACH         = 400;
   public static final int EXPR_FOR             = 401;
   public static final int EXPR_FOREACH_SPECIAL = 402;
   public static final int EXPR_TRYCATCH	= 403;
   public static final int EXPR_RETURN          = 500;
   public static final int EXPR_BREAK           = 501;
   public static final int EXPR_BIND            = 502;
   public static final int EXPR_ESCAPE          = 503;
   public static final int EXPR_BIND_PRED       = 504;
   public static final int EXPR_BIND_FILTER     = 505;
   public static final int EXPR_EVAL_STRING     = 506; // used for `backtick` strings that do something cool :)

   public static final int EXPR_ASSERT          = 507; // oooh ass hurt... eer assert.
 
   public static final int IDEA_EXPR            = 601;
   public static final int IDEA_OPER            = 603;
   public static final int IDEA_FUNC       = 604;
   public static final int IDEA_STRING     = 605;
   public static final int IDEA_LITERAL    = 606;
   public static final int IDEA_NUMBER     = 607;
   public static final int IDEA_DOUBLE     = 608;
   public static final int IDEA_BOOLEAN    = 609;
   public static final int IDEA_PROPERTY   = 610;
   public static final int IDEA_EXPR_I     = 611;
   public static final int IDEA_HASH_PAIR  = 612;
   public static final int IDEA_BLOCK      = 613;
   public static final int IDEA_CLASS      = 614;
  
   public static final int OBJECT_NEW      = 441;
   public static final int OBJECT_ACCESS   = 442;
   public static final int OBJECT_ACCESS_S = 443;
   public static final int OBJECT_IMPORT   = 444;
   public static final int OBJECT_CL_CALL  = 446; // a object closure call [$closure:parm1, parm2, parm3] or [$closure] 

   public static final int VALUE_SCALAR           = 701;
   public static final int VALUE_SCALAR_REFERENCE = 705;
   public static final int VALUE_INDEXED          = 710;


   public static final int PRED_BI         = 801;
   public static final int PRED_UNI        = 802;
   public static final int PRED_OR         = 803;
   public static final int PRED_AND        = 804;
   public static final int PRED_EXPR       = 805;
   public static final int PRED_IDEA       = 806; // we're testing a pred for a zero or non-zero value

   public static final int HACK_INC        = 901;
   public static final int HACK_DEC        = 902;
}
