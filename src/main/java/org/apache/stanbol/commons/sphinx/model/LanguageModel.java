package org.apache.stanbol.commons.sphinx.model;

import java.util.HashSet;

public class LanguageModel implements BaseModel{
    
   
    private String ModelLocation=System.getProperty("java.io.tmpdir");
    
	@Override
	public HashSet<String> getDefaultModel(String language) {
		HashSet<String> modelName=new HashSet<String>();
		modelName.add(String.format("%s-us.lm.dmp", language));
		return modelName;
	}
	@Override
	public void setLocation(String location) {
		ModelLocation=ModelLocation+"/"+location;		
	}
	
	@Override
	public HashSet<String> getCustomModel(String language, HashSet<String> models) {
		HashSet<String> modelName=new HashSet<String>();
		for(String m: models)
			modelName.add(String.format("%s-%s", language,m));
		return modelName;
	}
	public String toString() {
		return ModelLocation;
    }
}
