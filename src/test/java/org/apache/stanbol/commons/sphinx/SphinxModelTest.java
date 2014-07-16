package org.apache.stanbol.commons.sphinx;

import java.io.IOException;

import org.apache.stanbol.commons.sphinx.impl.ModelProviderImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author Suman Saurabh
 *
 */
public class SphinxModelTest {
	private static ModelProviderImpl MP;    
    @BeforeClass
    public static void init(){
    	MP = new ModelProviderImpl(new ClasspathDataFileProvider("DUMMY"));
    }
	
    
    @Test
    public void testLoadDefaultModel() throws IOException{
    	LanguageModel model = MP.getDefaultModel("en");
       	MP.clearTempResource();
        Assert.assertNotNull(model);
        
    }
    
    @Test
    public void testLoadCustomModel() throws IOException{
    	LanguageModel model = MP.getModel("org.apache.stanbol.data.sphinx.model.wsj","en");
    	MP.clearTempResource();
        Assert.assertNotNull(model);
    }
}
