package org.apache.stanbol.commons.sphinx.model;

import java.util.HashSet;

public interface BaseModel {
	

	/**
	 * Getter for Default model files for @language
	 * @param language parsed by the client
	 * @return
	 */
	public HashSet<String> getDefaultModel(String language);
	/**
	 * Getter for custom models parsed by client
	 * @param language
	 * @param models HashSet is used because Acoustic Model have large no. of model files 
	 * @return
	 */
	public void setLocation(String location);
}
