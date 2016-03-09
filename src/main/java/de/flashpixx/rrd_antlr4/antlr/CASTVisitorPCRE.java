/**
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the RRD-AntLR4                                                #
 * # Copyright (c) 2016, Philipp Kraus (philipp.kraus@tu-clausthal.de)                  #
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

package de.flashpixx.rrd_antlr4.antlr;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * AntLR 4 AST visitor of perl regular expressions
 */
public final class CASTVisitorPCRE extends PCREBaseVisitor<Object>
{

    @Override
    public final Object visitLetter( final PCREParser.LetterContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitDigits( final PCREParser.DigitsContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitNumber( final PCREParser.NumberContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitAlpha_nums( final PCREParser.Alpha_numsContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitAtom( final PCREParser.AtomContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitDigit( final PCREParser.DigitContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitName( final PCREParser.NameContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitCharacter_class( final PCREParser.Character_classContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitLiteral( final PCREParser.LiteralContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitCc_atom( final PCREParser.Cc_atomContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitCc_literal( final PCREParser.Cc_literalContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitShared_literal( final PCREParser.Shared_literalContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitShared_atom( final PCREParser.Shared_atomContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitOctal_char( final PCREParser.Octal_charContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitOctal_digit( final PCREParser.Octal_digitContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitQuantifier( final PCREParser.QuantifierContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitQuantifier_type( final PCREParser.Quantifier_typeContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitBackreference( final PCREParser.BackreferenceContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitBackreference_or_octal( final PCREParser.Backreference_or_octalContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitCapture( final PCREParser.CaptureContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitNon_capture( final PCREParser.Non_captureContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitComment( final PCREParser.CommentContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitOption( final PCREParser.OptionContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitOption_flags( final PCREParser.Option_flagsContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitOption_flag( final PCREParser.Option_flagContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitLook_around( final PCREParser.Look_aroundContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitSubroutine_reference( final PCREParser.Subroutine_referenceContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitConditional( final PCREParser.ConditionalContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitBacktrack_control( final PCREParser.Backtrack_controlContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitNewline_convention( final PCREParser.Newline_conventionContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitCallout( final PCREParser.CalloutContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitNon_close_parens( final PCREParser.Non_close_parensContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitNon_close_paren( final PCREParser.Non_close_parenContext p_context )
    {
        return p_context.getText();
    }



    @Override
    public final Object visitElement( final PCREParser.ElementContext p_context )
    {

        return super.visitElement( p_context );
    }

    @Override
    public final Object visitExpr( final PCREParser.ExprContext p_context )
    {
        final List<?> l_elements = p_context.element().stream().map( i -> i.getText() ).collect( Collectors.toList() );

        // strings will be concat into one string, so get all position of grammar elements
        final List<Integer> l_positiongrammar = IntStream.range( 0, l_elements.size() ).boxed().filter( i -> l_elements.get( i ) instanceof IGrammarElement )
                                                         .collect( Collectors.toList() );
        if ( l_positiongrammar.isEmpty() )
            return new CGrammarTerminalValue<>( p_context.getText() );

        // concat strings between grammar elements

        return new CGrammarTerminalValue<>( p_context.getText() );
    }

    @Override
    public final Object visitAlternation( final PCREParser.AlternationContext p_context )
    {
        return CCommon.choice(
                p_context.expr().stream()
                         .map( i -> (IGrammarElement) this.visitExpr( i ) )
                         .collect( Collectors.toList() )
        );
    }

    @Override
    public final Object visitParse( final PCREParser.ParseContext p_context )
    {
        return this.visitAlternation( p_context.alternation() );
    }
}
