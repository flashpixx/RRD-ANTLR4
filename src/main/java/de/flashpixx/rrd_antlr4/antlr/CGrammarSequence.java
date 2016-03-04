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

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;


/**
 * grammar sequence
 */
public final class CGrammarSequence extends IGrammarBaseCollection implements IGrammarChoice
{

    /**
     * ctor
     *
     * @param p_cardinality cardinality
     */
    public CGrammarSequence( final ECardinality p_cardinality )
    {
        this( p_cardinality, new LinkedList<>() );
    }

    /**
     * ctor
     *
     * @param p_cardinality cardinality
     * @param p_data data
     */
    public CGrammarSequence( final ECardinality p_cardinality, final List<IGrammarElement> p_data )
    {
        super( p_cardinality, p_data );
    }

    @Override
    public final String toString()
    {
        return MessageFormat.format( "Sequence( {0} )", StringUtils.join( m_data, ", " ) );
    }

}
