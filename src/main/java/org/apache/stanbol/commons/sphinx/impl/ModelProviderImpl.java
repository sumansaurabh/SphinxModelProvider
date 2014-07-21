package org.apache.stanbol.commons.sphinx.impl;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.commons.sphinx.ModelProvider;
import org.apache.stanbol.commons.sphinx.model.BaseModel;
import org.apache.stanbol.commons.stanboltools.datafileprovider.DataFileProvider;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Model Provider for SpeechToText Engine, this provides Acoustic, Language and 
 * Dictionary model to the Engine. Currently InputStream provided by DataFileProvider 
 * is used to build a Temp file as Sphinx needs model file location to extract the
 * speech content and DataFileProvider service provides InputStream of data only.
 * 
 * @author Suman Saurabh
 *
 */
@Component(immediate=true)
@Service(value=ModelProviderImpl.class)
public class ModelProviderImpl implements ModelProvider{
	/**
     * The logger
     */
	
	
		
	
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Reference
    private DataFileProvider dataFileProvider;
    
    
    

    /**
    * Map holding the already built Model
    */
    protected Map<HashSet<String>, BaseModel> models = new HashMap<HashSet<String>,BaseModel>();

    
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
    
    
    
    
    
	@Override
	public BaseModel getModel(String language, HashSet<String> models, BaseModel modelType) {
		return initModel(modelType.getCustomModel(language,models),modelType);		
	}
	
	@Override
	public BaseModel getDefaultModel(String language, BaseModel modelType ) {
		return initModel(modelType.getDefaultModel(language),modelType);
	}
	/**
     * It return the model location to the SpeechToText Engine.
     * @param <T>
     * @return
     * @throws IllegalFormatException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
	private <T> T initModel(HashSet<String> name, BaseModel modelType) {
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
        	InputStream modelDataStream;
        	for(String modelname: name)
        	{
        		String m[]=modelname.split("[-]");
        		
        		try {
        			modelDataStream = lookupModelStream(modelname);
        		} catch (IOException e) {
        			log.debug("Unable to load Resource {} via the DataFileProvider",name);
        			return null;
        		}
        		if(modelDataStream == null){
            		System.out.println("Dict name = "+modelname);

        			log.debug("Unable to load Resource {} via the DataFileProvider",name);
        			return null;
        		}
        		try {
        			createTempResource(modelDataStream,m[1], modelType.toString());
        		}
        		catch(PrivilegedActionException e) {
        			log.debug("Privledeged Exception thrown on Acoustic Language Model", e);
        			return null;
        		}
        		modelType.setLocation(m[1]);
        	}
        	
            models.put(name, modelType);
            return (T) modelType;
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void createTempResource(final InputStream modelDataStream, String resourceName, String path) throws PrivilegedActionException
    {
    	System.out.println("Reosurce Location = "+resourceName+" path = "+path);
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
     * @return the stream or <code>null</code> if not found
     * @throws IOException an any error while opening the model file
     */
    protected InputStream lookupModelStream(final String modelName) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
                @Override
                public InputStream run() throws IOException {
                    return dataFileProvider.getInputStream(null, modelName,null);
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


    @Deactivate
    protected void deactivate(ComponentContext ctx){
        log.debug("deactivating {}",this.getClass().getSimpleName() );
        clearTempResource(); //clean up temp resources
    }
    public void clearTempResource()
    {
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
