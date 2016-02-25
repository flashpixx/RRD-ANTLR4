package de.flashpixx.rrd_antlr4.antlr;

import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;


/**
 * represenation of a grammar rule
 */
public final class CGrammarRule implements IGrammarRule
{
    /**
     * ID of the rule
     */
    private final String m_id;
    /**
     * comment of the rule
     */
    private final String m_documentation;
    /**
     * alternatives
     */
    private final List<List<IGrammarElement>> m_alternatives;

    /**
     * ctor
     *
     * @param p_id rule ID
     * @param p_documentation comment
     * @param p_alternatives alternatives
     */
    public CGrammarRule( final String p_id, final String p_documentation, final List<List<IGrammarElement>> p_alternatives )
    {
        m_id = p_id;
        m_documentation = p_documentation == null ? "" : p_documentation;
        m_alternatives = p_alternatives == null ? Collections.<List<IGrammarElement>>emptyList() : p_alternatives;
    }


    @Override
    public final String id()
    {
        return m_id;
    }

    @Override
    public final String documentation()
    {
        return m_documentation;
    }

    @Override
    public final List<List<IGrammarElement>> alternatives()
    {
        return m_alternatives;
    }


    @Override
    public final int hashCode()
    {
        return this.id().hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return this.hashCode() == p_object.hashCode();
    }

    @Override
    public final String toString()
    {
        return MessageFormat.format(
                "{0} -> {1}{2}",
                this.id(),
                StringUtils.join( m_alternatives, " | " ),
                m_documentation.isEmpty() ? "" : "   // " + m_documentation
        );
    }
}
