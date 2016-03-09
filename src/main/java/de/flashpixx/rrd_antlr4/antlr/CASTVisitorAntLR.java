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

import de.flashpixx.rrd_antlr4.engine.template.ITemplate;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * AntLR 4 AST visitor of AntLR 4 grammar file
 */
@SuppressWarnings( {"all", "warnings", "unchecked", "unused", "cast"} )
public final class CASTVisitorAntLR extends ANTLRv4ParserBaseVisitor<IGrammarElement>
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
    public CASTVisitorAntLR( final ITemplate p_template, final Set<String> p_docuclean )
    {
        m_template = p_template;
        m_docuclean = p_docuclean;
    }


    @Override
    public final IGrammarElement visitGrammarSpec( final ANTLRv4Parser.GrammarSpecContext p_context )
    {
        m_grammar = m_template.grammar(
                new CGrammar(
                        p_context.id().getText(),
                        CCommon.cleanComment( p_context.DOC_COMMENT() == null ? null : p_context.DOC_COMMENT().getText(), m_docuclean )
                )
        );
        return super.visitGrammarSpec( p_context );
    }



    @Override
    public final IGrammarElement visitDelegateGrammar( final ANTLRv4Parser.DelegateGrammarContext p_context )
    {
        p_context.id().stream().map( i -> (IGrammarSimpleElement<String>) this.visitId( i ) ).forEach( i -> m_imports.add( i ) );
        return this.visitChildren( p_context );
    }

    @Override
    public final IGrammarElement visitParserRuleSpec( final ANTLRv4Parser.ParserRuleSpecContext p_context )
    {
        return m_template.element(
                m_grammar,
                new CGrammarRule(
                        p_context.RULE_REF().getText(),
                        CCommon.cleanComment( p_context.DOC_COMMENT() == null ? null : p_context.DOC_COMMENT().getText(), m_docuclean ),
                        this.visitRuleBlock( p_context.ruleBlock() )
                )
        );
    }

    @Override
    public final IGrammarElement visitRuleAltList( final ANTLRv4Parser.RuleAltListContext p_context )
    {
        return CCommon.choice(
                p_context.labeledAlt().stream()
                         .map( i -> this.visitLabeledAlt( i ) )
                         .filter( i -> i != null )
                         .collect( Collectors.toList() )
        );
    }

    @Override
    public final IGrammarElement visitLexerAltList( final ANTLRv4Parser.LexerAltListContext p_context )
    {
        return CCommon.choice(
                p_context.lexerAlt().stream()
                         .map( i -> this.visitLexerAlt( i ) )
                         .filter( i -> i != null )
                         .collect( Collectors.toList() )
        );
    }

    @Override
    public final IGrammarElement visitLexerElements( final ANTLRv4Parser.LexerElementsContext p_context )
    {
        return CCommon.sequence(
                p_context.lexerElement().stream()
                         .map( i -> this.visitLexerElement( i ) )
                         .filter( i -> i != null )
                         .collect( Collectors.toList() )
        );
    }

    @Override
    public final IGrammarElement visitLexerRuleSpec( final ANTLRv4Parser.LexerRuleSpecContext p_context )
    {
        // Element Push
        return m_template.element(
                m_grammar,
                new CGrammarNonTerminal(
                        p_context.TOKEN_REF().getText(),
                        CCommon.cleanComment( p_context.DOC_COMMENT() == null ? null : p_context.DOC_COMMENT().getText(), m_docuclean ),
                        this.visitLexerRuleBlock( p_context.lexerRuleBlock() )
                )
        );
    }

    @Override
    public final IGrammarElement visitAltList( final ANTLRv4Parser.AltListContext p_context )
    {
        return CCommon.choice(
                p_context.alternative().stream()
                         .map( i -> this.visitAlternative( i ) )
                         .filter( i -> i != null )
                         .collect( Collectors.toList() )
        );
    }

    @Override
    public final IGrammarElement visitNotSet( final ANTLRv4Parser.NotSetContext p_context )
    {
        // Sequence with NOT
        return this.visitChildren( p_context ).cardinality( IGrammarElement.ECardinality.NEGATION );
    }

    @Override
    public final IGrammarElement visitAlternative( final ANTLRv4Parser.AlternativeContext p_context )
    {
        return CCommon.sequence(
                p_context.element().stream()
                         .map( i -> this.visitElement( i ) )
                         .filter( i -> i != null )
                         .collect( Collectors.toList() )
        );
    }

    @Override
    public final IGrammarElement visitLexerAlt( final ANTLRv4Parser.LexerAltContext p_context )
    {
        // Sequence - ignoring lexer command rule
        return this.visitLexerElements( p_context.lexerElements() );
    }

    @Override
    public final IGrammarElement visitBlockSet( final ANTLRv4Parser.BlockSetContext p_context )
    {
        return CCommon.choice(
                p_context.setElement().stream()
                         .map( i -> this.visitSetElement( i ) )
                         .filter( i -> i != null )
                         .collect( Collectors.toList() )
        );
    }

    @Override
    public final IGrammarElement visitElement( final ANTLRv4Parser.ElementContext p_context )
    {
        if ( p_context.labeledElement() != null )
            return CCommon.cardinality(
                    p_context.ebnfSuffix() != null
                    ? p_context.ebnfSuffix().getText()
                    : "",
                    this.visitLabeledElement( p_context.labeledElement() )
            );


        if ( p_context.atom() != null )
            return CCommon.cardinality(
                    p_context.ebnfSuffix() != null
                    ? p_context.ebnfSuffix().getText()
                    : "",
                    this.visitAtom( p_context.atom() )
            );

        if ( p_context.ebnf() != null )
            return this.visitEbnf( p_context.ebnf() );


        return null;
    }

    @Override
    public final IGrammarElement visitEbnf( final ANTLRv4Parser.EbnfContext p_context )
    {
        return CCommon.cardinality(
                p_context.blockSuffix() != null
                ? p_context.blockSuffix().getText()
                : "",
                this.visitBlock( p_context.block() )
        );
    }

    @Override
    public final IGrammarElement visitBlock( final ANTLRv4Parser.BlockContext p_context )
    {
        // only alternative elements are needed
        return new CGrammarGroup( this.visitAltList( p_context.altList() ) );
    }



    @Override
    public final IGrammarElement visitTerminal( final ANTLRv4Parser.TerminalContext p_context )
    {
        return CCommon.terminalvalue(
                p_context.TOKEN_REF() != null
                ? p_context.TOKEN_REF().getText()
                : p_context.STRING_LITERAL().getText()
        );
    }

    @Override
    public final IGrammarElement visitLexerElement( final ANTLRv4Parser.LexerElementContext p_context )
    {
        return CCommon.terminalvalue( p_context.getText() );
    }

    @Override
    public final IGrammarElement visitRuleref( final ANTLRv4Parser.RulerefContext p_context )
    {
        return new CGrammarIdentifier( p_context.RULE_REF().getText() );
    }

    @Override
    public final IGrammarElement visitId( final ANTLRv4Parser.IdContext p_context )
    {
        return new CGrammarIdentifier( p_context.getText() );
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

}
