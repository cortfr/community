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
package org.neo4j.kernel.impl.nioneo.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.internal.matchers.StringContains.containsString;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.kernel.CommonFactories;
import org.neo4j.kernel.IdGeneratorFactory;
import org.neo4j.kernel.impl.storemigration.StoreMigrator;
import org.neo4j.kernel.impl.util.FileUtils;

public class StoreVersionTest
{
    @Test
    public void allStoresShouldHaveTheCurrentVersionIdentifier() throws IOException
    {
        File outputDir = new File( "target/var/" + StoreVersionTest.class.getSimpleName() );
        FileUtils.deleteRecursively( outputDir );
        assertTrue( outputDir.mkdirs() );
        String storeFileName = new File( outputDir, NeoStore.DEFAULT_NAME ).getPath();

        HashMap config = new HashMap();
        config.put( IdGeneratorFactory.class, CommonFactories.defaultIdGeneratorFactory() );
        config.put( FileSystemAbstraction.class, CommonFactories.defaultFileSystemAbstraction() );
        config.put( "neo_store", storeFileName );

        NeoStore.createStore( storeFileName, config );
        NeoStore neoStore = new NeoStore( config );

        CommonAbstractStore[] stores = {
                neoStore.getNodeStore(),
                neoStore.getRelationshipStore(),
                neoStore.getRelationshipTypeStore(),
                neoStore.getPropertyStore(),
                neoStore.getPropertyStore().getIndexStore()
        };

        for ( CommonAbstractStore store : stores )
        {
            assertThat( store.getTypeAndVersionDescriptor(), containsString( CommonAbstractStore.ALL_STORES_VERSION ) );
        }
        neoStore.close();
    }

    @Test
    @Ignore
    public void shouldFailToCreateAStoreContainingOldVersionNumber() throws IOException
    {
        File outputDir = new File( "target/var/" + StoreVersionTest.class.getSimpleName() );
        FileUtils.deleteRecursively( outputDir );
        assertTrue( outputDir.mkdirs() );

        URL legacyStoreResource = StoreMigrator.class.getResource( "legacystore/exampledb/neostore.nodestore.db" );
        File workingFile = new File( outputDir, "neostore.nodestore.db" );
        FileUtils.copyFile( new File( legacyStoreResource.getFile() ), workingFile );

        HashMap config = new HashMap();
        config.put( IdGeneratorFactory.class, CommonFactories.defaultIdGeneratorFactory() );
        config.put( FileSystemAbstraction.class, CommonFactories.defaultFileSystemAbstraction() );

        try {
            new NodeStore( workingFile.getPath(), config );
            fail( "Should have thrown exception" );
        } catch ( NotCurrentStoreVersionException e ) {
            //expected
        }
    }

    @Test
    public void neoStoreHasCorrectStoreVersionField() throws IOException
    {
        File outputDir = new File( "target/var/"
                                   + StoreVersionTest.class.getSimpleName()
                                   + "test2" );
        FileUtils.deleteRecursively( outputDir );
        assertTrue( outputDir.mkdirs() );

        String storeFileName = new File( outputDir, NeoStore.DEFAULT_NAME ).getPath();

        HashMap config = new HashMap();
        config.put( IdGeneratorFactory.class,
                CommonFactories.defaultIdGeneratorFactory() );
        config.put( FileSystemAbstraction.class,
                CommonFactories.defaultFileSystemAbstraction() );
        config.put( "neo_store", storeFileName );

        NeoStore.createStore( storeFileName, config );
        NeoStore neoStore = new NeoStore( config );
        // The first checks the instance method, the other the public one
        assertEquals( CommonAbstractStore.ALL_STORES_VERSION,
                NeoStore.versionLongToString( neoStore.getStoreVersion() ) );
        assertEquals( CommonAbstractStore.ALL_STORES_VERSION,
                NeoStore.versionLongToString( NeoStore.getStoreVersion( storeFileName ) ) );
    }

    @Test
    public void testProperEncodingDecodingOfVersionString()
    {
        String[] toTest = new String[] { "123", "foo", "0.9.9", "v0.A.0",
                "bar", "chris", "1234567" };
        for ( String string : toTest )
        {
            assertEquals(
                    string,
                    NeoStore.versionLongToString( NeoStore.versionStringToLong( string ) ) );
        }
    }
}
