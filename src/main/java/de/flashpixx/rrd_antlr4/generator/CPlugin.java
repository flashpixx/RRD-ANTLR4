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

import de.flashpixx.rrd_antlr4.engine.template.ITemplate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.AbstractMavenReportRenderer;
import org.apache.maven.reporting.MavenReportRenderer;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * maven plugin generator
 */
public final class CPlugin extends IBaseGenerator
{
    /**
     * base directory of grammar files
     */
    private final File m_basedirectory;
    /**
     * report title
     */
    private final String m_reporttitle;
    /**
     * report
     */
    private final MavenReportRenderer m_report;
    /**
     * set with all files
     */
    private final Set<File> m_files = new HashSet<>();


    /**
     * ctor
     *
     * @param p_sink report sink
     * @param p_reporttitle report title
     * @param p_baseoutputdirectory base output directory
     * @param p_basedirectory base directory
     * @param p_imports set with imported grammar files
     * @param p_docuclean set with documentation strings
     * @param p_templates array with exporting templates
     */
    public CPlugin( final Sink p_sink, final String p_reporttitle, final File p_baseoutputdirectory, final File p_basedirectory,
                    final Set<File> p_imports, final Set<String> p_docuclean, final Set<ITemplate> p_templates
    )
    {
        super( p_baseoutputdirectory, p_imports, p_docuclean, p_templates );
        m_basedirectory = p_basedirectory;
        m_reporttitle = p_reporttitle;
        m_report = new CReportGenerator( p_sink );
    }



    @Override
    public final IGenerator finish()
    {
        if ( !m_error )
            m_report.render();
        return this;
    }

    @Override
    protected final File processoutputdirectory( final File p_grammar )
    {
        return new File( m_basedirectory.toURI().relativize( p_grammar.toURI() ).toString() );
    }

    @Override
    protected final IGenerator processmessages( final File p_grammar, final Collection<String> p_messages )
    {
        m_error = !p_messages.isEmpty();
        if ( !m_error )
            m_files.add( p_grammar );
        return this;
    }

    /**
     * report generator for encapsuling the Maven
     */
    private final class CReportGenerator extends AbstractMavenReportRenderer
    {
        /**
         * ctor
         *
         * @param p_sink the sink to use
         */
        CReportGenerator( final Sink p_sink )
        {
            super( p_sink );
        }

        @Override
        public final String getTitle()
        {
            return m_reporttitle;
        }

        @Override
        protected final void renderBody()
        {
            this.startSection( this.getTitle() );

            this.startTable();
            this.tableHeader( ArrayUtils.add( m_templates.stream().map( ITemplate::name ).toArray( String[]::new ), 0, "Grammar" ) );

            m_files.forEach( i -> this.tableRow(
                new String[]{
                    m_basedirectory.toURI().relativize( i.toURI() ).toString(),
                    ""
                }
                             )
            );

            this.endTable();

            this.endSection();
        }
    }

}
