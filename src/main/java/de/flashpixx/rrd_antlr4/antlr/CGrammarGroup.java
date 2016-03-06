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

import de.flashpixx.rrd_antlr4.CCommon;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;


/**
 * group of grammar elements
 */
public final class CGrammarGroup implements IGrammarGroup
{
    /**
     * grammar element
     */
    private final IGrammarElement m_element;
    /**
     * cardinality
     */
    private ECardinality m_cardinality;


    /**
     * ctor
     *
     * @param p_element data
     */
    public CGrammarGroup( final IGrammarElement p_element )
    {
        m_element = p_element;
        m_cardinality = ECardinality.NONE;

        if ( m_element == null )
            throw new IllegalArgumentException( CCommon.getLanguageString( this, "empty" ) );
    }

    /**
     * ctor
     *
     * @param p_cardinality cardinality
     * @param p_element data
     */
    public CGrammarGroup( final ECardinality p_cardinality, final IGrammarElement p_element )
    {
        m_element = p_element;
        m_cardinality = p_cardinality;

        if ( m_element == null )
            throw new IllegalArgumentException( CCommon.getLanguageString( this, "empty" ) );
    }

    @Override
    public final String toString()
    {
        return MessageFormat.format( "( {0} ){1}", StringUtils.join( m_element, ", " ), m_cardinality ).trim();
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
    public final IGrammarElement element()
    {
        return m_element;
    }

    @Override
    public final int hashCode()
    {
        return m_element.hashCode() + m_cardinality.hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return this.hashCode() == p_object.hashCode();
    }
}
