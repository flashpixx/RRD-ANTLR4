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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * abstract class for collection
 */
public abstract class IGrammarBaseCollection implements IGrammarCollection
{
    /**
     * collection data
     */
    protected final List<IGrammarElement> m_data;
    /**
     * cardinality
     */
    protected ECardinality m_cardinality;


    /**
     * ctor
     *
     * @param p_cardinality cardinality
     * @param p_data data
     * @note filtering of not-null values
     */
    protected IGrammarBaseCollection( final ECardinality p_cardinality, final List<IGrammarElement> p_data )
    {
        if ( p_data == null )
            throw new IllegalArgumentException( CCommon.getLanguageString( IGrammarBaseCollection.class, "empty" ) );

        m_data = Collections.unmodifiableList( p_data.stream().filter( i -> i != null ).collect( Collectors.toList() ) );
        m_cardinality = p_cardinality;
    }

    @Override
    public final List<IGrammarElement> get()
    {
        return m_data;
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
        return m_cardinality.hashCode() + m_data.stream().mapToInt( i -> i.hashCode() ).sum();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return this.hashCode() == p_object.hashCode();
    }
}
