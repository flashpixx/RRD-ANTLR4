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

package de.flashpixx.rrd_antlr4.antlr;

import de.flashpixx.rrd_antlr4.CStringReplace;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * grammar functions
 */
public final class CCommon
{

    /**
     * ctor
     */
    private CCommon()
    {
    }

    /**
     * clean string value
     *
     * @param p_string string data
     * @return cleaned string
     */
    public static String cleanString( final String p_string )
    {
        if ( ( p_string.length() > 1 ) && ( p_string.startsWith( "'" ) ) && ( p_string.endsWith( "'" ) ) )
            return p_string.substring( 1, p_string.length() - 1 );

        return p_string;
    }

    /**
     * creates a choice
     *
     * @param p_elements grammar elements
     * @return grammar element
     */
    public static IGrammarElement choice( final List<IGrammarElement> p_elements )
    {
        return p_elements.size() == 1
               ? p_elements.get( 0 )
               : new CGrammarChoice( p_elements );
    }

    /**
     * creates a sequence
     *
     * @param p_elements grammar elements
     * @return grammar element
     */
    public static IGrammarElement sequence( final List<IGrammarElement> p_elements )
    {
        return p_elements.size() == 1
               ? p_elements.get( 0 )
               : new CGrammarSequence( p_elements );
    }

    /**
     * defines the cardinality of an grammar element
     *
     * @param p_cardinality cardinality string
     * @param p_element element
     * @return modified element
     */
    public static IGrammarElement cardinality( final String p_cardinality, final IGrammarElement p_element )
    {
        if ( p_cardinality.startsWith( "+" ) )
            return p_element.cardinality( IGrammarElement.ECardinality.ONEORMORE );

        if ( p_cardinality.startsWith( "*" ) )
            return p_element.cardinality( IGrammarElement.ECardinality.ZEROORMORE );

        if ( p_cardinality.startsWith( "?" ) )
            return p_element.cardinality( IGrammarElement.ECardinality.OPTIONAL );

        return p_element;
    }

    /**
     * generates a terminal element with data
     *
     * @param p_value data string
     * @return grammar terminal element
     */
    @SuppressWarnings( "unchecked" )
    public static IGrammarElement terminalvalue( final String p_value )
    {
        // a terminal-string starts and ends always with ', so we do
        // not need to parse th string if this condition fits
        if ( ( p_value.startsWith( "'" ) ) && ( p_value.endsWith( "'" ) ) )
            return new CGrammarTerminalValue<>( p_value );

        // try to compile string as regular expression pattern
        try
        {
            return (IGrammarElement) new CASTVisitorPCRE().visit(
                    new PCREParser(
                            new CommonTokenStream(
                                    new PCRELexer(
                                            new ANTLRInputStream( new ByteArrayInputStream( Pattern.compile( p_value ).pattern().getBytes() ) )
                                    )
                            )
                    ).parse()
            );
        }
        catch ( final PatternSyntaxException | IOException l_exception )
        {
        }

        // if any fails, return it directly
        return new CGrammarTerminalValue<>( p_value );
    }

    /**
     * cleanup comment from doxygen structure
     *
     * @param p_comment comment input
     * @param p_docuclean set with regular expression for cleaning
     * @return cleaned text or null
     */
    public static String cleanComment( final String p_comment, final Set<String> p_docuclean )
    {
        if ( p_comment == null )
            return null;

        // remove CR, LF and tab
        final CStringReplace l_documentation = new CStringReplace( p_comment ).replaceAll( "(\\t|\\n)+", " " ).replace( "\r", "" );
        p_docuclean.stream().forEach( i -> l_documentation.replaceAll( i, "" ) );

        return l_documentation.replaceAll( "\\*", "" ).replaceAll( "\\/", "" ).get().trim();
    }

}
