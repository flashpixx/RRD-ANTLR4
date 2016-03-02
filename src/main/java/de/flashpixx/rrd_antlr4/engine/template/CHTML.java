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
import de.flashpixx.rrd_antlr4.antlr.IGrammarComplexElement;
import de.flashpixx.rrd_antlr4.antlr.IGrammarRule;
import de.flashpixx.rrd_antlr4.antlr.IGrammarSimpleElement;
import de.flashpixx.rrd_antlr4.antlr.IGrammarTerminal;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * template for HTML export
 */
public final class CHTML extends IBaseTemplate
{
    /**
     * main grammar
     */
    private IGrammarComplexElement m_grammar;
    /**
     * rules table
     */
    private final Table<String, String, String> m_rules = HashBasedTable.create();

    /**
     * ctor
     *
     * @param p_name template name
     */
    public CHTML( final String p_name )
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
        // copy JavaScript and CSS elements
        this.copy( "index.htm", p_output );
        this.copy( "layout.css", p_output );
        this.copy( "action.js", p_output );
        this.copy( "lib/jquery/dist/jquery.min.js", p_output );
        this.copy( "lib/railroad-diagrams/railroad-diagrams.css", p_output );
        this.copy( "lib/railroad-diagrams/railroad-diagrams.js", p_output );

        // replace content
        this.replace(
                new File( p_output.toString(), "/index.htm" ),

                // set HTML language code
                "%language%", Locale.getDefault().getLanguage(),

                // set HTML title
                "%title%", CCommon.getLanguageString( this, "htmltitle", m_grammar.id() ),

                // set grammar documentation
                "%grammardocumentation%", m_grammar.documentation(),

                // sets the showall text
                "%ruletoggle%", MessageFormat.format( "<h5 id = \"ruletoggle\" >{0}</h5>", CCommon.getLanguageString( this, "htmlruletoggle" ) ),

                // set menu with rule list
                "%rulelist%", StringUtils.join(
                        m_rules.rowMap().entrySet().stream().sorted( ( n, m ) -> n.getKey().compareToIgnoreCase( m.getKey() ) )
                               .map( i -> MessageFormat.format(
                                       "<div class=\"rulelist\" id=\"list_{0}\">" +
                                       "<h5 data-ruleset=\"rules_{0}\" class=\"grammarlisthead\">{0}</h5>" +
                                       "<ul>{1}</ul>" +
                                       "</div>",
                                       i.getKey(),
                                       StringUtils.join(
                                               i.getValue().keySet().stream()
                                                .sorted( ( n, m ) -> n.compareToIgnoreCase( m ) )
                                                .map( j -> MessageFormat.format( "<li>{0}</li>", j.toLowerCase() ) )
                                                .collect( Collectors.toList() ),
                                               ""
                                       )
                                     )
                               )
                               .collect( Collectors.toList() ),
                        ""
                ),

                // set rules of diagrams
                "%rules%", StringUtils.join(
                        m_rules.rowMap().entrySet().stream().sorted( ( n, m ) -> n.getKey().compareToIgnoreCase( m.getKey() ) )
                               .map( i -> MessageFormat.format(
                                       "<div class=\"elements\" id=\"rules_{0}\">" +
                                       "<h2>{0}</h2>" +
                                       "<span><script>\n{1}\n</script></span>" +
                                       "</div>",
                                       i.getKey(),
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
                        ""
                )
        );
    }

    @Override
    public final void grammar( final IGrammarComplexElement p_grammar )
    {
        // set only if is not net
        if ( m_grammar == null )
            m_grammar = p_grammar;
    }

    @Override
    public final void rule( final IGrammarComplexElement p_grammar, final IGrammarRule p_rule )
    {
        m_rules.put(
                p_grammar.id(),
                p_rule.id(),
                MessageFormat.format(
                        "Diagram({0}).addTo();",
                        StringUtils.join(
                                p_rule.alternatives().stream()
                                      .map( i -> this.choice( i ) )
                                      .collect( Collectors.toList() ),
                                ", "
                        )
                )
        );
    }

    @Override
    public final void terminal( final IGrammarComplexElement p_grammar, final IGrammarTerminal p_terminal )
    {
        m_rules.put(
                p_grammar.id(),
                p_terminal.id(),
                MessageFormat.format(
                        "Diagram({0}).addTo();",
                        StringUtils.join(
                                p_terminal.alternatives().stream()
                                          .map( i -> this.choice( i ) )
                                          .collect( Collectors.toList() ),
                                ", "
                        )
                )
        );
    }

    /**
     * creates a grammar choice
     *
     * @param p_input element list
     * @return string representation
     */
    private String choice( final List<?> p_input )
    {
        return MessageFormat.format(
                "Choice({0}, {1})",
                0,
                StringUtils.join(
                        p_input.stream()
                               .map( i -> element( i ) )
                               .filter( i -> i != null )
                               .collect( Collectors.toList() ),
                        ", "
                )
        );
    }

    /**
     * creates a grammar sequence
     *
     * @param p_input element list
     * @return string representation
     */
    private String sequence( final List<?> p_input )
    {
        return MessageFormat.format(
                "Sequence({0})",
                StringUtils.join(
                        p_input.stream()
                               .map( i -> this.element( i ) )
                               .filter( i -> i != null )
                               .collect( Collectors.toList() ),
                        ", "
                )
        );
    }

    /**
     * create an element string
     *
     * @param p_element grammat element or string
     * @return string representation
     *
     * @tparam T object type
     */
    @SuppressWarnings( "unchecked" )
    private <T> String element( final T p_element )
    {
        if ( p_element instanceof String )
            return (String) p_element;

        if ( p_element instanceof IGrammarRule )
            return this.choice( ( (IGrammarRule) p_element ).alternatives() );

        if ( ( p_element instanceof IGrammarSimpleElement<?> ) && ( ( (IGrammarSimpleElement<?>) p_element ).isValueAssignableTo( Pattern.class ) ) )
            return "'" + StringEscapeUtils.escapeEcmaScript(
                    StringEscapeUtils.escapeEcmaScript( ( (IGrammarSimpleElement<?>) p_element ).<Pattern>get().pattern() ) ) + "'";

        if ( ( p_element instanceof IGrammarSimpleElement<?> ) && ( ( (IGrammarSimpleElement<?>) p_element ).isValueAssignableTo( String.class ) ) )
            return "'" + StringEscapeUtils.escapeEcmaScript( ( (IGrammarSimpleElement<?>) p_element ).<String>get() ) + "'";

        return StringEscapeUtils.escapeEcmaScript( "foo" );
    }
}
