package org.apache.stanbol.commons.sphinx;

import java.util.HashSet;

import org.apache.stanbol.commons.sphinx.model.BaseModel;


public interface ModelProvider {
	
	/**
	 * Initializes the Model files for the @langauge and @modeType { AcousticModel, LanguageModel, DictionaryModel }
	 * @param language e.g. {"en","fr","gn"}
	 * @param modelType e.g. { "AcousticModel", "LanguageModel", "DictionaryModel"}
	 * @return 
	 */
	public BaseModel getDefaultModel(String language, BaseModel modelType );
	/**
	 * * Initializes the Model files for the @models and @modeType { AcousticModel, LanguageModel, DictionaryModel }
	 * @param models models e.g. {"cmudict.0.6.d", "alpha.dict"}
	 * @param modelType modelType e.g. { "AcousticModel", "LanguageModel", "DictionaryModel"}
	 * @return
	 */
	public BaseModel getModel(HashSet<String> models, BaseModel modelType);

	public void setBundleSymbolicName(String bundleSymbolicName);
}
