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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * standalone program and Maven Report plugin
 */
@Mojo( name = "rrd-antlr4" )
public final class CMain extends AbstractMavenReport
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
     * default grammar file extension
     */
    private static final String GRAMMARFILEEXTENSION = ".g4";



    /**
     * Maven plugin parameter for output
     */
    @Parameter( defaultValue = "${project.reporting.outputDirectory}/" + DEFAULTOUTPUT )
    private String output;
    /**
     * Maven plugin used templates option
     */
    @Parameter( defaultValue = DEFAULTTEMPLATE )
    private String[] templates;
    /**
     * Maven plugin default directories of grammars
     */
    @Parameter( defaultValue = "${project.basedir}/src/main/antlr4" )
    private String[] grammar;
    /**
     * Maven plugin default grammar import directories
     */
    @Parameter( defaultValue = "${project.basedir}/src/main/antlr4/imports" )
    private String[] imports;
    /**
     * Maven plugin exclude file list
     */
    @Parameter
    private String[] excludes;
    /**
     * Maven plugin documentation cleanup regex
     */
    @Parameter
    private String[] docclean;


    // --- standalone execution --------------------------------------------------------------------------------------------------------------------------------

    /**
     * main
     *
     * @param p_args command-line arguments
     * @throws IOException on any io error
     */
    public static void main( final String[] p_args ) throws IOException
    {
        // --- define CLI options --------------------------------------------------------------------------------------
        final Options l_clioptions = new Options();
        l_clioptions.addOption( "help", false, CCommon.languagestring( CMain.class, "help" ) );
        l_clioptions.addOption( "output", true, CCommon.languagestring( CMain.class, "output", DEFAULTOUTPUT ) );
        l_clioptions.addOption( "imports", true, CCommon.languagestring( CMain.class, "import" ) );
        l_clioptions.addOption( "excludes", true, CCommon.languagestring( CMain.class, "exclude" ) );
        l_clioptions.addOption( "grammar", true, CCommon.languagestring( CMain.class, "grammar" ) );
        l_clioptions.addOption( "language", true, CCommon.languagestring( CMain.class, "language" ) );
        l_clioptions.addOption( "docclean", true, CCommon.languagestring( CMain.class, "documentationclean" ) );
        l_clioptions.addOption( "templates", true, CCommon.languagestring( CMain.class, "template", Arrays.asList( ETemplate.values() ), DEFAULTTEMPLATE ) );


        final CommandLine l_cli;
        try
        {
            l_cli = new DefaultParser().parse( l_clioptions, p_args );
        }
        catch ( final Exception l_exception )
        {
            System.err.println( CCommon.languagestring( CMain.class, "parseerror", l_exception.getLocalizedMessage() ) );
            System.exit( -1 );
            return;
        }


        // --- process CLI arguments and push configuration ------------------------------------------------------------
        if ( l_cli.hasOption( "help" ) )
        {
            final HelpFormatter l_formatter = new HelpFormatter();
            l_formatter.printHelp( new java.io.File( CMain.class.getProtectionDomain().getCodeSource().getLocation().getPath() ).getName(), l_clioptions );
            System.exit( 0 );
        }


        if ( !l_cli.hasOption( "grammar" ) )
        {
            System.err.println( CCommon.languagestring( CMain.class, "grammarnotset" ) );
            System.exit( -1 );
        }

        CCommon.language(
            l_cli.hasOption( "language" )
            ? Locale.forLanguageTag( l_cli.getOptionValue( "language" ) )
            : Locale.getDefault()
        );

        final Set<String> l_doclean = !l_cli.hasOption( "docclean" )
                                      ? Collections.<String>emptySet()
                                      : FileUtils.readLines( new File( l_cli.getOptionValue( "docclean" ) ), Charset.defaultCharset() )
                                                 .stream()
                                                 .map( String::trim )
                                                 .collect( Collectors.toSet() );

        final Set<String> l_exclude = !l_cli.hasOption( "excludes" )
                                      ? Collections.<String>emptySet()
                                      : Arrays.stream( l_cli.getOptionValue( "excludes" ).split( "," ) )
                                              .map( String::trim )
                                              .collect( Collectors.toSet() );

        final Set<String> l_import = !l_cli.hasOption( "imports" )
                                     ? Collections.<String>emptySet()
                                     : Arrays.stream( l_cli.getOptionValue( "imports" ).split( "," ) )
                                             .map( String::trim )
                                             .collect( Collectors.toSet() );

        final String[] l_templates = l_cli.hasOption( "templates" )
                                     ? l_cli.getOptionValue( "templates" ).split( "," )
                                     : new String[]{DEFAULTTEMPLATE};

        final String l_outputdirectory = l_cli.hasOption( "output" )
                                         ? l_cli.getOptionValue( "output" )
                                         : DEFAULTOUTPUT;


        // --- run generating ------------------------------------------------------------------------------------------
        final Collection<String> l_errors = Arrays.stream( l_cli.getOptionValue( "grammar" ).split( "," ) )
                                                  .parallel()
                                                  .flatMap( i -> generate( l_outputdirectory, l_exclude, l_import, new File( i ), l_doclean, l_templates )
                                                      .stream()
                                                  )
                                                  .collect( Collectors.toList() );

        if ( !l_errors.isEmpty() )
        {
            l_errors.forEach( System.err::println );
            System.exit( -1 );
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- Maven Report Plugin execution -----------------------------------------------------------------------------------------------------------------------

    @Override
    public final String getOutputName()
    {
        return output + "/" + "report";
    }

    @Override
    public final String getName( final Locale p_locale )
    {
        return "RRD-AntLR4";
    }

    @Override
    public final String getDescription( final Locale p_locale )
    {
        return "Railroad-Diagramm for AntLR4";
    }

    @Override
    protected final void executeReport( final Locale p_locale ) throws MavenReportException
    {
        if ( ( imports == null ) || ( imports.length == 0 ) )
            throw new MavenReportException( CCommon.languagestring( this, "importempty" ) );

        final Set<String> l_doclean = ( docclean == null ) || ( docclean.length == 0 )
                                      ? Collections.<String>emptySet()
                                      : Arrays.stream( docclean ).map( String::trim ).collect( Collectors.toSet() );

        final Set<String> l_exclude = ( excludes == null ) || ( excludes.length == 0 )
                                      ? Collections.<String>emptySet()
                                      : Arrays.stream( excludes ).map( String::trim ).collect( Collectors.toSet() );

        final Set<String> l_import = Arrays.stream( imports ).map( String::trim ).collect( Collectors.toSet() );

        // language definition set on runtime
        Locale.setDefault( p_locale );

        final Collection<String> l_errors = Arrays.stream( grammar ).parallel()
                                                  .flatMap( i -> generate( output, l_exclude, l_import,
                                                                           new File( i ), l_doclean, templates
                                                  ).stream() )
                                                  .collect( Collectors.toList() );

        if ( !l_errors.isEmpty() )
            throw new MavenReportException( StringUtils.join( l_errors, "\n" ) );
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- helper ----------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * generating export (generate template instances and call engine)
     *
     * @param p_outputdirectory output directory
     * @param p_exclude file names which are ignored
     * @param p_import import files & directories
     * @param p_grammar path to grammar file or grammar file directory
     * @param p_docuclean set with documentation clean regex
     * @param p_template string with export name
     * @return returns a collection with error messages
     */
    private static Collection<String> generate( final String p_outputdirectory, final Set<String> p_exclude, final Set<String> p_import, final File p_grammar,
                                                final Set<String> p_docuclean, final String... p_template
    )
    {
        try
        {
            // build import map
            final Map<String, File> l_imports = p_import.stream()
                                                        .flatMap( i ->
                                                        {
                                                            try
                                                            {
                                                                return getFileList( new File( i ), p_exclude );
                                                            }
                                                            catch ( final IOException l_exception )
                                                            {
                                                                throw new RuntimeException( l_exception );
                                                            }
                                                        } )
                                                        .collect( Collectors.toMap( i -> FilenameUtils.removeExtension( i.getName() ), j -> j ) );

            return getFileList( p_grammar, p_exclude )
                .flatMap( i ->
                          {
                              try
                              {
                                  return ENGINE.generate(
                                      p_outputdirectory,
                                      i, p_docuclean,
                                      l_imports,
                                      Arrays.stream( p_template )
                                            .map( j -> ETemplate.valueOf( j.trim().toUpperCase() ).generate() )
                                            .collect( Collectors.toSet() )
                                  ).stream();
                              }
                              catch ( final IOException l_exception )
                              {
                                  return Stream.of( l_exception.getMessage() );
                              }
                          } )
                .filter( i -> i != null )
                .collect( Collectors.toList() );
        }
        catch ( final IOException l_exception )
        {
            return Stream.of( l_exception.getMessage() ).collect( Collectors.toSet() );
        }
    }

    /**
     * returns a list of grammar files
     *
     * @param p_input grammar file or directory with grammar files
     * @param p_exclude file names which are ignored
     * @return stream of file objects
     */
    private static Stream<File> getFileList( final File p_input, final Set<String> p_exclude ) throws IOException
    {
        if ( !p_input.exists() )
            throw new RuntimeException( CCommon.languagestring( CMain.class, "notexist", p_input ) );

        return (
            p_input.isFile()
            ? Stream.of( p_input )
            : Files.find( p_input.toPath(), Integer.MAX_VALUE, (i,j) -> j.isRegularFile() ).map( Path::toFile )
        )
        .filter( i -> i.getName().endsWith( GRAMMARFILEEXTENSION ) )
        .filter( i -> !p_exclude.contains( i.getName() ) );
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

}
