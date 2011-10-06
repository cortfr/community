/**
 * Copyright (c) 2002-2011 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.impl.transaction.xaframework;

import java.io.IOException;

public enum ForceMode
{
    force
    {
        @Override
        public void force( LogBuffer buffer ) throws IOException
        {
            buffer.force();
        }
    },
    write
    {
        @Override
        public void force( LogBuffer buffer ) throws IOException
        {
            buffer.writeOut();
        }
    };
    
    public static ForceMode selected;
    static
    {
        String property = System.getProperty( ForceMode.class.getName() );
        selected = property == null ? force : valueOf( property );
        System.out.println( "ForceMode:" + selected );
    }
    
    public abstract void force( LogBuffer buffer ) throws IOException;
}
