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
import de.flashpixx.rrd_antlr4.antlr.IGrammarRule;
import de.flashpixx.rrd_antlr4.antlr.IGrammarTerminal;

import java.nio.file.Path;


/**
 * template for HTML export
 */
public final class CHTML implements ITemplate
{
    /**
     * template name
     */
    private final String m_name;

    /**
     * ctor
     *
     * @param p_name template name
     */
    public CHTML( final String p_name )
    {
        m_name = p_name;
    }

    @Override
    public final String name()
    {
        return m_name;
    }

    @Override
    public final void preprocess( final Path p_outputdirectoryfinal )
    {

    }

    @Override
    public final void postprocess( final Path p_outputdirectoryfinal )
    {

    }

    @Override
    public final void grammar( final IGrammarComplexElement p_grammar )
    {

    }

    @Override
    public final void rule( final IGrammarComplexElement p_grammar, final IGrammarRule p_rule )
    {
        System.out.println( "---> " + p_grammar + "    " + p_rule );
    }

    @Override
    public final void terminal( final IGrammarComplexElement p_grammar, final IGrammarTerminal p_terminal )
    {
        System.out.println( "---> " + p_grammar + "    " + p_terminal );
    }

}
