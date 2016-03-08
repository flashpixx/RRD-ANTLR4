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

import com.aol.cyclops.sequence.SequenceM;
import de.flashpixx.rrd_antlr4.CCommon;
import de.flashpixx.rrd_antlr4.CStringReplace;
import de.flashpixx.rrd_antlr4.antlr.IGrammarChoice;
import de.flashpixx.rrd_antlr4.antlr.IGrammarCollection;
import de.flashpixx.rrd_antlr4.antlr.IGrammarElement;
import de.flashpixx.rrd_antlr4.antlr.IGrammarGroup;
import de.flashpixx.rrd_antlr4.antlr.IGrammarIdentifier;
import de.flashpixx.rrd_antlr4.antlr.IGrammarRule;
import de.flashpixx.rrd_antlr4.antlr.IGrammarSequence;
import de.flashpixx.rrd_antlr4.antlr.IGrammarSimpleElement;
import de.flashpixx.rrd_antlr4.antlr.IGrammarTerminal;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
     * ctor
     *
     * @param p_name template name
     */
    public IBaseTemplate( final String p_name )
    {
        m_name = p_name.trim().toLowerCase();
    }

    @Override
    public final String name()
    {
        return m_name;
    }

    /**
     * copies files from the template directory of the template
     * to the output directory
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
                CCommon.getResourceURL( MessageFormat.format( "{0}{1}{2}{3}", "template/", m_name, "/", p_templatefile ) ).openStream(),
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
            throw new IllegalArgumentException( CCommon.getLanguageString( IBaseTemplate.class, "replaceerror" ) );

        final CStringReplace l_content = new CStringReplace( FileUtils.readFileToString( p_file ) );
        SequenceM.rangeLong( 0, p_replacepair.length )
                 .sliding( 2, 2 )
                 .forEach( i -> l_content.replaceAll( p_replacepair[i.get( 0 ).intValue()], p_replacepair[i.get( 1 ).intValue()] ) );
        FileUtils.writeStringToFile( p_file, l_content.get() );
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
            return this.nonterminal( (IGrammarIdentifier) p_element );

        if ( p_element instanceof IGrammarRule )
            return this.rule( (IGrammarRule) p_element );

        if ( p_element instanceof IGrammarSimpleElement<?> )
            return this.terminalvalue( (IGrammarSimpleElement<?>) p_element );

        if ( p_element instanceof IGrammarTerminal )
            return this.cardinality( p_element.cardinality(), this.terminal( ( (IGrammarTerminal) p_element ) ) );


        if ( p_element instanceof IGrammarGroup )
            return this.cardinality( p_element.cardinality(), this.group( (IGrammarGroup) p_element ) );

        if ( p_element instanceof IGrammarChoice )
            return this.cardinality( p_element.cardinality(), this.choice( (IGrammarChoice) p_element ) );

        if ( p_element instanceof IGrammarSequence )
            return this.cardinality( p_element.cardinality(), this.sequence( (IGrammarSequence) p_element ) );

        return "";
    }

    /**
     * creates a rule
     *
     * @param p_rule rule element
     * @return string represenation
     */
    protected String rule( final IGrammarRule p_rule )
    {
        return this.map( p_rule.children() );
    }

    /**
     * sets the cardinality
     *
     * @param p_cardinality cardinality value
     * @param p_inner inner string
     * @return string represenation
     */
    protected abstract String cardinality( final IGrammarElement.ECardinality p_cardinality, final String p_inner );

    /**
     * creates a grammar sequence
     *
     * @param p_input element list
     * @return string representation
     */
    protected abstract String sequence( final IGrammarCollection p_input );

    /**
     * creates a grammar choice
     *
     * @param p_input element list
     * @return string representation
     */
    protected abstract String choice( final IGrammarChoice p_input );

    /**
     * crates a grammer group
     *
     * @param p_group group element
     * @return string representation
     */
    protected abstract String group( final IGrammarGroup p_group );

    /**
     * creates a terminal
     *
     * @param p_value terminal value element
     * @return string represenation
     */
    protected abstract String terminalvalue( final IGrammarSimpleElement<?> p_value );

    /**
     * creates a terminal
     *
     * @param p_terminal terminal element
     * @return string represenation
     */
    protected String terminal( final IGrammarTerminal p_terminal )
    {
        return this.map( p_terminal.children() );
    }

    /**
     * creates an non-terminal
     *
     * @param p_element identifier element
     * @return string represenation
     */
    protected abstract String nonterminal( final IGrammarIdentifier p_element );

}
