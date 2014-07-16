package org.apache.stanbol.commons.sphinx.impl;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.commons.sphinx.LanguageModel;
import org.apache.stanbol.commons.sphinx.ModelProvider;
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
    
    final private String tempPath = System.getProperty("java.io.tmpdir");
    
    
    private String BundelName="org.apache.stanbol.data.sphinx.model.wsj";

    /**
    * Map holding the already built Model
    */
    private Map<String,String> ModeLocation = new HashMap<String,String>();

    
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
	public LanguageModel getDefaultModel(String language) {
		// TODO Auto-generated method stub
    	return initModel( language);
		
	}
	@Override
	public LanguageModel getModel(String bundlename, String language) {
		// TODO Auto-generated method stub
		
		this.BundelName=bundlename;
		return initModel( language);
	}
    /**
     * It return the model location to the SpeechToText Engine.
     * @param <T>
     * @return
     * @throws IllegalFormatException
     * @throws IOException
     */
    private LanguageModel initModel(String language) {
    	if(initAcousticModel()) {
    		if(initLanguageModel(language)) {
    			if(initDictionary(language))
    				return (new LanguageModel(ModeLocation));
    		}
    	}
    	return null;
    }
    
    
    
    /**
     * Builds Acoustic model files to temp directory.
     * @return
     */
    private boolean initAcousticModel()
    {
    	String acousticResource[]={"feat.params", "mdef", "means", "mixture_weights", "noisedict", "transition_matrices", "variances"};
    	InputStream modelDataStream = null;
    	String tempPath=this.tempPath+"/acoustic";
    	new File(tempPath).mkdir(); //this needs to be handled
    	for(String resource : acousticResource ) {
    		try {
    			modelDataStream = lookupModelStream(resource);
    		}
    		catch (IOException e) {
                log.debug("Unable to load Resource {} via the DataFileProvider",resource);
                return false;
    		}
    		if(modelDataStream == null){
                log.debug("Unable to load Resource {} via the DataFileProvider",resource);
                return false;
            }
    		try {
    	    	
    			createTempResource(modelDataStream,resource,tempPath);
    		}
    		catch(PrivilegedActionException e) {
    			log.debug("Privledeged Exception thrown on Acoustic Language Model", e);
    			return false;
    		}
    		finally {
    			IOUtils.closeQuietly(modelDataStream);
    		}
    	}
        ModeLocation.put("acoustic", tempPath);
        return true;
    }
    
    /**
     * Builds Language Model file to temp directory. 
     * @return
     */
    private boolean initLanguageModel(String language)
    {
    	
    	InputStream modelDataStream;
    	String resource=language+"-us.lm.dmp";
    	try {
			modelDataStream = lookupModelStream(resource);    			
		}
		catch (IOException e) {
            log.debug("Unable to load Resource {} via the DataFileProvider",resource);
            return false;
        }
    	
		if(modelDataStream == null){
            log.debug("Unable to load Resource {} via the DataFileProvider",resource);
            return false;
        }
		try {
			createTempResource(modelDataStream,resource,this.tempPath);
		}
		catch(PrivilegedActionException e) {
			log.debug("Privledeged Exception thrown on building Language Model", e);
            return false;
		}
		finally {
			IOUtils.closeQuietly(modelDataStream);
		}
    	ModeLocation.put("language", this.tempPath+"/"+resource);
    	return true;
    }
    
    /**
     * Builds Dictionary file to temp directory.
     * @return
     */
    private boolean initDictionary(String language)
    {
    	InputStream modelDataStream;
    	String resource=language+"-cmudict.0.6d";
    	try {
			modelDataStream = lookupModelStream(resource);    			
		}
		catch (IOException e) {
            log.debug("Unable to load Resource {} via the DataFileProvider",resource);
            return false;
        }
		if(modelDataStream == null){
            log.debug("Unable to load Resource {} via the DataFileProvider",resource);
            return false;
        }
		try{
			createTempResource(modelDataStream,resource,this.tempPath);
		}catch(PrivilegedActionException e) {
			log.debug("Privledeged Exception thrown on building Dictionary Model", e);
            return false;
		}
		finally {
			IOUtils.closeQuietly(modelDataStream);
		}
		
    	ModeLocation.put("dictionary", this.tempPath+"/"+resource);
        return true;
    }
    /**
     * Deletes the Temp resources when model files are not required. 
     * @param modelDataStream InputStream of the model file
     * @param resourceName name of the model file
     * @param path to the model files location
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
    
    /***********************************************************************************************************************************************/
    @Deactivate
    protected void deactivate(ComponentContext ctx){
        log.debug("deactivating {}",this.getClass().getSimpleName() );
        clearTempResource(); //clean up temp resources
    }
    public void clearTempResource()
    {
    	Iterator<String> keySetIterator = ModeLocation.keySet().iterator();
    	while(keySetIterator.hasNext()){
    		  String key = keySetIterator.next();
    		  File directory = new File(ModeLocation.get(key));
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
    /**********************************************************************************************************************************************/	
    
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
                    return dataFileProvider.getInputStream(BundelName, modelName,null);
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
	
    
}
