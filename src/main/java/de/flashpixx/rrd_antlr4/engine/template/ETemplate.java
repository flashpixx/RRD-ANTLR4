package de.flashpixx.rrd_antlr4.engine.template;

/**
 * enum for template creation
 */
public enum ETemplate
{
    HTML,
    LATEX;

    /**
     * creates a new template
     */
    public ITemplate generate()
    {
        switch ( this )
        {
            case HTML:
                return new CHTML();

            case LATEX:
                return new CLaTeX();

            default:
                throw new IllegalStateException();
        }
    }
}
