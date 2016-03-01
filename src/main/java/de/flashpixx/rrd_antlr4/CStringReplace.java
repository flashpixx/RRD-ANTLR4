package de.flashpixx.rrd_antlr4;

/**
 * class for replacing string content
 */
public final class CStringReplace
{
    /**
     * string data
     */
    private String m_data;

    /**
     * ctor
     *
     * @param p_data input data
     */
    public CStringReplace( final String p_data )
    {
        m_data = p_data;
    }

    /**
     * replaces the content
     *
     * @param p_search source
     * @param p_replace target
     * @return object reference
     */
    public final CStringReplace replaceAll( final String p_search, final String p_replace )
    {
        m_data = m_data.replaceAll( p_search, p_replace );
        return this;
    }

    /**
     * replace a char sequence
     *
     * @param p_search search char sequence
     * @param p_replace replace char sequence
     * @return object reference
     */
    public final CStringReplace replace( final CharSequence p_search, final CharSequence p_replace )
    {
        m_data.replace( p_search, p_replace );
        return this;
    }

    /**
     * returns the data
     *
     * @return string
     */
    public final String get()
    {
        return m_data;
    }

    @Override
    public final int hashCode()
    {
        return m_data.hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return m_data.hashCode() == p_object.hashCode();
    }

    @Override
    public final String toString()
    {
        return m_data;
    }
}
