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
package org.apache.stanbol.commons.sphinx.model;

import java.util.HashSet;
/**
 * This interface is for Sphinx- Models which wraps Model Properties. 
 * More properties can be added in future for  {@link BaseModel}
 *
 * @author Suman Saurabh
 */
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
