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

import org.apache.stanbol.commons.sphinx.model.AcousticModel;
import org.apache.stanbol.commons.sphinx.model.BaseModel;
import org.apache.stanbol.commons.sphinx.model.DictionaryModel;
import org.apache.stanbol.commons.sphinx.model.LanguageModel;
import org.apache.stanbol.commons.stanboltools.datafileprovider.DataFileProvider;
/**
 * OSGI service that let you load OpenNLP Models via the Stanbol 
 * {@link DataFileProvider} infrastructure. This allows users to copy models
 * to the 'datafiles' directory or developer to provide models via via OSGI
 * bundles.<p>
 * This service also provides methods that directly return the Sphinx component
 * wrapping the model.

 * @author Suman Saurabh
 *
 */

public interface ModelProvider {
	
	/**
	 * Initializes the Model files for the @langauge and {@link LanguageModel}, {@link AcousticModel}, {@link DictionaryModel}
	 * @param language e.g. {"en","fr","gn"}
	 * @param modelType {@link LanguageModel}, {@link AcousticModel}, {@link DictionaryModel}
	 * @return 
	 */
	public BaseModel getDefaultModel(String language, BaseModel modelType );
	/**
	 * * Initializes the Model files for the @models and @modeType { AcousticModel, LanguageModel, DictionaryModel }
	 * @param models e.g. {"en-cmu.dict", "en-alpha.dict"}
	 * @param modelType {@link LanguageModel}, {@link AcousticModel}, {@link DictionaryModel}
	 * @param bundleSymbolicName Symbolic name of bundle, currently used only for acoustic model
	 * @return
	 */
	public BaseModel getModel(HashSet<String> models, BaseModel modelType,String bundleSymbolicName);
	/**
	 * @param bundleSymbolicName Getter for symbolic name of bundle
	 */
}
