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

import de.flashpixx.rrd_antlr4.engine.template.ETemplate;

import java.io.File;
import java.util.Collection;
import java.util.Set;


/**
 * generator of a standalone program
 */
public final class CStandalone extends IBaseGenerator
{

    /**
     * ctor
     *
     * @param p_baseoutputdirectory base output directory
     * @param p_imports set with imported grammar files
     * @param p_docuclean set with documentation strings
     * @param p_templates array with exporting templates
     */
    public CStandalone( final File p_baseoutputdirectory, final Set<File> p_imports, final Set<String> p_docuclean, final Set<ETemplate> p_templates
    )
    {
        super( p_baseoutputdirectory, p_imports, p_docuclean, p_templates );
    }

    @Override
    protected File processoutputdirectory( final File p_grammar )
    {
        return new File( p_grammar.getName() );
    }

    @Override
    protected IGenerator processmessages( final File p_grammar, final File p_outputdirectory,  final Collection<String> p_messages )
    {
        m_error = !p_messages.isEmpty();
        p_messages.forEach( System.err::println );
        return this;
    }

}
