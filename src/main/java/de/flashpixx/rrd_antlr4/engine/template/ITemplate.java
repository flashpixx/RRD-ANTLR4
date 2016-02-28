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

package de.flashpixx.rrd_antlr4.engine.template;

import de.flashpixx.rrd_antlr4.antlr.IGrammarRule;
import de.flashpixx.rrd_antlr4.antlr.IGrammarTerminal;

import java.nio.file.Path;


/**
 * interface of a template
 */
public interface ITemplate
{

    /**
     * preprocessing (before AST visiting)
     *
     * @param p_outputdirectory output directory
     * @param p_grammar grammar file name (without path)
     */
    void preprocess( final Path p_outputdirectory, final String p_grammar );

    /**
     * postprocessing (after AST visiting)
     *
     * @param p_outputdirectory working directory
     * @param p_grammar grammar file name (without path)
     */
    void postprocess( final Path p_outputdirectory, final String p_grammar );

    /**
     * is called if any grammar rule is created
     *
     * @param p_grammar grammar file name
     * @param p_rule rule
     */
    void rule( final String p_grammar, final IGrammarRule p_rule );

    /**
     * is called if a terminal is created
     *
     * @param p_grammar grammar file name
     * @param p_terminal terminal
     */
    void terminal( final String p_grammar, final IGrammarTerminal p_terminal );

}
