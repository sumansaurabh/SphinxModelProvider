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


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.commons.stanboltools.datafileprovider.DataFileProvider;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * * OSGI service that let you load Sphinx Models via the Stanbol 
 * {@link DataFileProvider} infrastructure. This allows users to copy models
 * to the 'datafiles' directory or developer to provide models via via OSGI
 * bundles.<p>
 * This service also provides methods that directly return the Sphinx component
 * wrapping the model.
 * Makes the Model file available to {@link SpeechToTextEngine}.
 * 
 * Currently InputStream provided by DataFileProvider is saved as 
 * model file in temp folder. Sphinx needs model file location to extract the
 * speech content and DataFileProvider service provides InputStream of data only.
 * 
 * @author Suman Saurabh
 *
 */
@Component(immediate=true)
@Service(value=ModelProviderImpl.class)
public class ModelProviderImpl{
	/**
     * The logger
     */
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Reference
    private DataFileProvider dataFileProvider;
    
    /**
    * Map holding the already built Model needed on cleaning the temp resources
    */
    protected Map<HashSet<String>, BaseModel> models = new HashMap<HashSet<String>,BaseModel>();
    
    private String bundleSymbolicName=null;
            //"org.apache.stanbol.sphinx.model.language"; //Getter for bundle symbolic name
    
    /**
     * Default constructor
     */
    public ModelProviderImpl(){ 
    	super();
    }
    /**
     * Constructor intended to be used when running outside an OSGI environment
     * (e.g. when used for UnitTests)
     * @param dataFileProvider the dataFileProvider used to load Model data.
     */
    public ModelProviderImpl(DataFileProvider dataFileProvider){
        this();
        this.dataFileProvider = dataFileProvider;
    }
    
    
    /**
	 * Initializes the Model files for the @langauge and {@link LanguageModel}, {@link AcousticModel}, {@link DictionaryModel}
	 * @param modelType {@link LanguageModel}, {@link AcousticModel}, {@link DictionaryModel}
	 * @return 
	 */
	public BaseModel getModel(HashSet<String> models, BaseModel modelType, String bundleSymbolicName) {
		this.bundleSymbolicName=bundleSymbolicName;
		return initModel(models,modelType);		
	}
	/**
	 * Initializes the Model files for the @models and @modeType { AcousticModel, LanguageModel, DictionaryModel }
         * @param language
	 * @param modelType {@link LanguageModel}, {@link AcousticModel}, {@link DictionaryModel}
	 * @return
	 */
	public BaseModel getDefaultModel(String language, BaseModel modelType ) {
               log.info("\n################load language {} via the DataFileProvider",language);

		return initModel(modelType.getDefaultModel(language),modelType);
	}
	
	    private static final String DOWNLOAD_ROOT = "http://opennlp.sourceforge.net/models-1.5/";

	/**
	 * Initializes the Model files i.e. storing the copy in /tmp folder
	 * @param name Model File name set
	 * @param modelType {@link LanguageModel}, {@link AcousticModel}, {@link DictionaryModel}
	 * @return {@link LanguageModel}, {@link AcousticModel}, {@link DictionaryModel}
	 */
    
