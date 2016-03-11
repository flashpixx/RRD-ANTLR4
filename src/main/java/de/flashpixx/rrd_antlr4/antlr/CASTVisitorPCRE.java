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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

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
    public final Object visitElement( final PCREParser.ElementContext p_context )
    {
        // return the atom and a quatifier of the atom
        return new ImmutablePair<>(
                this.visitAtom( p_context.atom() ),
                p_context.quantifier() != null
                ? p_context.quantifier().getText()
                : ""
        );
    }

    @Override
    public final Object visitExpr( final PCREParser.ExprContext p_context )
    {
        return CCommon.sequence(
                this.implode(
                        p_context.element().stream()

                                 // pair with grammar element or string and cardinality
                                 .map( i -> (Pair<?, String>) this.visitElement( i ) )
                                 .filter( i -> i != null )
                                 .collect( Collectors.toList() )
                ).stream()
                    .map( i -> CCommon.cardinality(
                            i.getRight(),

                            // a string can be a single dot, so that is "any character", but within the
                            // element visitor rule cannot decide that is an "any character" element
                            i.getLeft() instanceof String
                            ? new CGrammarTerminalValue(
                                    i.getLeft().equals( "." )
                                    ? de.flashpixx.rrd_antlr4.CCommon.getLanguageString( this, "anychar" )
                                    : i.getLeft()
                                  )
                            : (IGrammarElement) i.getLeft()
                          )
                    )
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
        return this.visitAlternation( p_context.alternation() );
    }

    /**
     * implodes a list of any objects, strings
     * will be concated into one string
     *
     * @param p_list list of pairs object & quantifier
     * @return list with concated objects and used quantifier
     */
    private List<Pair<?, String>> implode( final List<Pair<?, String>> p_list )
    {
        final int l_start = this.filter( 0, p_list );
        if ( l_start < 0 )
            return p_list;

        final int l_end = this.filter( l_start + 1, p_list );
        if ( l_end < 0 )
            return p_list;

        return this.implode( new LinkedList<Pair<?, String>>()
        {{
            add( new ImmutablePair<>(
                    StringUtils.join( p_list.subList( l_start, l_end + 1 ).stream().map( i -> i.getLeft().toString() ).collect( Collectors.toList() ), "" ),
                    p_list.get( l_end ).getRight()
            ) );

            if ( l_end < p_list.size() )
                addAll( CASTVisitorPCRE.this.implode( p_list.subList( l_end + 1, p_list.size() ) ) );
        }} );
    }

    /**
     * filters the pair list of string or quantifier
     *
     * @param p_start start index
     * @param p_list list
     * @return end position of a string or quantifier element, -1 on non found
     */
    private int filter( final int p_start, final List<Pair<?, String>> p_list )
    {
        return IntStream.range( p_start, p_list.size() )
                        .boxed()
                        .filter( i -> ( p_list.get( i ).getLeft() instanceof String ) || ( !p_list.get( i ).getRight().isEmpty() ) )
                        .findFirst()
                        .orElse( -1 );
    }

}