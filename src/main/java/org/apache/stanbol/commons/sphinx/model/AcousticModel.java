package org.apache.stanbol.commons.sphinx.model;

import java.util.Arrays;
import java.util.HashSet;

public class AcousticModel implements BaseModel {
    
    private String ModelLocation=System.getProperty("java.io.tmpdir");
	
	

	@Override
	public HashSet<String> getDefaultModel(String language) {
    	String acousticResource[]={"feat.params", "mdef", "means", "mixture_weights", "noisedict", "transition_matrices", "variances"};
    	HashSet<String> modelName = new HashSet<String>(Arrays.asList(acousticResource));
		return modelName;
	}

	@Override
	public void setLocation(String location) {
		// TODO Auto-generated method stub
		
	}

	
	public String toString() {
		return ModelLocation+"/"+"acoustic";
	}
}
