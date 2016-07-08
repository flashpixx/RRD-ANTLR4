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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.flashpixx.rrd_antlr4.CCommon;
import de.flashpixx.rrd_antlr4.engine.template.ITemplate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.AbstractMavenReportRenderer;
import org.apache.maven.reporting.MavenReportRenderer;

import java.io.File;
import java.util.Collection;
import java.util.Set;


/**
 * maven plugin generator
 */
public final class CPlugin extends IBaseGenerator
{
    /**
     * report directory
     */
    private final File m_reportdirectory;
    /**
     * base directory of grammar files
     */
    private final File m_grammarbase;
    /**
     * report title
     */
    private final String m_reporttitle;
    /**
     * report
     */
    private final MavenReportRenderer m_report;
    /**
     * map with grammar files, pair of template name and link
     */
    private final Multimap<File, Pair<String, String>> m_files = HashMultimap.create();


    /**
     * ctor
     *
     * @param p_report maven project reference
     * @param p_reporttitle report title
     * @param p_baseoutputdirectory base output directory
     * @param p_grammarbase base directory of grammar files
     * @param p_imports set with imported grammar files
     * @param p_docuclean set with documentation strings
     * @param p_templates array with exporting templates
     */
    public CPlugin( final AbstractMavenReport p_report, final String p_reporttitle, final File p_baseoutputdirectory, final File p_grammarbase,
                    final Set<File> p_imports, final Set<String> p_docuclean, final Set<ITemplate> p_templates
    )
    {
        super( p_baseoutputdirectory, p_imports, p_docuclean, p_templates );
        m_grammarbase = p_grammarbase;
        m_reporttitle = p_reporttitle;
        m_report = new CReportGenerator( p_report.getSink() );
        m_reportdirectory = p_report.getReportOutputDirectory();
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
        return new File( m_grammarbase.toURI().relativize( p_grammar.toURI() ).toString() );
    }

    @Override
    protected final IGenerator processmessages( final File p_grammar, final File p_outputdirectory, final Collection<String> p_messages )
    {
        m_error = !p_messages.isEmpty();
        if ( !m_error )
            m_templates
                .forEach( i -> m_files.put(
                                   p_grammar,
                                   new ImmutablePair<>(
                                       i.name(),
                                       m_reportdirectory.toURI().relativize(
                                           CCommon.outputdirectory( m_baseoutput, i, p_outputdirectory, i.index() ).toUri()
                                       ).toString()
                                   )
                          )
                );

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
            this.tableHeader( ArrayUtils.add( m_templates.stream().map( i -> "" ).toArray( String[]::new ), 0, "Grammar" ) );

            m_files.asMap().entrySet().forEach( i -> {

                sink.tableRow();

                sink.tableCell();
                sink.text( m_grammarbase.toURI().relativize( i.getKey().toURI() ).toString() );
                sink.tableCell_();

                i.getValue()
                    .forEach( j -> {
                        sink.tableCell();
                        sink.link( j.getRight() );
                        sink.text( j.getLeft() );
                        sink.tableCell_();
                    } );

                sink.tableRow_();
            } );
            this.endTable();

            this.endSection();
        }
    }

}
