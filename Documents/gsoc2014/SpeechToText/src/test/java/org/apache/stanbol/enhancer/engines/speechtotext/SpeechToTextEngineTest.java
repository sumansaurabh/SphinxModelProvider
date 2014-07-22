package org.apache.stanbol.enhancer.engines.speechtotext;

import static java.util.Collections.singleton;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.stanbol.enhancer.servicesapi.EnhancementEngine.CANNOT_ENHANCE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.clerezza.rdf.core.Literal;
import org.apache.clerezza.rdf.core.LiteralFactory;
import org.apache.clerezza.rdf.core.NonLiteral;
import org.apache.clerezza.rdf.core.PlainLiteral;
import org.apache.clerezza.rdf.core.Resource;
import org.apache.clerezza.rdf.core.Triple;
import org.apache.clerezza.rdf.core.TypedLiteral;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.ontologies.RDF;
import org.apache.clerezza.rdf.ontologies.XSD;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.stanbol.enhancer.contentitem.inmemory.InMemoryContentItemFactory;
import org.apache.stanbol.enhancer.engines.tika.TikaEngineTest;
import org.apache.stanbol.enhancer.servicesapi.Blob;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.ContentItemFactory;
import org.apache.stanbol.enhancer.servicesapi.EngineException;
import org.apache.stanbol.enhancer.servicesapi.helper.ContentItemHelper;
import org.apache.stanbol.enhancer.servicesapi.impl.StreamSource;
import org.apache.stanbol.enhancer.servicesapi.rdf.NamespaceEnum;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.service.cm.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeechToTextEngineTest {
	
	 private static final Logger log = LoggerFactory.getLogger(SpeechToTextEngineTest.class);
	 private static final ContentItemFactory ciFactory = InMemoryContentItemFactory.getInstance();
	 private static SpeechToTextEngine engine;
	 private static MockComponentContext context;
	 private static LiteralFactory lf = LiteralFactory.getInstance();
	 
	 private static SimpleDateFormat dateDefaultTimezone =
	            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", new DateFormatSymbols(Locale.US));

	 @BeforeClass
	 public static void setUpServices() throws IOException {
		 context = new MockComponentContext();
	     context.properties.put(SpeechToTextEngine.PROPERTY_NAME, "tika");
	     //to test unmapped properties
	     context.properties.put(SpeechToTextEngine.UNMAPPED_PROPERTIES, "true");
	 }
	 
	 @Before
	 public void bindServices() throws ConfigurationException {
		 if(engine == null){
			 engine = new SpeechToTextEngine(ciFactory);
	         engine.activate(context);
	     }
	 }
	 @Test 
	 public void testWav() throws EngineException, IOException, ParseException {
		 log.info(">>> Sphinix Testing WAV  <<<");
	     ContentItem ci = createContentItem("test1.wav", "audio/wav");
	     assertFalse(engine.canEnhance(ci) == CANNOT_ENHANCE);
	     engine.computeEnhancements(ci);
	     Entry<UriRef,Blob> contentPart = ContentItemHelper.getBlob(ci, singleton("text/plain"));
	     assertNotNull(contentPart);
	     Blob plainTextBlob = contentPart.getValue();
	     assertNotNull(plainTextBlob);
	     assertContentRegexp(plainTextBlob, 
	    		 "Test Title",
	    		 "Test Artist",
	    		 "Test Album");
	        //validate XHTML results
	     contentPart = ContentItemHelper.getBlob(ci, 
	     singleton("application/xhtml+xml"));
	     assertNotNull(contentPart);
	     Blob xhtmlBlob = contentPart.getValue();
	     assertNotNull(xhtmlBlob);
	        //Test AudioTrack metadata
	     NonLiteral audioTrack = verifyNonLiteral(ci, new UriRef(NamespaceEnum.media+"hasTrack"));
	        //types
	     verifyValues(ci, audioTrack, RDF.type, 
	    		 new UriRef(NamespaceEnum.media+"MediaFragment"),
	    		 new UriRef(NamespaceEnum.media+"Track"),
	    		 new UriRef(NamespaceEnum.media+"AudioTrack"));
	        //	properties
	     verifyValue(ci, audioTrack, new UriRef(NamespaceEnum.media+"hasFormat"), XSD.string, "Stereo");
	     verifyValue(ci, audioTrack, new UriRef(NamespaceEnum.media+"samplingRate"), XSD.int_, "44100");
	     verifyValue(ci, audioTrack, new UriRef(NamespaceEnum.media+"hasCompression"), XSD.string, "M4A");
	 }
	 

	    private ContentItem createContentItem(String resourceName, String contentType) throws IOException {
	        InputStream in = SpeechToTextEngineTest.class.getClassLoader().getResourceAsStream(resourceName);
	        assertNotNull(in);
	        return ciFactory.createContentItem(new StreamSource(in,contentType));
	    }
	    /**
	     * Tests if the parsed regex pattern are contained in any line of the parsed
	     * test
	     * @throws IOException 
	     */
	    public void assertContentRegexp(Blob blob, String... regexp) throws IOException {
	        Charset charset;
	        if(blob.getParameter().containsKey("charset")){
	            charset = Charset.forName(blob.getParameter().get("charset"));
	        } else {
	            charset = Charset.defaultCharset();
	        }
	        Reader reader = null;
	        nextPattern:
	        for (String expr : regexp) {
	            if(reader != null){
	                closeQuietly(reader);
	            }
	            final Pattern p = Pattern.compile(".*" + expr + ".*");
	            reader = new InputStreamReader(blob.getStream(), charset);
	            final LineIterator it = new LineIterator(reader);
	            while (it.hasNext()) {
	                final String line = it.nextLine();
	                if (p.matcher(line).matches()) {
	                    continue nextPattern;
	                }
	            }
	            fail(this + ": no match for regexp '" + expr + "', content=\n" + 
	                    IOUtils.toString(blob.getStream(), charset.toString()));
	        }
	    }
	    @After
	    public void unbindServices() {/*nothing to do */}

	    @AfterClass
	    public static void shutdownServices() {
	        engine.deactivate(context);
	        engine = null;
	    }

	    /*
	     * Internal helper methods 
	     */
	    private NonLiteral verifyNonLiteral(ContentItem ci, UriRef property){
	        return verifyNonLiteral(ci, ci.getUri(), property);
	    }
	    private static NonLiteral verifyNonLiteral(ContentItem ci, UriRef subject, UriRef property){
	        Iterator<Triple> it = ci.getMetadata().filter(subject,property, null);
	        assertTrue(it.hasNext());
	        Resource r = it.next().getObject();
	        assertFalse(it.hasNext());
	        assertTrue(r instanceof NonLiteral);
	        return (NonLiteral)r;
	    }
	    private static UriRef verifyValue(ContentItem ci, UriRef property, UriRef value){
	        return verifyValue(ci, ci.getUri(), property, value);
	    }
	    private static UriRef verifyValue(ContentItem ci, NonLiteral subject, UriRef property, UriRef value){
	        Iterator<Triple> it = ci.getMetadata().filter(subject,property, null);
	        assertTrue(it.hasNext());
	        Resource r = it.next().getObject();
	        assertFalse(it.hasNext());
	        assertTrue(r instanceof UriRef);
	        assertEquals(value,r);
	        return (UriRef)r;
	   }
	    private static Literal verifyValue(ContentItem ci, UriRef property, UriRef dataType, String lexValue) throws ParseException{
	        return verifyValue(ci, ci.getUri(), property, dataType, lexValue);
	    }
	    private static Literal verifyValue(ContentItem ci, NonLiteral subject, UriRef property, UriRef dataType, String lexValue) throws ParseException{
	        Iterator<Triple> it = ci.getMetadata().filter(subject,property, null);
	        assertTrue(it.hasNext());
	        Resource r = it.next().getObject();
	        assertFalse(it.hasNext());
	        if(dataType == null){
	            assertTrue(r instanceof PlainLiteral);
	        } else {
	            assertTrue(r instanceof TypedLiteral);
	            assertEquals(dataType, ((TypedLiteral)r).getDataType());
	        }
	        //if we check dates and the lexical value is not UTC than we need to
	        //consider the time zone of the host running this test
	        if(XSD.dateTime.equals(dataType) && lexValue.charAt(lexValue.length()-1) != 'Z'){
	            Date expectedDate = dateDefaultTimezone.parse(lexValue);
	            assertEquals(expectedDate, lf.createObject(Date.class, ((TypedLiteral)r)));
	        } else {
	            assertEquals(lexValue,((Literal)r).getLexicalForm());
	        }
	        return (Literal)r;
	    }
	    private static Set<Literal> verifyValues(ContentItem ci, UriRef property, UriRef dataType, String...lexValues){
	        return verifyValues(ci, ci.getUri(), property, dataType, lexValues);
	    }
	    private static Set<Literal> verifyValues(ContentItem ci, NonLiteral subject, UriRef property, UriRef dataType, String...lexValues){
	        Iterator<Triple> it = ci.getMetadata().filter(subject,property, null);
	        assertTrue(it.hasNext());
	        Set<String> expected = new HashSet<String>(Arrays.asList(lexValues));
	        Set<Literal> found = new HashSet<Literal>(expected.size());
	        while(it.hasNext()){
	            Resource r = it.next().getObject();
	            if(dataType == null){
	                assertTrue(r instanceof PlainLiteral);
	            } else {
	                assertTrue(r instanceof TypedLiteral);
	                assertEquals(dataType, ((TypedLiteral)r).getDataType());
	            }
	            assertTrue(expected.remove(((Literal)r).getLexicalForm()));
	            found.add((Literal)r);
	        }
	        return found;
	    }
	    private static Set<NonLiteral> verifyValues(ContentItem ci, NonLiteral subject, UriRef property, NonLiteral...references){
	        Iterator<Triple> it = ci.getMetadata().filter(subject,property, null);
	        assertTrue(it.hasNext());
	        Set<NonLiteral> expected = new HashSet<NonLiteral>(Arrays.asList(references));
	        Set<NonLiteral> found = new HashSet<NonLiteral>(expected.size());
	        while(it.hasNext()){
	            Resource r = it.next().getObject();
	            assertTrue(r instanceof NonLiteral);
	            assertTrue(expected.remove(r));
	            found.add((NonLiteral)r);
	        }
	        return found;
	    }

	    
}
