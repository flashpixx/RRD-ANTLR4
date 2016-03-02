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
import de.flashpixx.rrd_antlr4.engine.template.ITemplate;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;


/**
 * AntLR 4 AST visitor
 */
@SuppressWarnings( {"all", "warnings", "unchecked", "unused", "cast"} )
public final class CASTVisitor extends ANTLRv4ParserBaseVisitor<Object>
{
    /**
     * exporting template
     */
    private final ITemplate m_template;
    /**
     * grammar name - is set by the first grammar rule
     */
    private IGrammarComplexElement m_grammar;
    /**
     * set with grammer imports
     */
    private Set<IGrammarSimpleElement<String>> m_imports = new HashSet<>();
    /**
     * set with documentation clean pattern
     */
    private final Set<String> m_docuclean;

    /**
     * exporting template
     *
     * @param p_template template
     * @param p_docuclean set with regex for documentation cleanup
     */
    public CASTVisitor( final ITemplate p_template, final Set<String> p_docuclean )
    {
        m_template = p_template;
        m_docuclean = p_docuclean;
    }


    @Override
    public final Object visitGrammarSpec( final ANTLRv4Parser.GrammarSpecContext p_context )
    {
        m_grammar = new CGrammar( p_context.id().getText(), this.cleanComment( p_context.DOC_COMMENT() == null ? null : p_context.DOC_COMMENT().getText() ) );
        m_template.grammar( m_grammar );
        return super.visitGrammarSpec( p_context );
    }

    @Override
    public final Object visitDelegateGrammar( final ANTLRv4Parser.DelegateGrammarContext p_context )
    {
        p_context.id().stream().map( i -> (IGrammarSimpleElement<String>) this.visitId( i ) ).forEach( i -> m_imports.add( i ) );
        return super.visitDelegateGrammar( p_context );
    }

    @Override
    public final Object visitId( final ANTLRv4Parser.IdContext p_context )
    {
        return new CGrammarIdentifier( p_context.getText() );
    }

    @Override
    public final Object visitParserRuleSpec( final ANTLRv4Parser.ParserRuleSpecContext p_context )
    {
        m_template.rule(
                m_grammar,
                new CGrammarRule(
                        p_context.RULE_REF().getText(),
                        this.cleanComment( p_context.DOC_COMMENT() == null ? null : p_context.DOC_COMMENT().getText() ),
                        null
                        // (List<List<IGrammarElement>>) this.visitRuleBlock( p_context.ruleBlock() )
                )
        );
        return null;
    }

    @Override
    public final Object visitRuleAltList( final ANTLRv4Parser.RuleAltListContext p_context )
    {
        return p_context.labeledAlt().stream().map( i -> this.visitLabeledAlt( i ) ).filter( i -> i != null ).collect( Collectors.toList() );
    }


    @Override
    public final Object visitAlternative( final ANTLRv4Parser.AlternativeContext p_context )
    {
        // ignore element options
        return p_context.element() == null
               ? null
               : p_context.element().stream()
                          .map( i -> (IGrammarElement) this.visitElement( i ) )
                          .filter( i -> i != null )
                          .collect( Collectors.toList() );
    }

    @Override
    public final Object visitElement( final ANTLRv4Parser.ElementContext p_context )
    {
        return this.convert( p_context.getText() );
    }



    @Override
    public final Object visitLexerRuleSpec( final ANTLRv4Parser.LexerRuleSpecContext p_context )
    {
        m_template.terminal(
                m_grammar,
                new CGrammarTerminal(
                        p_context.TOKEN_REF().getText(),
                        p_context.FRAGMENT() != null,
                        this.cleanComment( p_context.DOC_COMMENT() == null ? null : p_context.DOC_COMMENT().getText() ),
                        null //(List<List<IGrammarSimpleElement<?>>>) this.visitLexerRuleBlock( p_context.lexerRuleBlock() )
                )
        );
        return null;
    }

    @Override
    public final Object visitLexerAltList( final ANTLRv4Parser.LexerAltListContext p_context )
    {
        return p_context.lexerAlt().stream().map( i -> this.visitLexerAlt( i ) ).filter( i -> i != null ).collect( Collectors.toList() );
    }

    @Override
    public final Object visitLexerAlt( final ANTLRv4Parser.LexerAltContext p_context )
    {
        // ignoring lexer command rule
        return this.visitLexerElements( p_context.lexerElements() );
    }

    @Override
    public final Object visitLexerElements( final ANTLRv4Parser.LexerElementsContext p_context )
    {
        return p_context.lexerElement().stream()
                        .map( i -> (IGrammarElement) this.visitLexerElement( i ) )
                        .filter( i -> i != null )
                        .collect( Collectors.toList() );
    }

    @Override
    public final Object visitLexerElement( final ANTLRv4Parser.LexerElementContext p_context )
    {
        return this.convert( p_context.getText() );
    }

    /**
     * returns a set with grammar imports
     *
     * @return set with grammar imports
     */
    public final Set<IGrammarSimpleElement<String>> getGrammarImports()
    {
        return m_imports;
    }

    /**
     * converts a string to a grammar element
     *
     * @param p_input string input
     * @return grammar element or null
     */
    private IGrammarElement convert( final String p_input )
    {
        if ( ( p_input == null ) || ( p_input.isEmpty() ) )
            return null;

        // string check
        if ( p_input.startsWith( "'" ) && ( p_input.endsWith( "'" ) ) )
            return new CTerminalValue<>( p_input.substring( 1, p_input.length() - 1 ) );

        // regular expression check
        try
        {
            return new CTerminalValue<>( Pattern.compile( p_input ) );
        }
        catch ( final PatternSyntaxException p_exception )
        {
        }

        // it is a string / identifier
        return new CGrammarIdentifier( p_input );
    }

    /**
     * cleanup comment from doxygen structure
     *
     * @param p_comment comment input
     * @return cleaned text or null
     */
    private String cleanComment( final String p_comment )
    {
        if ( p_comment == null )
            return null;

        // remove CR, LF and tab
        final CStringReplace l_documentation = new CStringReplace( p_comment ).replaceAll( "(\\t|\\n)+", " " ).replace( "\r", "" );
        m_docuclean.stream().forEach( i -> l_documentation.replaceAll( i, "" ) );

        return l_documentation.replaceAll( "\\*", "" ).replaceAll( "\\/", "" ).get().trim();
    }

}
