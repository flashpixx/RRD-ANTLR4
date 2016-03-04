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
import java.util.Arrays;


/**
 * stores any terminal value
 */
public final class CGrammarTerminal<T> implements IGrammarSimpleElement<T>
{
    /**
     * terminal value
     */
    private final T m_value;
    /**
     * cardinality
     */
    private ECardinality m_cardinality;

    /**
     * ctor
     *
     * @param p_cardinality cardinality
     * @param p_value value
     */
    public CGrammarTerminal( final ECardinality p_cardinality, final T p_value )
    {
        m_value = p_value;
        m_cardinality = p_cardinality;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final <N> N get()
    {
        return (N) m_value;
    }

    @Override
    public final int hashCode()
    {
        return m_value.hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return m_value.hashCode() == p_object.hashCode();
    }

    @Override
    public final String toString()
    {
        return MessageFormat.format( "Terminal( {0} ){1}", m_value, m_cardinality );
    }

    @Override
    public final boolean isValueAssignableTo( final Class<?>... p_class )
    {
        return m_value == null ? true : Arrays.asList( p_class ).stream().map( i -> i.isAssignableFrom( m_value.getClass() ) ).anyMatch( i -> i );
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
}
