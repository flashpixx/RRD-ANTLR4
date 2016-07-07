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

package de.flashpixx.rrd_antlr4.generator;

import java.io.File;


/**
 * interface to generate data
 */
public interface IGenerator
{
    /**
     * generates export for a grammar file
     *
     * @param p_grammar grammar file
     * @param p_outputdirectory output directory
     * @return generator self reference
     */
    IGenerator generate( final File p_grammar, final File p_outputdirectory );

    /**
     * boolean error flag
     *
     * @return flag of errors
     */
    boolean hasError();

    /**
     * finishing after all grammar files are proceed
     *
     * @return generator self reference
     */
    IGenerator finish();

}
