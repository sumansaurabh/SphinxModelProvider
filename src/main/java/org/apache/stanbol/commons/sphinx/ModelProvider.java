package org.apache.stanbol.commons.sphinx;


public interface ModelProvider {
	
	/**
	 * Gets for the default model for the parsed language
	 * @param language
	 * @return LanguageModel
	 */
	public LanguageModel getDefaultModel(String language);
	/**
	 * Gets for a specific model for the parsed language
	 * @param bundlename
	 * @param language
	 * @return LanguageModel
	 */
	public LanguageModel getModel(String bundlename, String language);
}
