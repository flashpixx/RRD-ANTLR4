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
import java.nio.file.Files;
import java.nio.file.Path;


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
     * generator call
     *
     * @param p_grammar grammar input file
     * @param p_template exporting template
     * @param p_outputdirectory output direcotry
     * @throws IOException
     */
    public void generate( final File p_grammar, final ITemplate p_template, final Path p_outputdirectory ) throws IOException
    {
        final ANTLRv4Lexer l_lexer = new ANTLRv4Lexer( new ANTLRInputStream( new FileInputStream( p_grammar ) ) );
        l_lexer.removeErrorListeners();
        l_lexer.addErrorListener( m_errorlistener );

        final ANTLRv4Parser l_parser = new ANTLRv4Parser( new CommonTokenStream( l_lexer ) );
        l_parser.removeErrorListeners();
        l_parser.addErrorListener( m_errorlistener );


        // create output directory of not exists
        Files.createDirectories( p_outputdirectory );

        // run exporting process with the visitor
        p_template.preprocess( p_outputdirectory, p_grammar.getName() );
        new CASTVisitor( p_grammar.getName(), p_template ).visit( l_parser.grammarSpec() );
        p_template.postprocess( p_outputdirectory, p_grammar.getName() );
    }

}
