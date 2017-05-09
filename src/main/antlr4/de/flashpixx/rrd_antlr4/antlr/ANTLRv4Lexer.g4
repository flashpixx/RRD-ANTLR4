/*
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the RRD-AntLR4                                                #
 * # Copyright (c) 2016-17, Philipp Kraus (philipp.kraus@flashpixx.de)                  #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU Lesser General Public License as                     #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU Lesser General Public License for more details.                                #
 * #                                                                                    #
 * # You should have received a copy of the GNU Lesser General Public License           #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

/**
 *	A grammar for ANTLR v4 implemented using v4 syntax
 *
 *	Modified 2015.06.16 gbr
 *	-- update for compatibility with Antlr v4.5
 */

lexer grammar ANTLRv4Lexer;

options {
	superClass = de.flashpixx.rrd_antlr4.antlr.IAntLRv4LexerAdaptor ;
}

import LexBasic;	// Standard set of fragments

tokens {
	TOKEN_REF,
	RULE_REF,
	LEXER_CHAR_SET
}

channels {
	OFF_CHANNEL		// non-default channel for whitespace and comments
}


// ======================================================
// Lexer specification
//

// -------------------------
// Comments

DOC_COMMENT
	:	DocComment
	;

BLOCK_COMMENT
	:	BlockComment	-> channel(OFF_CHANNEL)
	;

LINE_COMMENT
	:	LineComment		-> channel(OFF_CHANNEL)
	;


// -------------------------
// Integer
//

INT	: DecimalNumeral
	;


// -------------------------
// Literal string
//
// ANTLR makes no distinction between a single character literal and a
// multi-character string. All literals are single quote delimited and
// may contain unicode escape sequences of the form \uxxxx, where x
// is a valid hexadecimal number (per Unicode standard).

STRING_LITERAL
	: SQuoteLiteral
	;

UNTERMINATED_STRING_LITERAL
	: USQuoteLiteral
	;


// -------------------------
// Arguments
//
// Certain argument lists, such as those specifying call parameters
// to a rule invocation, or input parameters to a rule specification
// are contained within square brackets.

BEGIN_ARGUMENT
	:	LBrack		{ handleBeginArgument(); }
	;


// -------------------------
// Actions

BEGIN_ACTION
	:	LBrace		-> pushMode(Action)
	;


// -------------------------
// Keywords
//
// Keywords may not be used as labels for rules or in any other context where
// they would be ambiguous with the keyword vs some other identifier.  OPTIONS,
// TOKENS, & CHANNELS blocks are handled idiomatically in dedicated lexical modes.

OPTIONS		: 'options'		-> pushMode(Options)	;
TOKENS		: 'tokens'		-> pushMode(Tokens)		;
CHANNELS	: 'channels'	-> pushMode(Channels)	;

IMPORT		: 'import'		;
FRAGMENT	: 'fragment'	;
LEXER		: 'lexer'		;
PARSER		: 'parser'		;
GRAMMAR		: 'grammar'		;
PROTECTED	: 'protected'	;
PUBLIC		: 'public'		;
PRIVATE		: 'private'		;
RETURNS		: 'returns'		;
LOCALS		: 'locals'		;
THROWS		: 'throws'		;
CATCH		: 'catch'		;
FINALLY		: 'finally'		;
MODE		: 'mode'		;


// -------------------------
// Punctuation

COLON		: Colon			;
COLONCOLON	: DColon		;
COMMA		: Comma			;
SEMI		: Semi			;
LPAREN		: LParen		;
RPAREN		: RParen		;
LBRACE		: LBrace		;
RBRACE		: RBrace		;
RARROW		: RArrow		;
LT			: Lt			;
GT			: Gt			;
ASSIGN		: Equal			;
QUESTION	: Question		;
STAR		: Star			;
PLUS_ASSIGN	: PlusAssign	;
PLUS		: Plus			;
OR			: Pipe			;
DOLLAR		: Dollar		;
RANGE		: Range			;
DOT			: Dot			;
AT			: At			;
POUND		: Pound			;
NOT			: Tilde			;


// -------------------------
// Identifiers - allows unicode rule/token names

ID	: Id
	;


// -------------------------
// Whitespace

WS	:	( Hws | Vws )+		-> channel(OFF_CHANNEL)	;


// -------------------------
// Illegal Characters
//
// This is an illegal character trap which is always the last rule in the
// lexer specification. It matches a single character of any value and being
// the last rule in the file will match when no other rule knows what to do
// about the character. It is reported as an error but is not passed on to the
// parser. This means that the parser to deal with the gramamr file anyway
// but we will not try to analyse or code generate from a file with lexical
// errors.
//
// Comment this rule out to allow the error to be propagated to the parser

ERRCHAR
	:	.	-> channel(HIDDEN)
	;


// ======================================================
// Lexer modes

// -------------------------
// Arguments

