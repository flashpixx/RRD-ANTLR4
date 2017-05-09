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

/*	A grammar for ANTLR v4 written in ANTLR v4.
 *
 *	Modified 2015.06.16 gbr
 *	-- update for compatibility with Antlr v4.5
 *	-- add mode for channels
 *	-- moved members to LexerAdaptor
 * 	-- move fragments to imports
 */

parser grammar ANTLRv4Parser;

options {
	tokenVocab = ANTLRv4Lexer ;
}

// The main entry point for parsing a v4 grammar.
grammarSpec
	:	DOC_COMMENT?
		grammarType id SEMI
		prequelConstruct*
		rules
		modeSpec*
		EOF
	;

grammarType
	:	(	LEXER GRAMMAR
		|	PARSER GRAMMAR
		|	GRAMMAR
		)
	;

// This is the list of all constructs that can be declared before
// the set of rules that compose the grammar, and is invoked 0..n
// times by the grammarPrequel rule.
prequelConstruct
	:	optionsSpec
	|	delegateGrammars
	|	tokensSpec
	|	channelsSpec
	|	action
	;


// ------------
// Options - things that affect analysis and/or code generation

optionsSpec
	:	OPTIONS LBRACE (option SEMI)* RBRACE
	;

option
	:	id ASSIGN optionValue
	;

optionValue
	:	id (DOT id)*
	|	STRING_LITERAL
	|	actionBlock			// TODO: is this valid?
	|	INT
	;

// ------------
// Delegates

delegateGrammars
	:	IMPORT delegateGrammar (COMMA delegateGrammar)* SEMI
	;

delegateGrammar
	:	id ASSIGN id
	|	id
	;


// ------------
// Tokens & Channels

tokensSpec
	:	TOKENS LBRACE idList? RBRACE
	;

channelsSpec
	:	CHANNELS LBRACE idList? RBRACE
	;

idList
	: id ( COMMA id )* COMMA?
	;


// Match stuff like @parser::members {int i;}
action
	:	AT (actionScopeName COLONCOLON)? id actionBlock
	;

// Scope names could collide with keywords; allow them as ids for action scopes
actionScopeName
	:	id
	|	LEXER
	|	PARSER
	;

actionBlock
	:	BEGIN_ACTION ACTION_CONTENT* END_ACTION
	;

argActionBlock
	:	BEGIN_ARGUMENT ARGUMENT_CONTENT* END_ARGUMENT
	;

modeSpec
	:	MODE id SEMI lexerRuleSpec*
	;

rules
	:	ruleSpec*
	;

ruleSpec
	:	parserRuleSpec
	|	lexerRuleSpec
	;

parserRuleSpec
	:	DOC_COMMENT?
		ruleModifiers? RULE_REF argActionBlock? ruleReturns? throwsSpec?
		localsSpec?
		rulePrequel*
		COLON
		ruleBlock
		SEMI
		exceptionGroup
	;

exceptionGroup
	:	exceptionHandler* finallyClause?
	;

exceptionHandler
	:	CATCH argActionBlock actionBlock
	;

finallyClause
	:	FINALLY actionBlock
	;

rulePrequel
	:	optionsSpec
	|	ruleAction
	;

ruleReturns
	:	RETURNS argActionBlock
	;

// --------------
// Exception spec

throwsSpec
	:	THROWS id (COMMA id)*
	;

localsSpec
	:	LOCALS argActionBlock
	;

/** Match stuff like @init {int i;} */
ruleAction
	:	AT id actionBlock
	;

ruleModifiers
	:	ruleModifier+
	;

// An individual access modifier for a rule. The 'fragment' modifier
// is an internal indication for lexer rules that they do not match
// from the input but are like subroutines for other lexer rules to
// reuse for certain lexical patterns. The other modifiers are passed
// to the code generation templates and may be ignored by the template
// if they are of no use in that language.
ruleModifier
	:	PUBLIC
	|	PRIVATE
	|	PROTECTED
	|	FRAGMENT
	;

