package org.apache.stanbol.enhancer.engines.speechtotext;

/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import static org.apache.stanbol.enhancer.servicesapi.helper.EnhancementEngineHelper.randomUUID;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.clerezza.rdf.core.UriRef;
import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.commons.sphinx.impl.ModelProviderImpl;
import org.apache.stanbol.enhancer.servicesapi.Blob;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.ContentItemFactory;
import org.apache.stanbol.enhancer.servicesapi.ContentSink;
import org.apache.stanbol.enhancer.servicesapi.EngineException;
import org.apache.stanbol.enhancer.servicesapi.EnhancementEngine;
import org.apache.stanbol.enhancer.servicesapi.InvalidContentException;
import org.apache.stanbol.enhancer.servicesapi.ServiceProperties;
import org.apache.stanbol.enhancer.servicesapi.impl.AbstractEnhancementEngine;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;

/**
 * EnhancementEngine based on Sphinx that converts the content of parsed 
 * content items to plain text. 
 * 
 * @author Suman Saurabh
 *
 */
@Component(immediate = true, metatype = true, inherit = true)
@Service
@Properties(value = {
    @Property(name = EnhancementEngine.PROPERTY_NAME, value = "Speech2Text"),
    @Property(name=SpeechToTextEngine.UNMAPPED_PROPERTIES,boolValue=SpeechToTextEngine.DEFAULT_UNMAPPED_PROPERTIES_STATE)

})
public class SpeechToTextEngine extends AbstractEnhancementEngine<RuntimeException,RuntimeException> 
implements EnhancementEngine, ServiceProperties {

    /**
     * Using slf4j for logging
     */
    private static final Logger log = LoggerFactory.getLogger(SpeechToTextEngine.class);

	
	public static final String UNMAPPED_PROPERTIES = "stanbol.engine.sphinx.mapping.unmapped";//need to work on this
    public static final boolean DEFAULT_UNMAPPED_PROPERTIES_STATE = false;
    protected static final Charset UTF8 = Charset.forName("UTF-8");

    private SphinxConfig config;
    
    /**
     * Include also properties without a namespace. Currently those are ignored
     */
    private boolean includeAllUnmappedProperties = false;
    /**
     * If <code>true</code> unmapped properties are added by using
     * <code>urn:sphinx.apache.org:sphinx:{property-name}</code> to the URI of the
     * contentItem.
     */
    private boolean includeUnmappedProperties;
    

    
    /**
     * The {@link ContentItemFactory} is used to create {@link Blob}s for the
     * plain text and XHTML version of the processed ContentItem
     */
    @Reference
    private ContentItemFactory ciFactory;
    
    /**
     * Used by the unit tests to init the {@link ContentItemFactory} outside
     * an OSGI environment.
     * @param cifactory
     */
    public SpeechToTextEngine() {}
    
    public SpeechToTextEngine(ContentItemFactory cifactory) {
        this.ciFactory = cifactory;
    }
    
    
    

    
    
   
    
    @Override
    protected void deactivate(ComponentContext ctx) throws RuntimeException {
        //this.config = null;
        try {
		super.deactivate(ctx);
	} catch (Exception e) {
            e.printStackTrace();
	}
    }
    
    /**
     * @return if and how (asynchronously) we can enhance a ContentItem
     */
    public int canEnhance(ContentItem ci) throws EngineException {
        // check if content is present
        try {
            if ((ci.getBlob() == null)
                    || (ci.getBlob().getStream().read() == -1)) {
                return CANNOT_ENHANCE;
            }
        } catch (IOException e) {
            log.error("Failed to get the text for "
                    + "enhancement of content: " + ci.getUri(), e);
            throw new InvalidContentException(this, ci, e);
        }
        // no reason why we should require to be executed synchronously
        return ENHANCE_ASYNC;
    }
    @Override
    protected void activate(ComponentContext ctx) throws ConfigurationException {
        try {
		super.activate(ctx);
            } catch (Exception e) {
                // TODO Auto-generated catch block
		e.printStackTrace();
            }
        //includeUnmappedProperties = getBoolean(ctx.getProperties(), UNMAPPED_PROPERTIES, DEFAULT_UNMAPPED_PROPERTIES_STATE);
    }
    
    @SuppressWarnings("deprecation")
	public void computeEnhancements(ContentItem ci) throws EngineException {
        config=new SphinxConfig(ci);
        List<String> resultPredicted;
        final InputStream in;

        Configuration configuration = config.getConfiguration();
        
        
        try {
            in = ci.getStream();
            //get the (generated or submitted) text version of the ContentItem
            StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
            recognizer.startRecognition(in);
            SpeechResult result;
            resultPredicted=new ArrayList<String>();
            while ((result = recognizer.getResult()) != null)
            {
                List<WordResult> wordlist=result.getWords();
            	String timeStamp="["+wordlist.get(0).getTimeFrame().getStart()+ " , "
            					+wordlist.get(wordlist.size()-2).getTimeFrame().getEnd()+"]";
            	resultPredicted.add(timeStamp+ " "+result.getHypothesis());
                //System.out.println(result.getHypothesis());
            }
            recognizer.stopRecognition();
        } catch (IOException ex) {
            log.error("Exception reading content item.", ex);
            throw new InvalidContentException("Exception reading content item.", ex);
        }
        //now add the Blob to the ContentItem
        ContentSink plainTextSink;
            try {
                plainTextSink = ciFactory.createContentSink("text/plain" +"; charset="+UTF8);

            } catch (IOException e) {
                IOUtils.closeQuietly(in); //close the input stream
                throw new EngineException("Error while initialising Blob for" +
                		"writing the text/plain version of the parsed content",e);
            }
            //final Writer plainTextWriter = new OutputStreamWriter(plainTextSink.getOutputStream(), UTF8);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(plainTextSink.getOutputStream(), UTF8));
            try
            { // parse the writer to the framework that extracts the text 
            	for(String str: resultPredicted)
            	out.write(str);
            } catch (IOException e) {
            	throw new EngineException("Unable to write extracted" +
                		"plain text to Blob (blob impl: "
                        + plainTextSink.getBlob().getClass()+")",e);
			}
            finally
            { IOUtils.closeQuietly(out); }
            
            String random = randomUUID().toString();
            UriRef textBlobUri = new UriRef("urn:Sphinx:text:"+random);//create an UriRef for the Blob
            ci.addPart(textBlobUri, plainTextSink.getBlob());
            
            
            plainTextSink = null;
            
            ci.getLock().writeLock().lock();
            /*
            try {
                MGraph graph = ci.getMetadata();
                System.out.println("--------------------------------------------------"+ci.getMetadata());

                UriRef id = ci.getUri();
                Set<String> mapped = ontologyMappings.apply(graph, id, metadata);
                if(includeUnmappedProperties){
                    Set<String> unmapped = new HashSet<String>(Arrays.asList(metadata.names()));
                    unmapped.removeAll(mapped);
                    for(String name : unmapped){
                        if(name.indexOf(':') >=0 || includeAllUnmappedProperties){ //only mapped
                            UriRef prop = new UriRef(new StringBuilder(TIKA_URN_PREFIX).append(name).toString());
                            for(String value : metadata.getValues(name)){
                                //TODO: without the Property for the name we have no datatype
                                //      information ... so we add PlainLiterals for now
                                graph.add(new TripleImpl(id, prop, new PlainLiteralImpl(value)));
                            }
                        }
                    }
                }
            }finally{
                ci.getLock().writeLock().unlock();
            }
            */
        }

   
   
    
    
    
    /**
     * ServiceProperties are currently only used for automatic ordering of the 
     * execution of EnhancementEngines (e.g. by the WeightedChain implementation).
     * ORDERING_PRE_PROCESSING: All values >= 200 are considered for engines that
     * do some kind of preprocessing of the content. This includes e.g. the 
     * conversion of media formats such as extracting the plain text from HTML, 
     * keyframes from videos, wave form from mp3 ...; extracting metadata directly 
     * encoded within the parsed content such as ID3 tags from MP3 or RDFa, 
     * microdata provided by HTML content.
     * use a value < {@link ServiceProperties#ORDERING_PRE_PROCESSING}
     * and >= {@link ServiceProperties#ORDERING_PRE_PROCESSING}.
     */
    public Map<String, Object> getServiceProperties() {
        return Collections.unmodifiableMap(Collections.singletonMap(
                ENHANCEMENT_ENGINE_ORDERING, (Object)ORDERING_PRE_PROCESSING));
    }
}
