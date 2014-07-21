package org.apache.stanbol.commons.sphinx.model;

import java.util.HashSet;

public interface BaseModel {
	
	public HashSet<String> getDefaultModel(String language);
	public HashSet<String> getCustomModel(String language, HashSet<String> models);
	public void setLocation(String location);
}