ruleBlock
	:	ruleAltList
	;

ruleAltList
	:	labeledAlt (OR labeledAlt)*
	;

labeledAlt
	:	alternative (POUND id)?
	;

// --------------------
// Lexer rules

lexerRuleSpec
	:	DOC_COMMENT? FRAGMENT?
		TOKEN_REF COLON lexerRuleBlock SEMI
	;

lexerRuleBlock
	:	lexerAltList
	;

lexerAltList
	:	lexerAlt (OR lexerAlt)*
	;

lexerAlt
	:	lexerElements lexerCommands?
	|									// explicitly allow empty alts
	;

lexerElements
	:	lexerElement+
	;

lexerElement
	:	labeledLexerElement ebnfSuffix?
	|	lexerAtom ebnfSuffix?
	|	lexerBlock ebnfSuffix?
	|	actionBlock QUESTION?	// actions only allowed at end of outer alt actually,
	;							// but preds can be anywhere

labeledLexerElement
	:	id (ASSIGN|PLUS_ASSIGN)
		(	lexerAtom
		|	block
		)
	;

lexerBlock
	:	LPAREN lexerAltList RPAREN
	;

// E.g., channel(HIDDEN), skip, more, mode(INSIDE), push(INSIDE), pop
lexerCommands
	:	RARROW lexerCommand (COMMA lexerCommand)*
	;

lexerCommand
	:	lexerCommandName LPAREN lexerCommandExpr RPAREN
	|	lexerCommandName
	;

lexerCommandName
	:	id
	|	MODE
	;

lexerCommandExpr
	:	id
	|	INT
	;

// --------------------
// Rule Alts

altList
	:	alternative (OR alternative)*
	;

alternative
	:	elementOptions? element+
	|								// explicitly allow empty alts
	;

element
	:	labeledElement
		(	ebnfSuffix
		|
		)
	|	atom
		(	ebnfSuffix
		|
		)
	|	ebnf
	|	actionBlock QUESTION?		// SEMPRED is actionBlock followed by QUESTION
	;

labeledElement
	:	id ( ASSIGN | PLUS_ASSIGN )
		(	atom
		|	block
		)
	;

// --------------------
// EBNF and blocks

ebnf
	:	block blockSuffix?
	;

blockSuffix
	:	ebnfSuffix 		// Standard EBNF
	;

ebnfSuffix
	:	QUESTION QUESTION?
  	|	STAR QUESTION?
   	|	PLUS QUESTION?
	;

lexerAtom
	:	range
	|	terminal
	|	RULE_REF
	|	notSet
	|	LEXER_CHAR_SET
	|	DOT elementOptions?
	;

atom
	:	range 				// Range x..y - only valid in lexers
	|	terminal
	|	ruleref
	|	notSet
	|	DOT elementOptions?
	;

// --------------------
// Inverted element set

notSet
	:	NOT setElement
	|	NOT blockSet
	;

blockSet
	:	LPAREN setElement (OR setElement)* RPAREN
	;

setElement
	:	TOKEN_REF elementOptions?
	|	STRING_LITERAL elementOptions?
	|	range
	|	LEXER_CHAR_SET
	;

// -------------
// Grammar Block

block
	:	LPAREN
		( optionsSpec? ruleAction* COLON )?
		altList
		RPAREN
	;

// ----------------
// Parser rule ref

ruleref
	:	RULE_REF argActionBlock? elementOptions?
	;

// ---------------
// Character Range

range
	: STRING_LITERAL RANGE STRING_LITERAL
	;

terminal
	:   TOKEN_REF elementOptions?
	|   STRING_LITERAL elementOptions?
	;

// Terminals may be adorned with certain options when
// reference in the grammar: TOK<,,,>
elementOptions
	:	LT elementOption (COMMA elementOption)* GT
	;

elementOption
	:	id 									// default node option
	|	id ASSIGN (id | STRING_LITERAL)		// option assignment
	;

id	:	RULE_REF
	|	TOKEN_REF
	;