mode Argument;			// E.g., [int x, List<String> a[]]

	NESTED_ARGUMENT			: LBrack			-> type(ARGUMENT_CONTENT), pushMode(Argument)	;

	ARGUMENT_ESCAPE			: EscAny			-> type(ARGUMENT_CONTENT)		;

	ARGUMENT_STRING_LITERAL	: DQuoteLiteral	-> type(ARGUMENT_CONTENT)		;
	ARGUMENT_CHAR_LITERAL	: SQuoteLiteral	-> type(ARGUMENT_CONTENT)		;

	END_ARGUMENT			: RBrack	{ handleEndArgument(); }	;

	// added this to return non-EOF token type here. EOF does something weird
	UNTERMINATED_ARGUMENT 	: EOF		-> popMode		;

	ARGUMENT_CONTENT		: .							;


// -------------------------
// Actions
//
// Many language targets use {} as block delimiters and so we
// must recursively match {} delimited blocks to balance the
// braces. Additionally, we must make some assumptions about
// literal string representation in the target language. We assume
// that they are delimited by ' or " and so consume these
// in their own alts so as not to inadvertantly match {}.

mode Action;

	NESTED_ACTION			: LBrace			-> type(ACTION_CONTENT), pushMode(Action)	;

	ACTION_ESCAPE			: EscAny			-> type(ACTION_CONTENT)		;

	ACTION_STRING_LITERAL	: DQuoteLiteral		-> type(ACTION_CONTENT)		;
	ACTION_CHAR_LITERAL		: SQuoteLiteral		-> type(ACTION_CONTENT)		;

	ACTION_DOC_COMMENT		: DocComment		-> type(ACTION_CONTENT)		;
	ACTION_BLOCK_COMMENT	: BlockComment 		-> type(ACTION_CONTENT)		;
	ACTION_LINE_COMMENT		: LineComment 		-> type(ACTION_CONTENT)		;

	END_ACTION				: RBrace	{ handleEndAction(); }	;

	UNTERMINATED_ACTION		: EOF		-> popMode		;

	ACTION_CONTENT			: .							;


// -------------------------

mode Options;

	OPT_DOC_COMMENT		: DocComment		-> type(DOC_COMMENT), channel(OFF_CHANNEL)		;
	OPT_BLOCK_COMMENT	: BlockComment 		-> type(BLOCK_COMMENT), channel(OFF_CHANNEL)	;
	OPT_LINE_COMMENT	: LineComment 		-> type(LINE_COMMENT), channel(OFF_CHANNEL)		;

	OPT_LBRACE			: LBrace			-> type(LBRACE)				;
	OPT_RBRACE			: RBrace			-> type(RBRACE), popMode	;

	OPT_ID				: Id				-> type(ID)					;
	OPT_DOT				: Dot				-> type(DOT)				;
	OPT_ASSIGN			: Equal				-> type(ASSIGN)				;
	OPT_STRING_LITERAL	: SQuoteLiteral		-> type(STRING_LITERAL)		;
	OPT_INT				: Int				-> type(INT)				;
	OPT_STAR			: Star				-> type(STAR)				;
	OPT_SEMI			: Semi				-> type(SEMI)				;

	OPT_WS				: ( Hws | Vws )+	-> type(WS), channel(OFF_CHANNEL) 	;


// -------------------------

mode Tokens;

	TOK_DOC_COMMENT		: DocComment		-> type(DOC_COMMENT), channel(OFF_CHANNEL)		;
	TOK_BLOCK_COMMENT	: BlockComment 		-> type(BLOCK_COMMENT), channel(OFF_CHANNEL)	;
	TOK_LINE_COMMENT	: LineComment 		-> type(LINE_COMMENT), channel(OFF_CHANNEL)		;

	TOK_LBRACE			: LBrace			-> type(LBRACE)				;
	TOK_RBRACE			: RBrace			-> type(RBRACE), popMode	;

	TOK_ID				: Id				-> type(ID)					;
	TOK_DOT				: Dot				-> type(DOT)				;
	TOK_COMMA			: Comma				-> type(COMMA)				;

	TOK_WS				: ( Hws | Vws )+	-> type(WS), channel(OFF_CHANNEL) 	;


// -------------------------

mode Channels;	// currently same as Tokens mode; distinguished by keyword

	CHN_DOC_COMMENT		: DocComment		-> type(DOC_COMMENT), channel(OFF_CHANNEL)		;
	CHN_BLOCK_COMMENT	: BlockComment 		-> type(BLOCK_COMMENT), channel(OFF_CHANNEL)	;
	CHN_LINE_COMMENT	: LineComment 		-> type(LINE_COMMENT), channel(OFF_CHANNEL)		;

	CHN_LBRACE			: LBrace			-> type(LBRACE)				;
	CHN_RBRACE			: RBrace			-> type(RBRACE), popMode	;

	CHN_ID				: Id				-> type(ID)					;
	CHN_DOT				: Dot				-> type(DOT)				;
	CHN_COMMA			: Comma				-> type(COMMA)				;

	CHN_WS				: ( Hws | Vws )+	-> type(WS), channel(OFF_CHANNEL) 	;


// -------------------------

mode LexerCharSet;

	LEXER_CHAR_SET_BODY
		:	(	~[\]\\]
			|	EscAny
			)+				-> more
		;

	LEXER_CHAR_SET
		:	RBrack			-> popMode
		;

	UNTERMINATED_CHAR_SET
		:	EOF				-> popMode
		;


// ------------------------------------------------------------------------------
// Grammar specific Keywords, Punctuation, etc.

fragment Id	: NameStartChar NameChar*	;

