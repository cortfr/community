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
package org.neo4j.server.helpers;

import java.io.IOException;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.server.NeoServer;
import org.neo4j.tooling.GlobalGraphOperations;

public class ServerHelper
{
//    private static final NeoServer SERVER;
//    static
//    {
//        try
//        {
//            SERVER = unstoppable( ServerBuilder.server().onPort( 7474 ).build() );
//            SERVER.start();
//        }
//        catch ( IOException e )
//        {
//            throw new Error( "Couldn't start default server", e );
//        }
//    }
    
    public static void cleanTheDatabase( final NeoServer server )
    {
        if ( server == null )
        {
            return;
        }

        new Transactor( server.getDatabase().graph, new UnitOfWork()
        {

            @Override
            public void doWork()
            {
                deleteAllNodesAndRelationships( server );

                deleteAllIndexes( server );
            }

            private void deleteAllNodesAndRelationships( final NeoServer server )
            {
                Iterable<Node> allNodes = GlobalGraphOperations.at( server.getDatabase().graph ).getAllNodes();
                for ( Node n : allNodes )
                {
                    Iterable<Relationship> relationships = n.getRelationships();
                    for ( Relationship rel : relationships )
                    {
                        rel.delete();
                    }
                    if ( n.getId() != 0 )
                    { // Don't delete the reference node - tests depend on it
                      // :-(
                        n.delete();
                    }
                    else
                    { // Remove all state from the reference node instead
                        for ( String key : n.getPropertyKeys() )
                        {
                            n.removeProperty( key );
                        }
                    }
                }
            }

            private void deleteAllIndexes( final NeoServer server )
            {
                for ( String indexName : server.getDatabase().graph.index()
                        .nodeIndexNames() )
                {
                	try{
	                    server.getDatabase().graph.index()
	                            .forNodes( indexName )
	                            .delete();
                	} catch(UnsupportedOperationException e) {
                		// Encountered a read-only index.
                	}
                }

                for ( String indexName : server.getDatabase().graph.index()
                        .relationshipIndexNames() )
                {
                	try {
	                    server.getDatabase().graph.index()
	                            .forRelationships( indexName )
	                            .delete();
                	} catch(UnsupportedOperationException e) {
                		// Encountered a read-only index.
                	}
                }
            }
        } ).execute();
    }
    
    public static NeoServer createServer() throws IOException
    {
        return createServer( false );
    }
    
    public static NeoServer createServer( boolean persistent ) throws IOException
    {
        ServerBuilder builder = ServerBuilder.server();
        if ( persistent ) builder = builder.persistent();
        NeoServer server = builder.build();
        server.start();
        return server;
    }
}
