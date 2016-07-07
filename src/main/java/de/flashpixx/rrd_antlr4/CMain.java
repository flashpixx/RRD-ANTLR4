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
import de.flashpixx.rrd_antlr4.engine.template.ITemplate;
import de.flashpixx.rrd_antlr4.generator.CPlugin;
import de.flashpixx.rrd_antlr4.generator.CStandalone;
import de.flashpixx.rrd_antlr4.generator.IGenerator;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.jooq.lambda.tuple.Tuple5;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
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
     * name of the plugin
     */
    private static final String NAME = "RRD-AntLR4";
    /**
     * main description
     */
    private static final String DESCRIPTION = "Railroad-Diagramm for AntLR4";
    /**
     * directory for AntLR grammar files
     */
    private static final String ANTLRGRAMMERDIR = "src/main/antlr4";
    /**
     * directory for AntLR import directory
     */
    private static final String ANTLRIMPORTDIR = "src/main/antlr4/imports";

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
     * Maven plugin used templates option
     */
    @Parameter( defaultValue = DEFAULTTEMPLATE )
    private String[] templates;
    /**
     * Maven plugin basedir of the grammar files
     */
    @Parameter( defaultValue = "${project.basedir}/" + ANTLRGRAMMERDIR )
    private String grammarbasedir;
    /**
     * Maven plugin default directories of grammars
     */
    @Parameter( defaultValue = "${project.basedir}/" + ANTLRGRAMMERDIR )
    private String[] grammar;
    /**
     * Maven plugin default grammar import directories
     */
    @Parameter( defaultValue = "${project.basedir}/" + ANTLRIMPORTDIR )
    private String[] imports;
    /**
     * Maven plugin parameter for output
     */
    @Parameter( defaultValue = "${project.reporting.outputDirectory}/" + DEFAULTOUTPUT )
    private String output;
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
        // --- define CLI options ---
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


        // --- process CLI arguments and push configuration ---
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


        final Tuple5<File, Set<ITemplate>, Set<File>, Set<String>, Set<String>> l_initdata = CMain.initialize(
            l_cli.getOptionValue( "language" ),

            l_cli.hasOption( "output" )
            ? l_cli.getOptionValue( "output" )
            : DEFAULTOUTPUT,

            l_cli.hasOption( "templates" )
            ? l_cli.getOptionValue( "templates" ).split( "," )
            : new String[]{DEFAULTTEMPLATE},

            l_cli.getOptionValue( "imports" ).split( "," ),

            l_cli.getOptionValue( "excludes" ).split( "," ),

            l_cli.getOptionValue( "docclean" ).split( "," )
        );


        // --- run generator ---
        final IGenerator l_generator = new CStandalone( l_initdata.v1(), l_initdata.v3(), l_initdata.v5(), l_initdata.v2() );

        if ( Arrays.stream( l_cli.getOptionValue( "grammar" ).split( "," ) )
                   .flatMap( i -> CMain.filelist( new File( i.trim() ), l_initdata.v3(), l_initdata.v4() ) )
                   .map( i -> l_generator.generate( i ).hasError() )
                   .findFirst()
                   .isPresent()
            )
            System.exit( -1 );

        l_generator.finish();
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- Maven Report Plugin execution -----------------------------------------------------------------------------------------------------------------------

    @Override
    public final String getOutputName()
    {
        return DEFAULTOUTPUT;
    }

    @Override
    public final String getName( final Locale p_locale )
    {
        return NAME;
    }

    @Override
    public final String getDescription( final Locale p_locale )
    {
        return DESCRIPTION;
    }

    @Override
    protected final void executeReport( final Locale p_locale ) throws MavenReportException
    {
        if ( ( imports == null ) || ( imports.length == 0 ) )
            throw new MavenReportException( CCommon.languagestring( this, "importempty" ) );

        final Tuple5<File, Set<ITemplate>, Set<File>, Set<String>, Set<String>> l_initdata = CMain.initialize(
            p_locale.toLanguageTag(),
            output,
            templates,
            imports,
            excludes,
            docclean
        );

        // --- run generator ---
        final IGenerator l_generator = new CPlugin( this.getSink(), NAME, l_initdata.v1(), new File( grammarbasedir ), l_initdata.v3(), l_initdata.v5(), l_initdata.v2() );
        Arrays.stream( grammar )
              .flatMap( i -> CMain.filelist( new File( i.trim() ), l_initdata.v3(), l_initdata.v4() ) )
              .forEach( l_generator::generate );

        l_generator.finish();
/*
        if ( !l_errors.isEmpty() )
            throw new MavenReportException( StringUtils.join( l_errors, "\n" ) );
            */
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- helper ----------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * initialize all data
     *
     * @param p_locale locale
     * @param p_outputdirectory output directory string
     * @param p_templates template string array
     * @param p_imports import string array
     * @param p_excludes exclude string array
     * @param p_docclean documentation clean array
     * @return 5-tuple with initialized and converted data
     */
    private static Tuple5<File, Set<ITemplate>, Set<File>, Set<String>, Set<String>> initialize(
        final String p_locale, final String p_outputdirectory, final String[] p_templates,
        final String[] p_imports, final String[] p_excludes, final String[] p_docclean
    )
    {
        CCommon.language(
            ( p_locale == null ) || ( p_locale.isEmpty() )
            ? Locale.getDefault()
            : Locale.forLanguageTag( p_locale )
        );

        return new Tuple5<>(
            new File( p_outputdirectory ),

            ( p_templates == null ) || ( p_templates.length == 0 )
            ? Collections.<ITemplate>emptySet()
            :
            Collections.unmodifiableSet(
                Arrays.stream( p_templates )
                      .map( i -> ETemplate.valueOf( i.trim().toUpperCase() ).generate() )
                      .collect( Collectors.toSet() )
            ),

            ( p_imports == null ) || ( p_imports.length == 0 )
            ? Collections.<File>emptySet()
            : Collections.unmodifiableSet(
                Arrays.stream( p_imports )
                      .map( String::trim )
                      .map( File::new )
                      .flatMap( i -> CMain.filelist( i, Collections.<File>emptySet(), Collections.<String>emptySet() ) )
                      .collect( Collectors.toSet() )
            ),

            ( p_excludes == null ) || ( p_excludes.length == 0 )
            ? Collections.<String>emptySet()
            : Collections.unmodifiableSet(
                Arrays.stream( p_excludes )
                      .map( String::trim )
                      .collect( Collectors.toSet() )
            ),

            ( p_docclean == null ) || ( p_docclean.length == 0 )
            ? Collections.<String>emptySet()
            : Collections.unmodifiableSet(
                Arrays.stream( p_docclean )
                      .flatMap( i ->
                                {
                                    try
                                    {
                                        return FileUtils.readLines( new File( i ), Charset.defaultCharset() ).stream();
                                    }
                                    catch ( final IOException l_exception )
                                    {
                                        return Stream.of( "" );
                                    }
                                } )
                      .map( String::trim )
                      .filter( i -> !i.isEmpty() )
                      .collect( Collectors.toSet() )
            )
        );
    }


    /**
     * returns a list of grammar files
     *
     * @param p_input grammar file or directory with grammar files
     * @param p_import imported files
     * @param p_exclude file names which are ignored
     * @return stream of file objects
     */
    private static Stream<File> filelist( final File p_input, final Set<File> p_import, final Set<String> p_exclude )
    {
        if ( !p_input.exists() )
            throw new RuntimeException( CCommon.languagestring( CMain.class, "notexist", p_input ) );

        try
        {
            return (
                p_input.isFile()
                ? Stream.of( p_input )
                : Files.find( p_input.toPath(), Integer.MAX_VALUE, ( i, j ) -> ( j.isRegularFile() ) && ( !j.isSymbolicLink() ) ).map( Path::toFile )
            )
                .filter( i -> i.getName().endsWith( GRAMMARFILEEXTENSION ) )
                .filter( i -> !p_import.contains( i ) )
                .filter( i -> !p_exclude.contains( i.getName() ) );
        }
        catch ( final IOException l_exception )
        {
            throw new RuntimeException( l_exception );
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

}
