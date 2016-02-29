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
import de.flashpixx.rrd_antlr4.antlr.CASTVisitor;
import de.flashpixx.rrd_antlr4.engine.template.ITemplate;
import org.antlr.v4.runtime.ANTLRErrorListener;
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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * template engine
 */
public final class CEngine
{
    /**
     * error handler
     */
    private final ANTLRErrorListener m_errorlistener = new CErrorListener();
    /**
     * directories with grammar imports
     */
    private final Set<Files> m_imports = new HashSet<>();


    /**
     * generator call
     *
     * @param p_outputdirectory output directory - the template name and grammar file name will be appended
     * @param p_template exporting templates
     * @param p_grammar grammar input file
     * @return list with error messages
     *
     * @throws IOException on IO error
     */
    public Collection<String> generate( final String p_outputdirectory, final Set<ITemplate> p_template, final File p_grammar ) throws IOException
    {
        // lexing and parsing the input grammar file
        final ANTLRv4Lexer l_lexer = new ANTLRv4Lexer( new ANTLRInputStream( new FileInputStream( p_grammar ) ) );
        l_lexer.removeErrorListeners();
        l_lexer.addErrorListener( m_errorlistener );

        final ANTLRv4Parser l_parser = new ANTLRv4Parser( new CommonTokenStream( l_lexer ) );
        l_parser.removeErrorListeners();
        l_parser.addErrorListener( m_errorlistener );


        // run for each template the exporting process
        return p_template
                .parallelStream()

                // create output directory if not exists
                .map( i -> {
                    try
                    {
                        final Path l_directory = Files.createDirectories( Paths.get( p_outputdirectory, i.name(), p_grammar.getName().toLowerCase() ) );

                        // run exporting process of the input grammar file with the visitor
                        i.preprocess( l_directory );

                        final CASTVisitor l_visitor = new CASTVisitor( i );
                        l_visitor.visit( l_parser.grammarSpec() );

                        // do recursive call to handle imported grammar files


                        i.postprocess( l_directory );
                        return null;
                    }
                    catch ( final URISyntaxException | IOException p_exception )
                    {
                        return p_exception.getMessage();
                    }
                } )

                // collect error messages
                .collect( Collectors.toList() );
    }

}
