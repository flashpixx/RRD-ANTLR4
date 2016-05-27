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

import de.flashpixx.rrd_antlr4.antlr.IGrammarComplexElement;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;


/**
 * interface of a de.flashpixx.rrd_antlr4.template
 */
public interface ITemplate
{

    /**
     * returns the name of the de.flashpixx.rrd_antlr4.template
     *
     * @return de.flashpixx.rrd_antlr4.template name
     */
    String name();

    /**
     * preprocessing (before AST visiting)
     *
     * @param p_output output directory
     */
    void preprocess( final Path p_output ) throws IOException, URISyntaxException;

    /**
     * postprocessing (after AST visiting)
     *
     * @param p_output working directory
     */
    void postprocess( final Path p_output ) throws IOException, URISyntaxException;

    /**
     * is called on the grammar definition
     *
     * @param p_grammar grammar
     * @return grammar object reference
     */
    IGrammarComplexElement grammar( final IGrammarComplexElement p_grammar );

    /**
     * is called if any grammar element is completed
     *
     * @param p_grammar grammar
     * @param p_element element
     * @return grammar object reference
     */
    IGrammarComplexElement element( final IGrammarComplexElement p_grammar, final IGrammarComplexElement p_element );

}
