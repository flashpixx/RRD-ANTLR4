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
 * A generally reusable set of fragments for import in to Lexer grammars.
 *
 *	Modified 2015.06.16 gbr - 
 *	-- generalized for inclusion into the ANTLRv4 grammar distribution
 * 
 */
  
lexer grammar LexBasic;

import LexUnicode;	// Formal set of Unicode ranges


// ======================================================
// Lexer fragments
//


// -----------------------------------
// Whitespace & Comments

fragment Ws				: Hws | Vws	;
fragment Hws			: [ \t]		;
fragment Vws			: [\r\n\f]	;

fragment DocComment		: '/**' .*? ('*/' | EOF)	;
fragment BlockComment	: '/*'  .*? ('*/' | EOF)	;

fragment LineComment	: '//' ~[\r\n]* 							;
fragment LineCommentExt	: '//' ~'\n'* ( '\n' Hws* '//' ~'\n'* )*	;


// -----------------------------------
// Escapes

// Any kind of escaped character that we can embed within ANTLR literal strings.
fragment EscSeq
	:	Esc
		( [btnfr"'\\]	// The standard escaped character set such as tab, newline, etc.
		| UnicodeEsc	// A Unicode escape sequence
		| .				// Invalid escape character
		| EOF			// Incomplete at EOF
		)
	;

fragment EscAny
	:	Esc .
	;

fragment UnicodeEsc
	:	'u' (HexDigit (HexDigit (HexDigit HexDigit?)?)?)?
	;

fragment OctalEscape
	:	OctalDigit
	|	OctalDigit OctalDigit
	|	[0-3] OctalDigit OctalDigit
	;


// -----------------------------------
// Numerals

fragment HexNumeral
	:	'0' [xX] HexDigits
	;

fragment OctalNumeral
	:	'0' '_' OctalDigits
	;

fragment DecimalNumeral
	:	'0'
	|	[1-9] DecDigit*
	;

fragment BinaryNumeral
	:	'0' [bB] BinaryDigits
	;


// -----------------------------------
// Digits

fragment HexDigits		: HexDigit+		;
fragment DecDigits		: DecDigit+		;
fragment OctalDigits	: OctalDigit+	;
fragment BinaryDigits	: BinaryDigit+	;

fragment HexDigit		: [0-9a-fA-F]	;
fragment DecDigit		: [0-9]			;
fragment OctalDigit		: [0-7]			;
fragment BinaryDigit	: [01]			;


// -----------------------------------
// Literals

fragment BoolLiteral	: True | False								;

fragment CharLiteral	: SQuote ( EscSeq | ~['\r\n\\] )  SQuote	;
fragment SQuoteLiteral	: SQuote ( EscSeq | ~['\r\n\\] )* SQuote	;
fragment DQuoteLiteral	: DQuote ( EscSeq | ~["\r\n\\] )* DQuote	;
fragment USQuoteLiteral	: SQuote ( EscSeq | ~['\r\n\\] )* 			;

fragment DecimalFloatingPointLiteral
	:   DecDigits DOT DecDigits? ExponentPart? FloatTypeSuffix?
	|   DOT DecDigits ExponentPart? FloatTypeSuffix?
	|	DecDigits ExponentPart FloatTypeSuffix?
	|	DecDigits FloatTypeSuffix
	;

fragment ExponentPart
	:	[eE] [+-]? DecDigits
	;

fragment FloatTypeSuffix
	:	[fFdD]
	;

fragment HexadecimalFloatingPointLiteral
	:	HexSignificand BinaryExponent FloatTypeSuffix?
	;

fragment HexSignificand
	:   HexNumeral DOT?
	|   '0' [xX] HexDigits? DOT HexDigits
	;

fragment BinaryExponent
	:	[pP] [+-]? DecDigits
	;


// -----------------------------------
// Character ranges

fragment NameChar
	:	NameStartChar
	|	'0'..'9'
	|	Underscore
	|	'\u00B7'
	|	'\u0300'..'\u036F'
	|	'\u203F'..'\u2040'
	;

fragment NameStartChar
	:	'A'..'Z'
	|	'a'..'z'
	|	'\u00C0'..'\u00D6'
	|	'\u00D8'..'\u00F6'
	|	'\u00F8'..'\u02FF'
	|	'\u0370'..'\u037D'
	|	'\u037F'..'\u1FFF'
	|	'\u200C'..'\u200D'
	|	'\u2070'..'\u218F'
	|	'\u2C00'..'\u2FEF'
	|	'\u3001'..'\uD7FF'
	|	'\uF900'..'\uFDCF'
	|	'\uFDF0'..'\uFFFD'
	;	// ignores | ['\u10000-'\uEFFFF] ;


fragment JavaLetter
	:   [a-zA-Z$_] // "java letters" below 0xFF
	|	JavaUnicodeChars
	;

fragment JavaLetterOrDigit
	:   [a-zA-Z0-9$_] // "java letters or digits" below 0xFF
	|	JavaUnicodeChars
	;

// covers all characters above 0xFF which are not a surrogate
// and UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
fragment JavaUnicodeChars
	: ~[\u0000-\u00FF\uD800-\uDBFF]		{Character.isJavaIdentifierPart(_input.LA(-1))}?
	|  [\uD800-\uDBFF] [\uDC00-\uDFFF]	{Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
	;


// -----------------------------------
// Types

fragment Boolean		: 'boolean'	;
fragment Byte			: 'byte'	;
fragment Short			: 'short'	;
fragment Int			: 'int'		;
fragment Long			: 'long'	;
fragment Char			: 'char'	;
fragment Float			: 'float'	;
fragment Double 		: 'double'	;

fragment True		 	: 'true'	;
fragment False			: 'false'	;


// -----------------------------------
// Symbols

fragment Esc			: '\\'	;
fragment Colon			: ':'	;
fragment DColon			: '::'	;
fragment SQuote			: '\''	;
fragment DQuote			: '"'	;
fragment BQuote			: '`'	;
fragment LParen			: '('	;
fragment RParen			: ')'	;
fragment LBrace			: '{'	;
fragment RBrace			: '}'	;
fragment LBrack			: '['	;
fragment RBrack			: ']'	;
fragment RArrow			: '->'	;
fragment Lt				: '<'	;
fragment Gt				: '>'	;
fragment Lte			: '<='	;
fragment Gte			: '>='	;
fragment Equal			: '='	;
fragment NotEqual		: '!='	;
fragment Question		: '?'	;
fragment Bang			: '!'	;
fragment Star			: '*'	;
fragment Slash			: '/'	;
fragment Percent		: '%'	;
fragment Caret			: '^'	;
fragment Plus			: '+'	;
fragment Minus			: '-'	;
fragment PlusAssign		: '+='	;
fragment MinusAssign	: '-='	;
fragment MulAssign		: '*='	;
fragment DivAssign		: '/='	;
fragment AndAssign		: '&='	;
fragment OrAssign		: '|='	;
fragment XOrAssign		: '^='	;
fragment ModAssign		: '%='	;
fragment LShiftAssign	: '<<='	;
fragment RShiftAssign	: '>>='	;
fragment URShiftAssign	: '>>>=';
fragment Underscore		: '_'	;
fragment Pipe			: '|'	;
fragment Amp			: '&'	;
fragment And			: '&&'	;
fragment Or				: '||'	;
fragment Inc			: '++'	;
fragment Dec			: '--'	;
fragment LShift			: '<<'	;
fragment RShift			: '>>'	;
fragment Dollar			: '$'	;
fragment Comma			: ','	;
fragment Semi			: ';'	;
fragment Dot			: '.'	;
fragment Range			: '..'	;
fragment Ellipsis		: '...'	;
fragment At				: '@'	;
fragment Pound			: '#'	;
fragment Tilde			: '~'	;
