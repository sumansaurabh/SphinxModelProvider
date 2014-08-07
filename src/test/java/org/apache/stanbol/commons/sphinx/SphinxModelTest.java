/*******************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.apache.stanbol.commons.sphinx;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Suman Saurabh
 *
 */
public class SphinxModelTest {
	
	private static ModelProviderImpl MP;
    private static final Logger log = LoggerFactory.getLogger(SphinxModelTest.class);

    @Before
    public void init(){
    	MP = new ModelProviderImpl(new ClasspathDataFileProvider("DUMMY"));
    }
	
    
    @Test
    public void testDefaultLanguageModel() throws IOException{
    	log.info(">> Testing Default Language Model <<");
   	BaseModel modelType = new LanguageModel();
    	modelType=MP.getDefaultModel("en", modelType);
    	//MP.getDefaultModel("en", modelType);
//    	MP.clearTempResource();
    	Assert.assertEquals(modelType.getClass(), (new LanguageModel()).getClass());
        Assert.assertNotNull(modelType);
    }
    @Test
    public void testDefaultAcousticModel() throws IOException{
    	log.info(">> Testing Default Acoustic Model <<");
    	BaseModel modelType = new AcousticModel();
    	modelType=MP.getDefaultModel("en", modelType);
  //  	MP.clearTempResource();
    	Assert.assertEquals(modelType.getClass(), (new AcousticModel()).getClass());
        Assert.assertNotNull(modelType);
    }
    
    @Test
    public void testDefaultDictionaryModel() throws IOException{
    	log.info(">> Testing Default Dictionary Model <<");
    	BaseModel modelType = new DictionaryModel();
    	modelType=MP.getDefaultModel("en", modelType);
    //	MP.clearTempResource();
    	Assert.assertEquals(modelType.getClass(), (new DictionaryModel()).getClass());
        Assert.assertNotNull(modelType);
    }
    
    @Test
    public void testCustomLanguageModel() throws IOException{
    	log.info(">> Testing Custom Language Model <<");
    	BaseModel modelType = new LanguageModel();
		HashSet<String> modelName=new HashSet<String>();
		modelName.add("en-us.lm.dmp");

    	modelType=MP.getModel(modelName, modelType,null);    	
    	Assert.assertEquals(modelType.getClass(), (new LanguageModel()).getClass());
        Assert.assertNotNull(modelType);
    }
    @Test
    public void testCustomAcousticModel() throws IOException{
    	log.info(">> Testing Custom Acoustic Model <<");
    	String acousticResource[]={"feat.params", "mdef", "means", "mixture_weights", "noisedict", "transition_matrices", "variances","feature_transform"};
    	BaseModel modelType = new AcousticModel();
		HashSet<String> modelName=new HashSet<String>(Arrays.asList(acousticResource));
    	modelType=MP.getModel(modelName, modelType,null);//    	MP.clearTempResource();
    	Assert.assertEquals(modelType.getClass(), (new AcousticModel()).getClass());
        Assert.assertNotNull(modelType);
    }
    
    
    @Test
    public void testCustomDictionaryModel() throws IOException{
    	log.info(">> Testing Custom Dictionary Model <<");
    	BaseModel modelType = new DictionaryModel();
		HashSet<String> modelName=new HashSet<String>();
		modelName.add("en-cmu.dict"); //This test not passed

    	modelType=MP.getModel(modelName, modelType,null);
//    	MP.clearTempResource();
    	Assert.assertEquals(modelType.getClass(), (new DictionaryModel()).getClass());
        Assert.assertNotNull(modelType);
    }
    @After
    public void unbindServices() {
    	log.info(">> Cleaning resources <<");
       	MP.deactivate(null);
    	MP=null;
    }
	
}
