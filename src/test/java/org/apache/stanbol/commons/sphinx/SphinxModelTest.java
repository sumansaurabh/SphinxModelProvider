package org.apache.stanbol.commons.sphinx;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.stanbol.commons.sphinx.impl.ModelProviderImpl;
import org.apache.stanbol.commons.sphinx.model.AcousticModel;
import org.apache.stanbol.commons.sphinx.model.BaseModel;
import org.apache.stanbol.commons.sphinx.model.DictionaryModel;
import org.apache.stanbol.commons.sphinx.model.LanguageModel;
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
    public void testDefaultLanguageModel() throws IOException{
    	BaseModel modelType = new LanguageModel();
    	modelType=MP.getDefaultModel("en", modelType);
    	//MP.getDefaultModel("en", modelType);
//    	MP.clearTempResource();
    	Assert.assertEquals(modelType.getClass(), (new LanguageModel()).getClass());
        Assert.assertNotNull(modelType);
    }
    @Test
    public void testDefaultAcousticModel() throws IOException{
    	BaseModel modelType = new AcousticModel();
    	modelType=MP.getDefaultModel("en", modelType);
  //  	MP.clearTempResource();
    	Assert.assertEquals(modelType.getClass(), (new AcousticModel()).getClass());
        Assert.assertNotNull(modelType);
    }
    
    @Test
    public void testDefaultDictionaryModel() throws IOException{
    	BaseModel modelType = new DictionaryModel();
    	modelType=MP.getDefaultModel("en", modelType);
    //	MP.clearTempResource();
    	Assert.assertEquals(modelType.getClass(), (new DictionaryModel()).getClass());
        Assert.assertNotNull(modelType);
    }
    
    @Test
    public void testCustomLanguageModel() throws IOException{
    	BaseModel modelType = new LanguageModel();
		HashSet<String> modelName=new HashSet<String>();
		modelName.add("en-us.lm.dmp");

    	modelType=MP.getModel(modelName, modelType);    	//MP.getDefaultModel("en", modelType);
    	//MP.clearTempResource();
    	Assert.assertEquals(modelType.getClass(), (new LanguageModel()).getClass());
        Assert.assertNotNull(modelType);
    }
    @Test
    public void testCustomAcousticModel() throws IOException{
    	String acousticResource[]={"feat.params", "mdef", "means", "mixture_weights", "noisedict", "transition_matrices", "variances"};
    	BaseModel modelType = new AcousticModel();
		HashSet<String> modelName=new HashSet<String>(Arrays.asList(acousticResource));
    	modelType=MP.getModel(modelName, modelType);//    	MP.clearTempResource();
    	Assert.assertEquals(modelType.getClass(), (new AcousticModel()).getClass());
        Assert.assertNotNull(modelType);
    }
    
    @Test
    public void testCustomDictionaryModel() throws IOException{
    	BaseModel modelType = new DictionaryModel();
		HashSet<String> modelName=new HashSet<String>();
		modelName.add("en-digits.dict"); //This test not passed

    	modelType=MP.getModel(modelName, modelType);
//    	MP.clearTempResource();
    	Assert.assertEquals(modelType.getClass(), (new DictionaryModel()).getClass());
        Assert.assertNotNull(modelType);
    }
    
}
