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

import de.flashpixx.grammar.ANTLRv4Parser;
import de.flashpixx.rrd_antlr4.engine.template.ITemplate;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

import java.util.Collection;
import java.util.stream.Collectors;


/**
 * AntLR 4 AST visitor
 */
@SuppressWarnings( {"all", "warnings", "unchecked", "unused", "cast"} )
public final class CASTVisitor extends AbstractParseTreeVisitor<Object> implements IVisitor
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
    public final Object visitGrammarSpec( final ANTLRv4Parser.GrammarSpecContext p_context )
    {
        return this.visitRules( p_context.rules() );
    }

    @Override
    public final Object visitGrammarType( final ANTLRv4Parser.GrammarTypeContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitPrequelConstruct( final ANTLRv4Parser.PrequelConstructContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitOptionsSpec( final ANTLRv4Parser.OptionsSpecContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitOption( final ANTLRv4Parser.OptionContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitOptionValue( final ANTLRv4Parser.OptionValueContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitDelegateGrammars( final ANTLRv4Parser.DelegateGrammarsContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitDelegateGrammar( final ANTLRv4Parser.DelegateGrammarContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitTokensSpec( final ANTLRv4Parser.TokensSpecContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitChannelsSpec( final ANTLRv4Parser.ChannelsSpecContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitIdList( final ANTLRv4Parser.IdListContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitAction( final ANTLRv4Parser.ActionContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitActionScopeName( final ANTLRv4Parser.ActionScopeNameContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitActionBlock( final ANTLRv4Parser.ActionBlockContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitArgActionBlock( final ANTLRv4Parser.ArgActionBlockContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitModeSpec( final ANTLRv4Parser.ModeSpecContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitRules( final ANTLRv4Parser.RulesContext p_context )
    {
        return this.visitChildren( p_context );
    }

    @Override
    public final Object visitRuleSpec( final ANTLRv4Parser.RuleSpecContext p_context )
    {
        return this.visitChildren( p_context );
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
    public final Object visitExceptionGroup( final ANTLRv4Parser.ExceptionGroupContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitExceptionHandler( final ANTLRv4Parser.ExceptionHandlerContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitFinallyClause( final ANTLRv4Parser.FinallyClauseContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitRulePrequel( final ANTLRv4Parser.RulePrequelContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitRuleReturns( final ANTLRv4Parser.RuleReturnsContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitThrowsSpec( final ANTLRv4Parser.ThrowsSpecContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitLocalsSpec( final ANTLRv4Parser.LocalsSpecContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitRuleAction( final ANTLRv4Parser.RuleActionContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitRuleModifiers( final ANTLRv4Parser.RuleModifiersContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitRuleModifier( final ANTLRv4Parser.RuleModifierContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitRuleBlock( final ANTLRv4Parser.RuleBlockContext p_context )
    {
        return this.visitChildren( p_context.ruleAltList() );
    }

    @Override
    public final Object visitRuleAltList( final ANTLRv4Parser.RuleAltListContext p_context )
    {
        return p_context.labeledAlt() != null ? p_context.labeledAlt().stream().map( i -> this.visitLabeledAlt( i ) ).filter( i -> i != null ).collect(
                Collectors.toList() ) : null;
    }

    @Override
    public final Object visitLabeledAlt( final ANTLRv4Parser.LabeledAltContext p_context )
    {
        return this.visitAlternative( p_context.alternative() );
    }

    @Override
    public final Object visitLexerRuleSpec( final ANTLRv4Parser.LexerRuleSpecContext p_context )
    {
        m_template.terminal(
                new CGrammarTerminal(
                        p_context.TOKEN_REF().getText(),
                        p_context.FRAGMENT() != null,
                        p_context.DOC_COMMENT() == null ? "" : p_context.DOC_COMMENT().getText(),
                        (Collection<String>) this.visitLexerRuleBlock( p_context.lexerRuleBlock() )
                ) );
        return null;
    }

    @Override
    public final Object visitLexerRuleBlock( final ANTLRv4Parser.LexerRuleBlockContext p_context )
    {
        return this.visitChildren( p_context );
    }

    @Override
    public final Object visitLexerAltList( final ANTLRv4Parser.LexerAltListContext p_context )
    {
        return p_context.lexerAlt() != null ? p_context.lexerAlt().stream().map( i -> this.visitChildren( i ) ).filter( i -> i != null ).collect(
                Collectors.toList() ) : null;
    }

    @Override
    public final Object visitLexerAlt( final ANTLRv4Parser.LexerAltContext p_context )
    {
        return this.visitChildren( p_context );
    }

    @Override
    public final Object visitLexerElements( final ANTLRv4Parser.LexerElementsContext p_context )
    {
        return p_context.lexerElement() != null ? p_context.lexerElement().stream().map( i -> this.visitLexerElement( i ) ).filter( i -> i != null ).collect(
                Collectors.toList() ) : null;
    }

    @Override
    public final Object visitLexerElement( final ANTLRv4Parser.LexerElementContext p_context )
    {
        return p_context != null ? p_context.getText() : null;
    }

    @Override
    public final Object visitLabeledLexerElement( final ANTLRv4Parser.LabeledLexerElementContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitLexerBlock( final ANTLRv4Parser.LexerBlockContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitLexerCommands( final ANTLRv4Parser.LexerCommandsContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitLexerCommand( final ANTLRv4Parser.LexerCommandContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitLexerCommandName( final ANTLRv4Parser.LexerCommandNameContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitLexerCommandExpr( final ANTLRv4Parser.LexerCommandExprContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitAltList( final ANTLRv4Parser.AltListContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitAlternative( final ANTLRv4Parser.AlternativeContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitElement( final ANTLRv4Parser.ElementContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitLabeledElement( final ANTLRv4Parser.LabeledElementContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitEbnf( final ANTLRv4Parser.EbnfContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitBlockSuffix( final ANTLRv4Parser.BlockSuffixContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitEbnfSuffix( final ANTLRv4Parser.EbnfSuffixContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitLexerAtom( final ANTLRv4Parser.LexerAtomContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitAtom( final ANTLRv4Parser.AtomContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitNotSet( final ANTLRv4Parser.NotSetContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitBlockSet( final ANTLRv4Parser.BlockSetContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitSetElement( final ANTLRv4Parser.SetElementContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitBlock( final ANTLRv4Parser.BlockContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitRuleref( final ANTLRv4Parser.RulerefContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitRange( final ANTLRv4Parser.RangeContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitTerminal( final ANTLRv4Parser.TerminalContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitElementOptions( final ANTLRv4Parser.ElementOptionsContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitElementOption( final ANTLRv4Parser.ElementOptionContext p_context )
    {
        return null;
    }

    @Override
    public final Object visitId( final ANTLRv4Parser.IdContext p_context )
    {
        return null;
    }
}
