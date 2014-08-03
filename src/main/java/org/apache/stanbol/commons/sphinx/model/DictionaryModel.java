package org.apache.stanbol.commons.sphinx.model;

import java.util.HashSet;

public class DictionaryModel implements BaseModel{
	
	private String ModelLocation=System.getProperty("java.io.tmpdir");
	
	@Override
	public HashSet<String> getDefaultModel(String language) {
		HashSet<String> modelName=new HashSet<String>();
		modelName.add(String.format("%s-cmu.dict", language));
		return modelName;
	}
	@Override
	public void setLocation(String location) {
		ModelLocation=ModelLocation+"/"+location;
		// TODO Auto-generated method stub
		
	}
	
	public String toString() {
		return ModelLocation;
	}
	
}
