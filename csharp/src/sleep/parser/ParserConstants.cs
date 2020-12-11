/*
 * Copyright 2002-2020 Raphael Mudge
 * Copyright 202 Sebastian Ritter
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
namespace sleep.parser {

public interface ParserConstants
{
   public const int EXPR_BLOCK           = 150;       
   public const int EXPR_WHILE           = 100;
   public const int EXPR_WHILE_SPECIAL   = 101;       
   public const int EXPR_ASSIGNMENT      = 200;
   public const int EXPR_ASSIGNMENT_T    = 202;
   public const int EXPR_ASSIGNMENT_OP   = 203;
   public const int EXPR_ASSIGNMENT_T_OP = 204;
   public const int EXPR_IF              = 300;
   public const int EXPR_IF_ELSE         = 301;
   public const int EXPR_FOREACH         = 400;
   public const int EXPR_FOR             = 401;
   public const int EXPR_FOREACH_SPECIAL = 402;
   public const int EXPR_TRYCATCH	= 403;
   public const int EXPR_RETURN          = 500;
   public const int EXPR_BREAK           = 501;
   public const int EXPR_BIND            = 502;
   public const int EXPR_ESCAPE          = 503;
   public const int EXPR_BIND_PRED       = 504;
   public const int EXPR_BIND_FILTER     = 505;
   public const int EXPR_EVAL_STRING     = 506; // used for `backtick` strings that do something cool :)

   public const int EXPR_ASSERT          = 507; // oooh ass hurt... eer assert.
 
   public const int IDEA_EXPR            = 601;
   public const int IDEA_OPER            = 603;
   public const int IDEA_FUNC       = 604;
   public const int IDEA_STRING     = 605;
   public const int IDEA_LITERAL    = 606;
   public const int IDEA_NUMBER     = 607;
   public const int IDEA_DOUBLE     = 608;
   public const int IDEA_BOOLEAN    = 609;
   public const int IDEA_PROPERTY   = 610;
   public const int IDEA_EXPR_I     = 611;
   public const int IDEA_HASH_PAIR  = 612;
   public const int IDEA_BLOCK      = 613;
   public const int IDEA_CLASS      = 614;
  
   public const int OBJECT_NEW      = 441;
   public const int OBJECT_ACCESS   = 442;
   public const int OBJECT_ACCESS_S = 443;
   public const int OBJECT_using    = 444;
   public const int OBJECT_CL_CALL  = 446; // a object closure call [$closure:parm1, parm2, parm3] or [$closure] 

   public const int VALUE_SCALAR           = 701;
   public const int VALUE_SCALAR_REFERENCE = 705;
   public const int VALUE_INDEXED          = 710;


   public const int PRED_BI         = 801;
   public const int PRED_UNI        = 802;
   public const int PRED_OR         = 803;
   public const int PRED_AND        = 804;
   public const int PRED_EXPR       = 805;
   public const int PRED_IDEA       = 806; // we're testing a pred for a zero or non-zero value

   public const int HACK_INC        = 901;
   public const int HACK_DEC        = 902;
}
}