    @SuppressWarnings("unchecked")
	private <T> T initModel(HashSet<String> name, BaseModel modelType) {
            log.info("\n################load model {} via the DataFileProvider",name);
            Map<String,String>modelProperties = new HashMap<String,String>();
    	Object model = models.get(name);
        if(model != null) {
            if(modelType.getClass().isAssignableFrom(model.getClass())){
                return (T) model;
            } 
            else {
                throw new IllegalStateException(String.format(
                    "Incompatible Model Types for name '%s': present=%s | requested=%s",
                    name,model.getClass(),modelType));
            }
        } 
        else {
                if(!modelProperties.containsKey("Description")){
                    modelProperties.put("Description", "Statistical model for Sphinx");
                }
                if(!modelProperties.containsKey("Model Type")){
                    modelProperties.put("Model Type", modelType.toString());
                }
                if(!modelProperties.containsKey("Download Location")){
                    modelProperties.put("Download Location", DOWNLOAD_ROOT+name);
                }
        	InputStream modelDataStream;
        	for(String modelname: name)
        	{
                        log.info("\n################load model {} via the DataFileProvider",modelname);

        		try {
        			//modelDataStream = lookupModelStream(modelname,modelProperties);
                                    System.out.println("Looking for model name = '"+modelname+"' and bundleprop ='"+bundleSymbolicName+"'");

                            modelDataStream=lookupModelStream(modelname,modelProperties);
        		} catch (IOException e) {
                                  System.out.println("Exception : Uable to load Resource via the DataFileProvider = '"+modelname+"' and bundlename ='"+bundleSymbolicName+"'");

        			log.debug("Unable to load Resource {} via the DataFileProvider",name);
        			return null;
        		}
        		if(modelDataStream == null){
                                System.out.println("NULL : Uable to load Resource via the DataFileProvider = '"+modelname+"' and bundlename ='"+bundleSymbolicName+"'");

        			log.debug("Unable to load Resource {} via the DataFileProvider",name);
        			return null;
        		}
        		try {
        			createTempResource(modelDataStream,modelname, modelType.toString());
        		}
        		catch(PrivilegedActionException e) {
        			log.debug("Privledeged Exception thrown on Acoustic Language Model", e);
        			return null;
        		}
        		modelType.setLocation(modelname);
        	}
        	
            models.put(name, modelType);
            return (T) modelType;
        }
    }
    /**
     * 
     * @param modelDataStream {@link InputStream} of the Model, received from {@link DataFileProvider} Service
     * @param resourceName Model File Name
     * @param path path to copy the @resourceName i.e. /tmp
     * @throws PrivilegedActionException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void createTempResource(final InputStream modelDataStream, String resourceName, String path) throws PrivilegedActionException
    {
	    final File resource = new File(path+"/"+resourceName);
	    
    	AccessController.doPrivileged(new PrivilegedAction() {
                @Override
		public Object run() {
			try{
				FileUtils.copyInputStreamToFile(modelDataStream, resource);
			}
			catch(IOException e) {
				log.debug("Unable to copy Resource {} to temp",resource.getAbsolutePath());
			}
			return null;
		}
         });  
    }
    
    /**
     * Lookup an Sphinx data file via the {@link #dataFileProvider}
     * @param modelName the name of the model
     * @param properties
     * @return the stream or <code>null</code> if not found
     * @throws IOException an any error while opening the model file
     */
    protected InputStream lookupModelStream(final String modelName,final Map<String,String> properties) throws IOException {
        log.debug("Looking for model name = '{}' and bundlename ='{}'",modelName,bundleSymbolicName);
        System.out.println("Looking for model name = '"+modelName+"' and bundlename ='"+bundleSymbolicName+"'");
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
                @Override
                public InputStream run() throws IOException {
                            return dataFileProvider.getInputStream(bundleSymbolicName, modelName,properties);
                }
            });
        } catch (PrivilegedActionException pae) {
            
            Exception e = pae.getException();
            if(e instanceof IOException){
                throw (IOException)e;
            } else {
                throw RuntimeException.class.cast(e);
            }
        }        
    }


    /**
     * For Clearing /tmp resource
     * @param ctx
     */
    @Deactivate
    public void deactivate(ComponentContext ctx){
        log.debug("deactivating {}",this.getClass().getSimpleName() );
        clearTempResource(); //clean up temp resources
    }
    
    /**
     * Remove the Model files when they become unavialbe to Stanbol
     * @param modelType {@link LanguageModel}, {@link AcousticModel}, {@link DictionaryModel} 
     */
    public void removeUnavailableResource(BaseModel modelType) {
    	File directory = new File(models.get(modelType).toString());
    	if(directory.exists()){
	           try{
	               delete(directory);
	           }catch(IOException e){
	        	   log.debug("Unable to clear temp Resource {} to temp");
	           }
		  }
    }
    /** 
     * For completely cleaning the tmp resource when {@link ModelProvider} bundle is deactivated
     */
    private void clearTempResource()
    {
    	if(models==null) {
    		log.debug("Already Deactiavted {}",this.getClass().getSimpleName() );
    		return ;
    	}
    	Iterator<HashSet<String>> keySetIterator = models.keySet().iterator();

    	while(keySetIterator.hasNext()){
    		  HashSet<String> key = keySetIterator.next();
    		  File directory = new File(models.get(key).toString());
    		  if(directory.exists()){
    	           try{
    	               delete(directory);
    	           }catch(IOException e){
    	        	   log.debug("Unable to clear temp Resource {} to temp");
    	           }
    		  }
    	}
    }
           
    private void delete(File file) throws IOException
    {
    	if(file.isDirectory()){
    		
    		//directory is empty, then delete it
    		if(file.list().length==0){
    		   file.delete();
    		   log.debug("Directory is deleted : " + file.getAbsolutePath());
 
    		}else{
    		   //list all the directory contents
        	   String files[] = file.list();
        	   for (String temp : files) {
        	      //construct the file structure
        	      File fileDelete = new File(file, temp);
        	     delete(fileDelete);
        	   }
        	   //check the directory again, if empty then delete it
        	   if(file.list().length==0){
           	     file.delete();
           	     log.debug("Directory is deleted : " + file.getAbsolutePath());
        	   }
    		}
 
    	}else{
    		//if file, then delete it
    		file.delete();
    		log.debug("File is deleted : " + file.getAbsolutePath());
    	}
    } 
}
