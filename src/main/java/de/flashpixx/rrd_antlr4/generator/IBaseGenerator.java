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

import com.sun.xml.internal.ws.api.pipe.Engine;
import de.flashpixx.rrd_antlr4.engine.CEngine;
import de.flashpixx.rrd_antlr4.engine.template.ETemplate;
import de.flashpixx.rrd_antlr4.engine.template.ITemplate;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * base generator class with default definitions
 */
public abstract class IBaseGenerator implements IGenerator
{
    /**
     * engine instance
     */
    private static final CEngine ENGINE = new CEngine();
    /**
     * set of generator templates
     */
    private final Set<ITemplate> m_templates;
    /**
     * map with imported grammar files
     */
    private final Map<String, File> m_imports;
    /**
     * set with regex string for documentation cleaning
     */
    private final Set<String> m_docuclean;
    /**
     * result generator error
     */
    protected boolean m_error;

    /**
     * ctor
     *
     * @param p_templates array with exporting templates
     * @param p_imports set with imported grammar files
     * @param p_docuclean set with documentation strings
     */
    protected IBaseGenerator( final Set<File> p_imports, final Set<String> p_docuclean, final ITemplate... p_templates )
    {
        m_docuclean = p_docuclean;
        m_templates = Collections.unmodifiableSet( Arrays.stream( p_templates ).collect( Collectors.toSet() ) );
        m_imports = Collections.unmodifiableMap( p_imports.parallelStream().collect( Collectors.toMap( i -> FilenameUtils.removeExtension( i.getName() ), i -> i ) ) );
    }


    @Override
    public final IGenerator generate( final File p_grammar, final File p_outputdirectory )
    {
        try
        {
            return this.processmessages(
                p_grammar,
                ENGINE.generate(
                    this.processoutputdirectory( p_grammar, p_outputdirectory ),
                    p_grammar,
                    m_docuclean,
                    m_imports,
                    m_templates
                )
            );
        }
        catch ( final IOException l_exception )
        {
            return this.processmessages( p_grammar, Collections.unmodifiableSet( Stream.of( l_exception.getMessage() ).collect( Collectors.toSet() ) ) );
        }
    }

    @Override
    public final boolean hasError()
    {
        return m_error;
    }

    /**
     * processes the output directory
     *
     * @param p_grammar input grammar file
     * @param p_outputdirectory output directory
     * @return new output directory
     */
    protected abstract File processoutputdirectory( final File p_grammar, final File p_outputdirectory );

    /**
     * processes the error messages
     *
     * @param p_grammar input grammar file
     * @param p_messages error messages
     * @return generator self reference
     */
    protected abstract IGenerator processmessages( final File p_grammar, final Collection<String> p_messages );

}
