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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.flashpixx.rrd_antlr4.CCommon;
import de.flashpixx.rrd_antlr4.antlr.IGrammarChoice;
import de.flashpixx.rrd_antlr4.antlr.IGrammarCollection;
import de.flashpixx.rrd_antlr4.antlr.IGrammarComplexElement;
import de.flashpixx.rrd_antlr4.antlr.IGrammarElement;
import de.flashpixx.rrd_antlr4.antlr.IGrammarGroup;
import de.flashpixx.rrd_antlr4.antlr.IGrammarIdentifier;
import de.flashpixx.rrd_antlr4.antlr.IGrammarSimpleElement;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.stream.Collectors;


/**
 * template for LaTeX export
 */
public final class CLaTeX extends IBaseTemplate
{
    /**
     * main grammar
     */
    private IGrammarComplexElement m_grammar;
    /**
     * rules table for text representation
     */
    private final Table<String, String, String> m_rulestext = HashBasedTable.create();
    /**
     * rules table for diagram representation
     */
    private final Table<String, String, String> m_rulesdiagram = HashBasedTable.create();

    /**
     * ctor
     *
     * @param p_name template name
     */
    public CLaTeX( final String p_name )
    {
        super( p_name );
    }



    @Override
    public final void preprocess( final Path p_output ) throws IOException, URISyntaxException
    {

    }

    @Override
    public final void postprocess( final Path p_output ) throws IOException, URISyntaxException
    {
        this.copy( "index.tex", p_output );

        // replace content
        this.replace(
                new File( p_output.toString(), "/index.tex" ),

                // set title
                "-title-", CCommon.getLanguageString( this, "section", m_grammar.id() ),

                // set grammar documentation
                "-grammardocumentation-", m_grammar.documentation(),

                // set rules of diagrams
                "-rules-", StringUtils.join(
                        m_rulestext.rowMap().entrySet().stream().sorted( ( n, m ) -> n.getKey().compareToIgnoreCase( m.getKey() ) )
                                   .map( i -> MessageFormat.format(
                                       "\\section*\\{{0}\\}\n{1}",
                                       CCommon.getLanguageString( this, "subsection", i.getKey() ),
                                       StringUtils.join(
                                               i.getValue().entrySet().stream()
                                                .sorted( ( n, m ) -> n.getKey().compareToIgnoreCase( m.getKey() ) )
                                                .map( j -> j.getValue() )
                                                .collect( Collectors.toList() ),
                                               "\n"
                                       ).trim()
                                     )
                               )
                                   .collect( Collectors.toList() ),
                        "\n\n"
                )
        );
    }

    @Override
    public final IGrammarComplexElement grammar( final IGrammarComplexElement p_grammar )
    {
        // set only if is not net
        if ( m_grammar == null )
            m_grammar = p_grammar;

        return p_grammar;
    }

    @Override
    public final IGrammarComplexElement element( final IGrammarComplexElement p_grammar, final IGrammarComplexElement p_element )
    {
        return p_grammar;
    }

    @Override
    protected final String cardinality( final IGrammarElement.ECardinality p_cardinality, final String p_inner )
    {
        return null;
    }

    @Override
    protected final String sequence( final IGrammarCollection p_input )
    {
        return null;
    }

    @Override
    protected final String choice( final IGrammarChoice p_input )
    {
        return null;
    }

    @Override
    protected final String group( final IGrammarGroup p_group )
    {
        return null;
    }

    @Override
    protected final String terminalvalue( final IGrammarSimpleElement<?> p_value )
    {
        return null;
    }

    @Override
    protected final String nonterminal( final IGrammarIdentifier p_element )
    {
        return null;
    }

}
