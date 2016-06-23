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
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


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
        this.copy( "lib/jquery-ui/jquery-ui.min.js", p_output );
        this.copy( "lib/railroad-diagrams/railroad-diagrams.css", p_output );
        this.copy( "lib/railroad-diagrams/railroad-diagrams.js", p_output );

        // replace content
        this.replace(
                new File( p_output.toString(), "/index.htm" ),

                // set HTML language code
                "%language%", Locale.getDefault().getLanguage(),

                // set HTML title
                "%title%", CCommon.languagestring( this, "htmltitle", m_grammar.id() ),

                // set grammar documentation
                "%grammardocumentation%", m_grammar.documentation(),

                // sets the showall text
                "%ruletoggle%", MessageFormat.format( "<h5 id = \"ruletoggle\" >{0}</h5>", CCommon.languagestring( this, "htmlruletoggle" ) ),

                // set menu with rule list
                "%rulelist%", StringUtils.join(
                        m_rules.rowMap().entrySet().stream().sorted( ( n, m ) -> n.getKey().compareToIgnoreCase( m.getKey() ) )
                               .map( i -> MessageFormat.format(
                                       "<div class=\"rulelist\" id=\"list_{0}\">\n"
                                       + "<h5 data-ruleset=\"rules_{0}\" class=\"grammarlisthead\">{0}</h5>\n"
                                       + "<ul>\n{1}</ul>\n"
                                       + "</div>",
                                       i.getKey(),
                                       StringUtils.join(
                                               i.getValue().keySet().stream()
                                                .sorted( String::compareToIgnoreCase )
                                                .map( j -> MessageFormat.format( "<li><a href=\"#{0}\">{1}</a></li>\n", this.linkhash( j ),
                                                                                 j.toLowerCase()
                                                ) )
                                                .collect( Collectors.toList() ),
                                               ""
                                       )
                                     )
                               )
                               .collect( Collectors.toList() ),
                        "\n\n"
                ),

                // set rules of diagrams
                "%rules%", StringUtils.join(
                        m_rules.rowMap().entrySet().stream().sorted( ( n, m ) -> n.getKey().compareToIgnoreCase( m.getKey() ) )
                               .map( i -> MessageFormat.format(
                                       "<div class=\"ruleelements\" id=\"rules_{0}\">"
                                       + "<h2>{0}</h2>"
                                       + "\n{1}\n"
                                       + "</div>",
                                       i.getKey(),
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
                        "<div class=\"ruledetail\">"
                        + "<a name=\"{0}\"></a>"
                        + "<h5>{1}</h5>"
                        + "<p>{2}</p>"
                        + "<p><script>"
                        + "var l_rrd = Diagram({3}).toSVG();"
                        + "l_rrd.id = \"svg_{0}\";"
                        + "var l_script = document.getElementsByTagName(\"script\");"
                        + "l_script[l_script.length - 1].parentNode.appendChild(l_rrd);"
                        + "</script></p>"
                        + "</div>",
                        this.linkhash( p_element.id() ),
                        p_element.id(),
                        p_element.documentation(),
                        this.map( p_element )
                )
        );

        return p_element;
    }


    @Override
    protected final String group( final IGrammarGroup p_element )
    {
        return MessageFormat.format( "({0})", this.map( p_element.element() ) );
    }

    @Override
    protected final String choice( final IGrammarChoice p_element )
    {
        final String l_child = StringUtils.join(
                IntStream
                        .range( 0, p_element.get().size() )
                        .boxed()
                        .map( i -> this.map( p_element.get().get( i ) ) )
                        .filter( i -> i != null )
                        .collect( Collectors.toList() ),
                ", "
        );

        return p_element.get().size() == 1 ? l_child : MessageFormat.format( "Choice({0}, {1})", 0, l_child );
    }

    @Override
    protected final String sequence( final IGrammarCollection p_element )
    {
        final String l_child = StringUtils.join(
                p_element.get().stream()
                         .map( this::map )
                         .filter( i -> i != null )
                         .collect( Collectors.toList() ),
                ", "
        );

        return p_element.get().size() == 1 ? l_child : MessageFormat.format( "Sequence({0})", l_child );
    }

    @Override
    protected final String terminalvalue( final IGrammarSimpleElement<?> p_element )
    {
        return MessageFormat.format(
                "Terminal(''{0}'', ''#{1}'')",
                StringEscapeUtils.escapeEcmaScript( p_element.get().toString() ),
                this.linkhash( p_element.get().toString() )
        );
    }

    @Override
    protected final String nonterminal( final IGrammarIdentifier p_element )
    {
        return MessageFormat.format(
                "NonTerminal(''{0}'', ''#{1}'')",
                p_element.get(),
                this.linkhash( p_element.get() )
        );
    }

    @Override
    protected final String negation( final IGrammarElement p_element )
    {
        return MessageFormat.format(
                "Sequence( Comment(''{0}''), {1} )",
                StringEscapeUtils.escapeEcmaScript( CCommon.languagestring( this, "htmlnegation" ) ),
                this.map( p_element )
        );
    }

    @Override
    protected final String cardinality( final IGrammarElement.ECardinality p_cardinality, final String p_element )
    {
        switch ( p_cardinality )
        {
            case OPTIONAL:
                return MessageFormat.format( "Optional({0})", p_element );

            case ZEROORMORE:
                return MessageFormat.format( "ZeroOrMore({0})", p_element );

            case ONEORMORE:
                return MessageFormat.format( "OneOrMore({0})", p_element );

            default:
                return p_element;
        }
    }

    /**
     * create a hash of a link
     *
     * @param p_value ID element
     * @return hash
     */
    private String linkhash( final String p_value )
    {
        try
        {
            return new BigInteger( 1, MessageDigest.getInstance( "MD5" ).digest( p_value.getBytes( "UTF-8" ) ) ).toString( 16 );
        }
        catch ( final UnsupportedEncodingException | NoSuchAlgorithmException l_exception )
        {
            return "";
        }
    }

}
