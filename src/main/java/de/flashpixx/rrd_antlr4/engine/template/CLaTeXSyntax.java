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
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * template for LaTeX text export
 */
public final class CLaTeXSyntax extends IBaseTemplate
{
    /**
     * main grammar
     */
    private IGrammarComplexElement m_grammar;
    /**
     * rules table for text representation
     */
    private final Table<String, String, String> m_rules = HashBasedTable.create();

    /**
     * ctor
     *
     * @param p_name template name
     */
    public CLaTeXSyntax( final String p_name )
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
                "-grammartitle-", CCommon.languagestring( this, "section", m_grammar.id() ),

                // set grammar documentation
                "-grammardocumentation-", m_grammar.documentation(),

                // set text rules
                "-rules-", StringUtils.join(
                        m_rules.rowMap().entrySet().stream().sorted( ( n, m ) -> n.getKey().compareToIgnoreCase( m.getKey() ) )
                               .map( i -> MessageFormat.format(
                                       "\\\\subsection*'{'{0}'}'\n"
                                       + "\\\\begin'{'grammar'}'"
                                       + "\n{1}\n"
                                       + "\\\\end'{'grammar'}'",
                                       CCommon.languagestring( this, "subsectiongrammar", i.getKey() ),
                                       StringUtils.join(
                                               i.getValue().entrySet().stream()
                                                .sorted( ( n, m ) -> n.getKey().compareToIgnoreCase( m.getKey() ) )
                                                .map( Map.Entry::getValue )
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
        m_rules.put(
                p_grammar.id(),
                p_element.id(),
                MessageFormat.format(
                        "<{0}> ::= {1}\n",
                        p_element.id(),
                        this.map( p_element )
                )
        );
        return p_element;
    }

    @Override
    protected final String cardinality( final IGrammarElement.ECardinality p_cardinality, final String p_element )
    {
        switch ( p_cardinality )
        {
            case OPTIONAL:
                return MessageFormat.format( "{0}{1}", p_element, "?" ).trim();

            case ZEROORMORE:
                return MessageFormat.format( "{0}{1}", p_element, "*" ).trim();

            case ONEORMORE:
                return MessageFormat.format( "{0}{1}", p_element, "+" ).trim();

            default:
                return p_element;
        }
    }

    @Override
    protected final String sequence( final IGrammarCollection p_element )
    {
        final String l_child = StringUtils.join( p_element.get().stream().map( this::map ).collect( Collectors.toList() ), " " ).trim();
        return p_element.get().size() == 1 ? l_child : MessageFormat.format( "[ {0} ]", l_child );
    }

    @Override
    protected final String choice( final IGrammarChoice p_element )
    {
        return StringUtils.join(
                p_element.get().stream()
                         .map( this::map )
                         .collect( Collectors.toList() ),
                " \\\\alt "
        );
    }

    @Override
    protected final String group( final IGrammarGroup p_element )
    {
        return MessageFormat.format( "[ {0} ]", this.map( p_element.element() ) );
    }

    @Override
    protected final String terminalvalue( final IGrammarSimpleElement<?> p_element )
    {
        return MessageFormat.format( "''{0}''", this.escapelatex( p_element.get().toString() ) );
    }

    @Override
    protected final String nonterminal( final IGrammarIdentifier p_element )
    {
        return MessageFormat.format( "<{0}>", p_element.get().toString() );
    }

    @Override
    protected final String negation( final IGrammarElement p_element )
    {
        return MessageFormat.format( "({0} {1})", CCommon.languagestring( this, "latexnegation" ), this.map( p_element ) ).trim();
    }

    /**
     * escape string with correct LaTeX definition
     *
     * @param p_string input string
     * @return escaped string
     */
    private String escapelatex( final String p_string )
    {
        return StringEscapeUtils.escapeJava(
                (
                        p_string.startsWith( "'" ) && p_string.endsWith( "'" )
                        ? p_string.substring( 1, p_string.length() - 1 )
                        : p_string
                )
                        .replace( "\\", "\\textbackslash " )

                        .replace( "&", "\\&" )

                        .replace( "$", "\\$" )

                        .replace( "<", "\\textless " )
                        .replace( ">", "\\textgreater " )

                        .replace( "{", "\\{" )
                        .replace( "}", "\\}" )

                        .replace( "#", "\\#" )

                        .replace( "~", "\\textasciitilde" )

                        .replace( "^", "\\^{}" )

                        .replace( "\"", "\\\"{}" )
        );
    }

}
