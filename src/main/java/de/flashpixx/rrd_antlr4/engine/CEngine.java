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

package de.flashpixx.rrd_antlr4.engine;

import de.flashpixx.rrd_antlr4.antlr.ANTLRv4Lexer;
import de.flashpixx.rrd_antlr4.antlr.ANTLRv4Parser;
import de.flashpixx.rrd_antlr4.antlr.CASTVisitorAntLR;
import de.flashpixx.rrd_antlr4.engine.template.ITemplate;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * template engine
 */
public final class CEngine
{

    /**
     * generator call
     *
     * @param p_baseoutputdirectory base output directory
     * @param p_outputdirectory output directory - relative to base output directory
     * @param p_grammar grammar input file
     * @param p_docuclean set with documentation clean regex
     * @param p_imports map with grammar imported grammar files
     * @param p_templates exporting templates  @return list with error messages
     * @return list with error messages
     * @throws IOException on IO error
     */
    public Collection<String> generate( final File p_baseoutputdirectory, final File p_outputdirectory, final File p_grammar, final Set<String> p_docuclean,
                                        final Map<String, File> p_imports, final Set<ITemplate> p_templates
    ) throws IOException
    {
        return Collections.unmodifiableList( p_templates
                .parallelStream()

                // create output directory if not exists
                .flatMap( i ->
                {
                    try
                    {
                        final Path l_directory = Files.createDirectories( Paths.get( p_baseoutputdirectory.toString(), i.name(), p_outputdirectory.toString() ) );

                        // run exporting process
                        i.preprocess( l_directory );
                        final Collection<String> l_errors = this.parse( p_grammar, p_docuclean, p_imports, i );
                        if ( !l_errors.isEmpty() )
                            return l_errors.stream();

                        i.postprocess( l_directory );
                        return Stream.<String>of();
                    }
                    catch ( final URISyntaxException | IOException l_exception )
                    {
                        return Stream.of( l_exception.getMessage() );
                    }
                } )

                // collect error messages
                .collect( Collectors.toList() )
        );
    }


    /**
     * runs parsing process with recursive descent of a grammar file
     *
     * @param p_grammar grammar file
     * @param p_docuclean set with documentation clean regex
     * @param p_imports map with grammar imported grammar files
     * @param p_template template which will be passend
     * @return colleciton with error messages
     *
     * @throws IOException thrown on IO errors
     */
    private Collection<String> parse( final File p_grammar, final Set<String> p_docuclean, final Map<String, File> p_imports, final ITemplate p_template )
    throws IOException
    {
        // lexing and parsing the input grammar file
        final CASTVisitorAntLR l_visitor = new CASTVisitorAntLR( p_template, p_docuclean );
        l_visitor.visit(
                new ANTLRv4Parser(
                        new CommonTokenStream(
                                new ANTLRv4Lexer(
                                        new ANTLRInputStream( new FileInputStream( p_grammar ) )
                                )
                        )
                ).grammarSpec()
        );

        return l_visitor.getGrammarImports().stream()
                        .map( i -> p_imports.get( i.get() ) )
                        .filter( i -> i != null )
                        .flatMap( i ->
                        {
                            try
                            {
                                return this.parse( i, p_docuclean, p_imports, p_template ).stream();
                            }
                            catch ( final IOException l_exception )
                            {
                                return Stream.of( l_exception.getMessage() );
                            }
                        } ).collect( Collectors.toList() );
    }

}
