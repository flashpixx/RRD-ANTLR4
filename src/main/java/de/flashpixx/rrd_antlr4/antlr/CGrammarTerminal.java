package de.flashpixx.rrd_antlr4.antlr;

import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;


/**
 * represenation of a terminal symbol
 */
public class CGrammarTerminal implements IGrammarTerminal
{
    /**
     * ID
     */
    private final String m_id;
    /**
     * fragment flag
     */
    private final boolean m_isfragment;
    /**
     * documentation string
     */
    private final String m_documentation;
    /**
     * alternatives
     */
    private final Collection<String> m_alternatives;

    /**
     * ctor
     *
     * @param p_id ID
     * @param p_isfragment fragment
     * @param p_documentation documentation
     * @param p_alternatives alternatives
     */
    public CGrammarTerminal( final String p_id, final boolean p_isfragment, final String p_documentation, final Collection<String> p_alternatives )
    {
        m_id = p_id;
        m_isfragment = p_isfragment;
        m_documentation = p_documentation == null ? "" : p_documentation;
        m_alternatives = p_alternatives == null ? Collections.<String>emptyList() : p_alternatives;
    }


    @Override
    public final boolean isFragment()
    {
        return m_isfragment;
    }

    @Override
    public final Collection<String> alternatives()
    {
        return m_alternatives;
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
    public final int hashCode()
    {
        return super.hashCode();
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
                "{0}{1} -> {2}{3}",
                m_isfragment ? "fragment " : "",
                this.id(),
                StringUtils.join( m_alternatives, " | " ),
                m_documentation.isEmpty() ? "" : "   // " + m_documentation
        );
    }
}
