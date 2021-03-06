/*
 * #%L
 * Alfresco Solr 4
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.solr.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;


@RunWith(MockitoJUnitRunner.class)
public class TempFileWarningLoggerTest
{
    private @Mock Logger log;
    private Path path;
    
    @Before
    public void setUp()
    {
        path = Paths.get(System.getProperty("java.io.tmpdir"));
        
        // Simulate warn-level logging
        Mockito.when(log.isWarnEnabled()).thenReturn(true);
    }
    
    @Test
    public void checkGlobBuiltCorrectly()
    {
        TempFileWarningLogger warner = 
                    new TempFileWarningLogger(
                                log,
                                "MyPrefix*",
                                new String[] { "temp", "remove-me", "~notrequired" },
                                path);
        
        assertEquals("MyPrefix*.{temp,remove-me,~notrequired}", warner.getGlob());
    }
    
    @Test
    public void checkFindFiles() throws IOException
    {
        File f = File.createTempFile("MyPrefix", ".remove-me", path.toFile());
        f.deleteOnExit();
        
        try
        {
            TempFileWarningLogger warner = 
                        new TempFileWarningLogger(
                                    log,
                                    "MyPrefix*",
                                    new String[] { "temp", "remove-me", "~notrequired" },
                                    path);
            
            boolean found = warner.checkFiles();
            
            assertTrue("Should have found matching files", found);
            // Should be a warn-level log message.
            Mockito.verify(log, never()).warn(Mockito.anyString());
        }
        finally
        {
            f.delete();
        }
    }
    
    
    @Test
    public void checkWhenNoFilesToFind() throws IOException
    {
        File f = new File(path.toFile(), "TestFile.random");
        
        // It would be very odd if this file exists!
        assertFalse("Unable to perform test as file exists: " + f, f.exists());
                
        TempFileWarningLogger warner = 
                    new TempFileWarningLogger(
                                log,
                                "TestFile",
                                new String[] { "random" },
                                path);
        
        boolean found = warner.checkFiles();
        
        assertFalse("Should NOT have found matching file", found);
        // Should be no warn-level log message.
        Mockito.verify(log, Mockito.never()).warn(Mockito.anyString());
    }    
    @Test
    public void removeManyFiles() throws IOException 
    {
        File f = File.createTempFile("WFSTInputIterator", ".input", path.toFile());
        File f2 = File.createTempFile("WFSTInputIterator", ".sorted", path.toFile());
        File f3 = File.createTempFile("WFSTInputIterator12324", ".sorted", path.toFile());
        File f4 = File.createTempFile("WFSTInputIterator12335555", ".input", path.toFile());
        f.deleteOnExit();
        f2.deleteOnExit();
        
        TempFileWarningLogger warner = new TempFileWarningLogger(log,
                                                                 "WFSTInputIterator*",
                                                                 new String[] { "input", "sorted" },
                                                                 path);

        
        boolean found = warner.checkFiles();
        assertTrue("Should have found matching file", found);
        assertTrue(f.exists());
        assertTrue(f2.exists());
        assertTrue(f3.exists());
        assertTrue(f4.exists());
        if(found)
        {
            warner.removeFiles();
        }
        assertFalse(f.exists());
        assertFalse(f2.exists());
        assertFalse(f3.exists());
        assertFalse(f4.exists());
        
        boolean found2 = warner.checkFiles();
        assertFalse("Should NOT have found a matching file", found2);
        
    }
    @Test
    public void notToRemoveFilesThatDontMatch() throws IOException 
    {
        File f = File.createTempFile("someotherfile", ".input", path.toFile());
        File f2 = File.createTempFile("someotherfile", ".sorted", path.toFile());
        f.deleteOnExit();
        f2.deleteOnExit();
        
        TempFileWarningLogger warner = new TempFileWarningLogger(log,
                                                                 "WFSTInputIterator*",
                                                                 new String[] { "input", "sorted" },
                                                                 path);

        
        assertTrue(f.exists());
        assertTrue(f2.exists());
        warner.removeFiles();
        assertTrue(f.exists());
        assertTrue(f2.exists());
        
        boolean found2 = warner.checkFiles();
        assertFalse("Should NOT have found a matching file", found2);
        
    }
}
