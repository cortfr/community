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
package org.neo4j.kernel.impl.transaction;

import static java.lang.System.currentTimeMillis;
import static org.neo4j.test.TargetDirectory.forTest;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TransactionBuilder;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class TestRelaxedForce
{
    private static AbstractGraphDatabase db;
    
    @BeforeClass
    public static void setupDb()
    {
        db = new EmbeddedGraphDatabase( forTest( TestRelaxedForce.class ).directory( "d", true ).getAbsolutePath() );
    }
    
    @AfterClass
    public static void teardownDb()
    {
        db.shutdown();
    }
    
    @Test
    public void relaxedForce() throws Exception
    {
        long t = currentTimeMillis();
        for ( int i = 0; i < 100000; i++ )
        {
            TransactionBuilder builder = db.tx();
            if ( (i/10000)%2 == 1 ) builder = builder.relaxed();
            Transaction tx = builder.begin();
            Node node = db.createNode();
            node.setProperty( "name", "Mattias" );
            tx.success();
            tx.finish();
            if ( i % 1000 == 0 && i > 0 ) System.out.println( i );
        }
        System.out.println( (currentTimeMillis()-t) + "ms" );
    }
    
    @Test
    public void demo() throws Exception
    {
        {
            // Normal transaction
            Transaction tx = db.tx().begin();
            //               db.beginTx(); is so 2010
            // ...
        }        
        
        {
            // Transaction with relaxed force
            Transaction tx = db.tx().relaxed().begin();
        }
    }
}
