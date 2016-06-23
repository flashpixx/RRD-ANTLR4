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

import java.text.MessageFormat;


/**
 * negation
 */
public final class CGrammarNegation implements IGrammarNegation
{
    /**
     * grammar
     * element
     */
    private final IGrammarElement m_element;
    /**
     * cardinality
     */
    private ECardinality m_cardinality;

    /**
     * ctor
     *
     * @param p_element grammar element
     */
    public CGrammarNegation( final IGrammarElement p_element )
    {
        this( ECardinality.NONE, p_element );
    }

    /**
     * ctor
     *
     * @param p_cardinality cardinality
     * @param p_element grammar element
     */
    public CGrammarNegation( final ECardinality p_cardinality, final IGrammarElement p_element )
    {
        m_element = p_element;
        m_cardinality = p_cardinality;
    }


    @Override
    public final IGrammarElement inner()
    {
        return m_element;
    }

    @Override
    public final ECardinality cardinality()
    {
        return m_cardinality;
    }

    @Override
    public final IGrammarElement cardinality( final ECardinality p_cardinality )
    {
        m_cardinality = p_cardinality;
        return this;
    }

    @Override
    public final int hashCode()
    {
        return m_element.hashCode() + 37991;
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return ( p_object != null ) && ( p_object instanceof IGrammarNegation ) && ( this.hashCode() == p_object.hashCode() );
    }

    @Override
    public final String toString()
    {
        return MessageFormat.format( "Negation( {0} ){1}", m_element, m_cardinality ).trim();
    }
}
