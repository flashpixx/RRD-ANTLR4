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

package de.flashpixx.rrd_antlr4.antlr;

import java.util.Collections;
import java.util.List;


/**
 * empty grammar collection
 */
final class CGrammarEmptyCollection implements IGrammarCollection
{
    /**
     * singletone instance
     */
    private static final CGrammarEmptyCollection INSTANCE = new CGrammarEmptyCollection();


    /**
     * ctor
     */
    private CGrammarEmptyCollection()
    {
    }

    @Override
    public final List<IGrammarElement> get()
    {
        return Collections.<IGrammarElement>emptyList();
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

    @Override
    public final int hashCode()
    {
        return INSTANCE.hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return ( p_object != null ) && ( p_object instanceof IGrammarCollection ) && ( this.hashCode() == p_object.hashCode() );
    }

    @Override
    public final String toString()
    {
        return "";
    }
}
