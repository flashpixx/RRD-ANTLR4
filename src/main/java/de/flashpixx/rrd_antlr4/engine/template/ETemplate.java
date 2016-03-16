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

package de.flashpixx.rrd_antlr4.engine.template;

import de.flashpixx.rrd_antlr4.CCommon;


/**
 * enum for template creation
 */
public enum ETemplate
{
    HTML,
    LATEXSYNTAX,
    LATEXDIAGRAM;

    /**
     * creates a new template
     */
    public ITemplate generate()
    {
        switch ( this )
        {
            case HTML:
                return new CHTML( HTML.toString() );

            case LATEXSYNTAX:
                return new CLaTeXSyntax( LATEXSYNTAX.toString() );

            case LATEXDIAGRAM:
                return new CLaTeXDiagram( LATEXDIAGRAM.toString() );

            default:
                throw new IllegalStateException( CCommon.getLanguageString( this, "unknowntype" ) );
        }
    }
}
