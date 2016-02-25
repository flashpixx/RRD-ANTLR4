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

import java.util.Collection;
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
     * exporting template
     *
     * @param p_template template
     */
    public CASTVisitor( final ITemplate p_template )
    {
        m_template = p_template;
    }

    @Override
    public final Object visitParserRuleSpec( final ANTLRv4Parser.ParserRuleSpecContext p_context )
    {
        //System.out.println( "---> " + this.visitRuleBlock( p_context.ruleBlock() ) );

        m_template.rule(
                new CGrammarRule(
                        p_context.RULE_REF().getText(),
                        p_context.DOC_COMMENT() == null ? "" : p_context.DOC_COMMENT().getText(),
                        null
                )
        );
        return null;
    }

    @Override
    public final Object visitRuleAltList( final ANTLRv4Parser.RuleAltListContext p_context )
    {
        return p_context.labeledAlt() != null ? p_context.labeledAlt().stream().map( i -> this.visitLabeledAlt( i ) ).filter( i -> i != null ).collect(
                Collectors.toList() ) : null;
    }

    @Override
    public final Object visitLexerRuleSpec( final ANTLRv4Parser.LexerRuleSpecContext p_context )
    {
        m_template.terminal(
                new CGrammarTerminal(
                        p_context.TOKEN_REF().getText(),
                        p_context.FRAGMENT() != null,
                        p_context.DOC_COMMENT() == null ? "" : p_context.DOC_COMMENT().getText(),
                        (Collection<Collection<String>>) this.visitLexerRuleBlock( p_context.lexerRuleBlock() )
                ) );
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
        return p_context.lexerElement().stream().map( i -> this.visitLexerElement( i ) ).filter( i -> i != null ).collect( Collectors.toList() );
    }

    @Override
    public final Object visitLexerElement( final ANTLRv4Parser.LexerElementContext p_context )
    {
        return p_context.getText();
    }

}
