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
 * Wraps up the model properties for {@link LanguageModel}
 *  
 * @author Suman Saurabh
 *
 */

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
	
	
	public String toString() {
		return ModelLocation;
    }
}
