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
package org.neo4j.kernel.impl.util;

public class RelIdArrayWithLoops extends RelIdArray
{
    private IdBlock lastLoopBlock;
    
    public RelIdArrayWithLoops()
    {
    }
    
    protected RelIdArrayWithLoops( RelIdArray from )
    {
        super( from );
        lastLoopBlock = from.getLastLoopBlock();
    }

    @Override
    protected IdBlock getLastLoopBlock()
    {
        return this.lastLoopBlock;
    }

    @Override
    protected void setLastLoopBlock( IdBlock block )
    {
        this.lastLoopBlock = block;
    }
    
    @Override
    public RelIdArray upgradeIfNeeded( RelIdArray capabilitiesToMatch )
    {
        return this;
    }
    
    @Override
    public RelIdArray downgradeIfPossible()
    {
        return lastLoopBlock == null ? new RelIdArray( this ) : this;
    }
    
    @Override
    public RelIdArray addAll( RelIdArray source )
    {
        if ( source == null )
        {
            return this;
        }
        append( source, DirectionWrapper.OUTGOING );
        append( source, DirectionWrapper.INCOMING );
        append( source, DirectionWrapper.BOTH );
        return this;
    }

    public RelIdArray newSimilarInstance()
    {
        return new RelIdArrayWithLoops();
    }
    
    @Override
    public boolean supportsLoops()
    {
        return true;
    }
}
