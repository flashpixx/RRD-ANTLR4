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


package de.flashpixx.rrd_antlr4;

import de.flashpixx.rrd_antlr4.engine.CEngine;
import de.flashpixx.rrd_antlr4.engine.template.ETemplate;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;


/**
 * standalone program and Maven plugin
 *
 * @see https://maven.apache.org/guides/plugin/guide-java-plugin-development.html
 */
public final class CMain extends AbstractMojo
{
    /**
     * engine instance
     */
    private static final CEngine ENGINE = new CEngine();
    /**
     * default output directory
     */
    private static final String DEFAULTOUTPUT = "rrd-output";
    /**
     * default export format
     */
    private static final String DEFAULTTEMPLATE = "HTML";

    /**
     * Maven plugin parameter for output
     */
    @Parameter( defaultValue = DEFAULTOUTPUT )
    private String output;
    /**
     * Maven plugin used templates option
     */
    @Parameter( defaultValue = DEFAULTTEMPLATE )
    private String[] template;


    /**
     * main
     *
     * @param p_args command-line arguments
     */
    public static void main( final String[] p_args )
    {
        // --- define CLI options --------------------------------------------------------------------------------------
        final Options l_clioptions = new Options();
        l_clioptions.addOption( "help", false, CCommon.getLanguageString( CMain.class, "help" ) );
        l_clioptions.addOption( "grammar", true, CCommon.getLanguageString( CMain.class, "grammar" ) );
        l_clioptions.addOption( "template", true, CCommon.getLanguageString( CMain.class, "template", Arrays.asList( ETemplate.values() ), DEFAULTTEMPLATE ) );
        l_clioptions.addOption( "output", true, CCommon.getLanguageString( CMain.class, "output", DEFAULTOUTPUT ) );

        CommandLine l_cli = null;
        try
        {
            l_cli = new DefaultParser().parse( l_clioptions, p_args );
        }
        catch ( final Exception l_exception )
        {
            System.err.println( CCommon.getLanguageString( CMain.class, "parseerror", l_exception.getLocalizedMessage() ) );
            System.exit( -1 );
        }


        // --- process CLI arguments and push configuration ------------------------------------------------------------
        if ( l_cli.hasOption( "help" ) )
        {
            final HelpFormatter l_formatter = new HelpFormatter();
            l_formatter.printHelp(
                    ( new java.io.File( CMain.class.getProtectionDomain().getCodeSource().getLocation().getPath() ).getName() ), l_clioptions );
            System.exit( 0 );
        }


        if ( !l_cli.hasOption( "grammar" ) )
        {
            System.err.println( CCommon.getLanguageString( CMain.class, "grammarnotset" ) );
            System.exit( -1 );
        }

        final String[] l_templates = l_cli.hasOption( "template" ) ? l_cli.getOptionValue( "template" ).split( "," ) : new String[]{DEFAULTTEMPLATE};
        final Path l_outputdirectory = l_cli.hasOption( "output" ) ? Paths.get( l_cli.getOptionValue( "output" ) ) : null;
        final Collection<String> l_errors = Arrays.stream( l_cli.getOptionValue( "grammar" ).split( "," ) )
                                                  .parallel()
                                                  .flatMap( i -> generate( new File( i ), l_outputdirectory, l_templates ).stream() )
                                                  .collect( Collectors.toList() );

        if ( !l_errors.isEmpty() )
        {
            l_errors.stream().forEach( System.err::println );
            System.exit( -1 );
        }
    }

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException
    {

    }


    /**
     * generating export
     *
     * @param p_grammar path to grammar file
     * @param p_outputdirectory output directory
     * @param p_template string with export name
     * @return returns a collection with error messages
     */
    private static Collection<String> generate( final File p_grammar, final Path p_outputdirectory, final String... p_template )
    {
        return Arrays.stream( p_template )
                     .parallel()
                     .map( i -> {
                               try
                               {
                                   ENGINE.generate(
                                           p_grammar,
                                           ETemplate.valueOf( i.trim().toUpperCase() ).generate(),
                                           p_outputdirectory != null ? p_outputdirectory
                                                                     : Paths.get( DEFAULTOUTPUT, i.trim().toLowerCase(), p_grammar.getName().toLowerCase() )
                                   );
                                   return null;
                               }
                               catch ( final IOException p_exception )
                               {
                                   return p_exception.getMessage();
                               }
                           }
                     )
                     .filter( i -> i != null )
                     .collect( Collectors.toSet() );
    }
}
