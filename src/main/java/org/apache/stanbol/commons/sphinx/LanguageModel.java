package org.apache.stanbol.commons.sphinx;

import java.util.HashMap;
import java.util.Map;

public class LanguageModel{
    
    private Map<String,String> ModelLocation=new HashMap<String,String>();;

    //List<ArrayList<String>> ModelFiles;
    public LanguageModel(Map<String,String> ModelLocation) {
    	this.ModelLocation=ModelLocation;
    }
	public Map<String,String> getModelFiles() {
		return this.ModelLocation;
	}
	
	
}
