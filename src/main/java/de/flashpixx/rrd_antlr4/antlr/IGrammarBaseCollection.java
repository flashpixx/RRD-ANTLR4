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

import java.util.Collection;
import java.util.Collections;


/**
 * abstract class for collection
 */
public abstract class IGrammarBaseCollection implements IGrammarCollection
{
    /**
     * collection data
     */
    protected final Collection<IGrammarElement> m_data;
    /**
     * cardinality
     */
    protected ECardinality m_cardinality;


    /**
     * ctor
     *
     * @param p_cardinality cardinality
     * @param p_data data
     */
    protected IGrammarBaseCollection( final ECardinality p_cardinality, final Collection<IGrammarElement> p_data )
    {
        m_data = Collections.unmodifiableCollection( p_data );
        m_cardinality = p_cardinality;
    }

    @Override
    public final Collection<IGrammarElement> get()
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
}
