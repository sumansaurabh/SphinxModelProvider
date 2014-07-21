package org.apache.stanbol.commons.sphinx;

import java.util.HashSet;

import org.apache.stanbol.commons.sphinx.model.BaseModel;


public interface ModelProvider {
	
	public BaseModel getDefaultModel(String language, BaseModel modelType );
	
	//public <T> T getModel(String bundlename, String language);
	public BaseModel getModel(String language, HashSet<String> models, BaseModel modelType);
}
