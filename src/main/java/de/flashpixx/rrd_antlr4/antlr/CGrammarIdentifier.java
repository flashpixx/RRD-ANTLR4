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

import java.util.Arrays;


/**
 * link class between two rules
 */
public final class CGrammarIdentifier implements IGrammarSimpleElement<String>
{
    /**
     * rule name
     */
    private final String m_value;

    /**
     * ctor
     *
     * @param p_value rule name
     */
    public CGrammarIdentifier( final String p_value )
    {
        m_value = p_value;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final <N> N get()
    {
        return (N) m_value;
    }

    @Override
    public final boolean isValueAssignableTo( final Class<?>... p_class )
    {
        return m_value == null ? true : Arrays.asList( p_class ).stream().map( i -> i.isAssignableFrom( m_value.getClass() ) ).anyMatch( i -> i );
    }

    @Override
    public final int hashCode()
    {
        return m_value.hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return this.hashCode() == p_object.hashCode();
    }

    @Override
    public final String toString()
    {
        return m_value;
    }

    @Override
    public final ECardinality cardinality()
    {
        return ECardinality.NONE;
    }

    @Override
    public final IGrammarElement cardinality( final ECardinality p_cardinality )
    {
        return this;
    }
}
