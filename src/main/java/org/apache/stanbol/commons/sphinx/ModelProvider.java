/*******************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
	 * @param models e.g. {"en-cmu.dict", "en-alpha.dict"}
	 * @param modelType e.g. { "AcousticModel", "LanguageModel", "DictionaryModel"}
	 * @return
	 */
	public BaseModel getModel(HashSet<String> models, BaseModel modelType);
	/**
	 * @param bundleSymbolicName Getter for symbolic name of bundle
	 */
	public void setBundleSymbolicName(String bundleSymbolicName);
}
