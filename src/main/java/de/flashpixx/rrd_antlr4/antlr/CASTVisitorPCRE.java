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
    public final Object visitCharacter_class( final PCREParser.Character_classContext p_context )
    {
        p_context.cc_atom().stream().forEach( i -> {
            System.out.println( i.getText() );
        } );
        System.out.println();

        return null;
    }

    @Override
    public final Object visitAtom( final PCREParser.AtomContext p_context )
    {
        // string / character definition
        if ( p_context.literal() != null )
            return p_context.literal().getText();

        // character class like [a-z]
        if ( p_context.character_class() != null )
            return this.visitCharacter_class( p_context.character_class() );

        return null;
    }

    @Override
    public final Object visitQuantifier( final PCREParser.QuantifierContext p_context )
    {
        return p_context.getText();
    }

    @Override
    public final Object visitElement( final PCREParser.ElementContext p_context )
    {
        //if (p_context.quantifier() != null)
        //    System.out.println( this.visitQuantifier( p_context.quantifier() ) );

        return this.visitAtom( p_context.atom() );
    }

    @Override
    public final Object visitExpr( final PCREParser.ExprContext p_context )
    {
        final List<?> l_elements = p_context.element().stream()
                                            .map( i -> this.visitElement( i ) )
                                            .filter( i -> i != null )
                                            .collect( Collectors.toList() );

        // strings will be concat into one string, so get all position of grammar elements
        final List<Integer> l_positiongrammar = IntStream.range( 0, l_elements.size() )
                                                         .boxed()
                                                         .filter( i -> l_elements.get( i ) instanceof IGrammarElement )
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
