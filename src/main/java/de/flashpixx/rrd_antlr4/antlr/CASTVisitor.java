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

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
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
        m_grammar = m_template.grammar(
                new CGrammar(
                        p_context.id().getText(),
                        this.cleanComment( p_context.DOC_COMMENT() == null ? null : p_context.DOC_COMMENT().getText() )
                )
        );
        return super.visitGrammarSpec( p_context );
    }



    @Override
    public final Object visitDelegateGrammar( final ANTLRv4Parser.DelegateGrammarContext p_context )
    {
        p_context.id().stream().map( i -> (IGrammarSimpleElement<String>) this.visitId( i ) ).forEach( i -> m_imports.add( i ) );
        return this.visitChildren( p_context );
    }

    @Override
    public final Object visitId( final ANTLRv4Parser.IdContext p_context )
    {
        return new CGrammarIdentifier( p_context.getText() );
    }

    @Override
    public final Object visitParserRuleSpec( final ANTLRv4Parser.ParserRuleSpecContext p_context )
    {
        m_template.element(
                m_grammar,
                new CGrammarRule(
                        p_context.RULE_REF().getText(),
                        this.cleanComment( p_context.DOC_COMMENT() == null ? null : p_context.DOC_COMMENT().getText() ),
                        (IGrammarCollection) this.visitRuleBlock( p_context.ruleBlock() )
                )
        );
        return null;
    }

    @Override
    public final Object visitRuleAltList( final ANTLRv4Parser.RuleAltListContext p_context )
    {
        return new CGrammarChoice(
                IGrammarElement.ECardinality.NONE,
                p_context.labeledAlt().stream()
                         .map( i -> (IGrammarElement) this.visitLabeledAlt( i ) )
                         .filter( i -> i != null )
                         .collect( Collectors.toList() )
        );
    }

    @Override
    public final Object visitLexerAltList( final ANTLRv4Parser.LexerAltListContext p_context )
    {
        return new CGrammarChoice(
                IGrammarElement.ECardinality.NONE,
                p_context.lexerAlt().stream().map( i -> (IGrammarElement) this.visitLexerAlt( i ) ).filter( i -> i != null )
                         .collect( Collectors.toList() )
        );
    }

    @Override
    public final Object visitLexerElements( final ANTLRv4Parser.LexerElementsContext p_context )
    {
        return new CGrammarSequence(
                IGrammarElement.ECardinality.NONE,
                p_context.lexerElement().stream()
                         .map( i -> (IGrammarElement) this.visitLexerElement( i ) )
                         .filter( i -> i != null )
                         .collect( Collectors.toList() )
        );
    }

    @Override
    public final Object visitRuleSpec( final ANTLRv4Parser.RuleSpecContext p_context )
    {
        // Element Push
        return super.visitRuleSpec( p_context );
    }

    @Override
    public final Object visitLexerRuleSpec( final ANTLRv4Parser.LexerRuleSpecContext p_context )
    {
        // Element Push
        m_template.element(
                m_grammar,
                new CGrammarNonTerminal(
                        p_context.TOKEN_REF().getText(),
                        this.cleanComment( p_context.DOC_COMMENT() == null ? null : p_context.DOC_COMMENT().getText() ),
                        IGrammarElement.ECardinality.NONE,
                        (IGrammarCollection) this.visitLexerRuleBlock( p_context.lexerRuleBlock() )
                )
        );
        return null;
    }

    @Override
    public final Object visitAltList( final ANTLRv4Parser.AltListContext p_context )
    {
        return new CGrammarChoice(
                IGrammarElement.ECardinality.NONE,
                p_context.alternative().stream()
                         .map( i -> (IGrammarElement) this.visit( i ) )
                         .collect( Collectors.toList() )
        );
    }

    @Override
    public final Object visitNotSet( final ANTLRv4Parser.NotSetContext p_context )
    {
        // Sequence with NOT
        return ( (IGrammarElement) this.visitChildren( p_context ) ).cardinality( IGrammarElement.ECardinality.NEGATION );
    }

    @Override
    public final Object visitLexerAlt( final ANTLRv4Parser.LexerAltContext p_context )
    {
        // Sequence

        // ignoring lexer command rule
        return this.visitLexerElements( p_context.lexerElements() );
    }

    // visitElements

    @Override
    public final Object visitBlockSet( final ANTLRv4Parser.BlockSetContext p_context )
    {
        return new CGrammarChoice(
                IGrammarElement.ECardinality.NONE,
                p_context.setElement().stream()
                         .map( i -> (IGrammarElement) this.visit( i ) )
                         .collect( Collectors.toList() )
        );
    }

    @Override
    public final Object visitEbnfSuffix( final ANTLRv4Parser.EbnfSuffixContext p_context )
    {
        final IGrammarElement l_element = (IGrammarElement) this.visitChildren( p_context );

        if ( p_context.PLUS() != null )
            return l_element.cardinality( IGrammarElement.ECardinality.ONEORMORE );

        if ( p_context.STAR() != null )
            return l_element.cardinality( IGrammarElement.ECardinality.ZEROORMORE );

        if ( p_context.QUESTION() != null )
            return l_element.cardinality( IGrammarElement.ECardinality.OPTIONAL );

        return l_element;
    }

    @Override
    public final Object visitLexerAtom( final ANTLRv4Parser.LexerAtomContext p_context )
    {
        // Terminal & NonTermial
        return super.visitLexerAtom( p_context );
    }

    @Override
    public final Object visitTerminal( final ANTLRv4Parser.TerminalContext p_context )
    {
        return p_context.TOKEN_REF() != null
               ? new CGrammarTerminal( IGrammarElement.ECardinality.NONE, p_context.TOKEN_REF().getText() )
               : new CGrammarTerminal( IGrammarElement.ECardinality.NONE, p_context.STRING_LITERAL().getText() );
    }

    @Override
    public final Object visitRange( final ANTLRv4Parser.RangeContext p_context )
    {
        return new CGrammarTerminal<>(
                IGrammarElement.ECardinality.NONE,
                MessageFormat.format(
                        "{0} .. {1}",
                        p_context.STRING_LITERAL( 0 ).getText(),
                        p_context.STRING_LITERAL( 1 ).getText()
                )
        );
    }

    @Override
    public final Object visitElement( final ANTLRv4Parser.ElementContext p_context )
    {
        return new CGrammarTerminal<>( IGrammarElement.ECardinality.NONE, this.cleanString( p_context.getText() ) );
    }

    @Override
    public final Object visitLexerElement( final ANTLRv4Parser.LexerElementContext p_context )
    {
        return new CGrammarTerminal<>( IGrammarElement.ECardinality.NONE, this.cleanString( p_context.getText() ) );
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
     * clean string value
     *
     * @param p_string string data
     * @return cleaned string
     */
    private String cleanString( final String p_string )
    {
        if ( ( p_string.length() > 1 ) && ( p_string.startsWith( "'" ) ) && ( p_string.endsWith( "'" ) ) )
            return p_string.substring( 1, p_string.length() - 1 );

        return p_string;
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
