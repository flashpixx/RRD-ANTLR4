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

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * AntLR 4 AST visitor of perl regular expressions
 */
public final class CASTVisitorPCRE extends PCREBaseVisitor<Object>
{

    @Override
    public final Object visitCapture( final PCREParser.CaptureContext p_context )
    {
        return this.visitAlternation( p_context.alternation() );
    }

    @Override
    public final Object visitCharacter_class( final PCREParser.Character_classContext p_context )
    {
        return CCommon.choice(
                p_context.cc_atom().stream()
                         .map( i -> new CGrammarTerminalValue( i.getText() ) )
                         .collect( Collectors.toList() )
        );
    }

    @Override
    public final Object visitAtom( final PCREParser.AtomContext p_context )
    {
        // character class like [a-z]
        if ( p_context.character_class() != null )
            return this.visitCharacter_class( p_context.character_class() );

        // dot replaced with a fixed terminal but in the visitor rule above,
        // because other characters can be also returned
        if ( p_context.Dot() != null )
            return p_context.Dot().getText();

        // string / character definition
        if ( p_context.literal() != null )
            return p_context.literal().getText();

        // defines ^
        if ( p_context.Caret() != null )
            return p_context.getText();

        // defines choice a | b | c
        if ( p_context.capture() != null )
            return this.visitCapture( p_context.capture() );

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
        if ( p_context.quantifier() != null )
            System.out.println( this.visitQuantifier( p_context.quantifier() ) );
        // @bug quantifier cannot be set on a string, so return value of visitAtom
        // can be a grammar element or a string

        return this.visitAtom( p_context.atom() );
    }

    @Override
    public final Object visitExpr( final PCREParser.ExprContext p_context )
    {
        return CCommon.sequence(
                this.implode(
                        p_context.element().stream()
                                 .map( i -> this.visitElement( i ) )
                                 .filter( i -> i != null )
                                 .collect( Collectors.toList() )
                ).stream()
                    .map( i ->
                                  // a string can be a single dot, so that is "any character", but within the
                                  // element visitor rule cannot decide that is an "any character" element
                                  i instanceof String
                                  ? new CGrammarTerminalValue(
                                          i.equals( "." )
                                          ? de.flashpixx.rrd_antlr4.CCommon.getLanguageString( this, "anychar" )
                                          : i
                                  )
                                  : (IGrammarElement) i )
                    .collect( Collectors.toList() )
        );
    }

    @Override
    public final Object visitAlternation( final PCREParser.AlternationContext p_context )
    {
        return CCommon.choice(
                p_context.expr().stream()
                         .map( i -> (IGrammarElement) this.visitExpr( i ) )
                         .filter( i -> i != null )
                         .collect( Collectors.toList() )
        );
    }

    @Override
    public final Object visitParse( final PCREParser.ParseContext p_context )
    {
        final Object x = this.visitAlternation( p_context.alternation() );
        System.out.println( x );
        System.out.println( "--------" );
        return x;
    }

    /**
     * implodes a list of any objects, strings
     * will be concated into one string
     *
     * @param p_list list of objects
     * @return list with concated objects
     */
    private List<?> implode( final List<?> p_list )
    {
        final int l_start = IntStream.range( 0, p_list.size() ).boxed().filter( i -> p_list.get( i ) instanceof String ).findFirst().orElse( -1 );
        if ( l_start < 0 )
            return p_list;

        final int l_end = IntStream.range( l_start + 1, p_list.size() ).boxed().filter( i -> p_list.get( i ) instanceof String ).findFirst().orElse( -1 );
        if ( l_end < 0 )
            return p_list;

        return this.implode( new LinkedList<Object>()
        {{
            add( StringUtils.join( p_list.subList( l_start, l_end + 1 ), "" ) );
            if ( l_end < p_list.size() )
                addAll( CASTVisitorPCRE.this.implode( p_list.subList( l_end + 1, p_list.size() ) ) );
        }} );
    }
}
