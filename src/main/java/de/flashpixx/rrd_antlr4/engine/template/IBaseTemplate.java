/*
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the RRD-AntLR4                                                #
 * # Copyright (c) 2016-17, Philipp Kraus (philipp.kraus@flashpixx.de)                  #
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

import com.aol.cyclops.sequence.SequenceM;
import de.flashpixx.rrd_antlr4.CCommon;
import de.flashpixx.rrd_antlr4.CStringReplace;
import de.flashpixx.rrd_antlr4.antlr.IGrammarChoice;
import de.flashpixx.rrd_antlr4.antlr.IGrammarCollection;
import de.flashpixx.rrd_antlr4.antlr.IGrammarElement;
import de.flashpixx.rrd_antlr4.antlr.IGrammarGroup;
import de.flashpixx.rrd_antlr4.antlr.IGrammarIdentifier;
import de.flashpixx.rrd_antlr4.antlr.IGrammarNegation;
import de.flashpixx.rrd_antlr4.antlr.IGrammarRule;
import de.flashpixx.rrd_antlr4.antlr.IGrammarSequence;
import de.flashpixx.rrd_antlr4.antlr.IGrammarSimpleElement;
import de.flashpixx.rrd_antlr4.antlr.IGrammarTerminal;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;


/**
 * base implementation
 */
public abstract class IBaseTemplate implements ITemplate
{

    /**
     * template name
     */
    private final String m_name;
    /**
     * index file name
     */
    private final String m_index;

    /**
     * ctor
     *
     * @param p_name template name
     * @param p_index index file
     */
    public IBaseTemplate( final String p_name, final String p_index )
    {
        m_name = p_name.trim().toLowerCase();
        m_index = p_index;
    }

    @Override
    public final String index()
    {
        return m_index;
    }

    @Override
    public final String name()
    {
        return m_name;
    }

    /**
     * copies files from the directory of the template to the output directory
     *
     * @param p_templatefile file within the template directory
     * @param p_output output directory
     * @throws IOException on IO error
     * @throws URISyntaxException on URL syntax error
     */
    protected final void copy( final String p_templatefile, final Path p_output ) throws IOException, URISyntaxException
    {
        final Path l_target = Paths.get( p_output.toString(), p_templatefile );
        Files.createDirectories( l_target.getParent() );
        Files.copy(
                CCommon.resourceurl( MessageFormat.format( "{0}{1}{2}{3}", "de/flashpixx/rrd_antlr4/template/", m_name, "/", p_templatefile ) ).openStream(),
                l_target,
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    /**
     * replaces the string within the file
     *
     * @param p_file file
     * @param p_replacepair string tupels for replacing
     */
    protected final void replace( final File p_file, final String... p_replacepair ) throws IOException
    {
        if ( ( p_replacepair == null ) || ( p_replacepair.length % 2 != 0 ) )
            throw new IllegalArgumentException( CCommon.languagestring( IBaseTemplate.class, "replaceerror", p_file ) );

        final CStringReplace l_content = new CStringReplace( FileUtils.readFileToString( p_file, Charset.forName( "UTF-8" ) ) );
        SequenceM.rangeLong( 0, p_replacepair.length )
                 .sliding( 2, 2 )
                 .forEach( i -> l_content.replaceAll( p_replacepair[i.get( 0 ).intValue()], p_replacepair[i.get( 1 ).intValue()] ) );
        FileUtils.write( p_file, l_content.get(), Charset.forName( "UTF-8" ) );
    }

    /**
     * calls the sub routines to format the lement
     *
     * @param p_element grammat element or string
     * @return string representation
     */
    @SuppressWarnings( "unchecked" )
    protected String map( final IGrammarElement p_element )
    {
        if ( p_element instanceof IGrammarIdentifier )
            return this.cardinality( p_element.cardinality(), this.nonterminal( (IGrammarIdentifier) p_element ) );

        if ( p_element instanceof IGrammarRule )
            return this.cardinality( p_element.cardinality(), this.rule( (IGrammarRule) p_element ) );

        if ( p_element instanceof IGrammarSimpleElement<?> )
            return this.cardinality( p_element.cardinality(), this.terminalvalue( (IGrammarSimpleElement<?>) p_element ) );

        if ( p_element instanceof IGrammarTerminal )
            return this.cardinality( p_element.cardinality(), this.terminal( (IGrammarTerminal) p_element ) );


        if ( p_element instanceof IGrammarNegation )
            return this.cardinality( p_element.cardinality(), this.negation( ( (IGrammarNegation) p_element ).inner() ) );

        if ( p_element instanceof IGrammarGroup )
            return this.cardinality( p_element.cardinality(), this.group( (IGrammarGroup) p_element ) );

        if ( p_element instanceof IGrammarChoice )
            return this.cardinality( p_element.cardinality(), this.choice( (IGrammarChoice) p_element ) );

        if ( p_element instanceof IGrammarSequence )
            return this.cardinality( p_element.cardinality(), this.sequence( (IGrammarSequence) p_element ) );

        return "";
    }

    /**
     * removing quotes if needed
     *
     * @param p_string input string
     * @return unquoted string
     */
    protected static String removequotes( final String p_string )
    {
        return ( p_string.startsWith( "'" ) ) && ( p_string.endsWith( "'" ) ) && ( p_string.length() >= 2 )
               ? p_string.substring( 1, p_string.length() - 1 )
               : p_string;
    }

    /**
     * create a hash of a link
     *
     * @param p_value ID element
     * @return hash
     */
    protected static String linkhash( final String p_value )
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

    /**
     * creates a rule
     *
     * @param p_element rule element
     * @return string represenation
     */
    protected String rule( final IGrammarRule p_element )
    {
        return this.map( p_element.children() );
    }

    /**
     * sets the cardinality
     *
     * @param p_cardinality cardinality value
     * @param p_element inner string
     * @return string represenation
     */
    protected abstract String cardinality( final IGrammarElement.ECardinality p_cardinality, final String p_element );

    /**
     * creates a grammar sequence
     *
     * @param p_element element list
     * @return string representation
     */
    protected abstract String sequence( final IGrammarCollection p_element );

    /**
     * creates a grammar choice
     *
     * @param p_element element list
     * @return string representation
     */
    protected abstract String choice( final IGrammarChoice p_element );

    /**
     * crates a grammer group
     *
     * @param p_element group element
     * @return string representation
     */
    protected abstract String group( final IGrammarGroup p_element );

    /**
     * creates a terminal
     *
     * @param p_element terminal value element
     * @return string represenation
     */
    protected abstract String terminalvalue( final IGrammarSimpleElement<?> p_element );

    /**
     * creates a terminal
     *
     * @param p_element terminal element
     * @return string represenation
     */
    protected String terminal( final IGrammarTerminal p_element )
    {
        return this.map( p_element.children() );
    }

    /**
     * creates a non-terminal
     *
     * @param p_element identifier element
     * @return string represenation
     */
    protected abstract String nonterminal( final IGrammarIdentifier p_element );

    /**
     * creates a negation structure
     *
     * @param p_element inner negation element
     * @return string represenation
     */
    protected abstract String negation( final IGrammarElement p_element );

}
