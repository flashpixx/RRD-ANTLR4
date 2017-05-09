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

package de.flashpixx.rrd_antlr4;

import java.util.regex.Matcher;


/**
 * class for replacing string content
 */
public final class CStringReplace
{
    /**
     * string data
     */
    private String m_data;

    /**
     * ctor
     *
     * @param p_data input data
     */
    public CStringReplace( final String p_data )
    {
        m_data = p_data;
    }

    /**
     * replaces the content
     *
     * @param p_search source
     * @param p_replace target
     * @return object reference
     */
    public final CStringReplace replaceAll( final String p_search, final String p_replace )
    {
        m_data = m_data.replaceAll( p_search, Matcher.quoteReplacement( p_replace ) );
        return this;
    }

    /**
     * replace a char sequence
     *
     * @param p_search search char sequence
     * @param p_replace replace char sequence
     * @return object reference
     */
    public final CStringReplace replace( final CharSequence p_search, final CharSequence p_replace )
    {
        m_data = m_data.replace( p_search, p_replace );
        return this;
    }

    /**
     * returns the data
     *
     * @return string
     */
    public final String get()
    {
        return m_data;
    }

    @Override
    public final int hashCode()
    {
        return m_data.hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return ( p_object != null ) && ( p_object instanceof CStringReplace ) && ( m_data.hashCode() == p_object.hashCode() );
    }

    @Override
    public final String toString()
    {
        return m_data;
    }
}
