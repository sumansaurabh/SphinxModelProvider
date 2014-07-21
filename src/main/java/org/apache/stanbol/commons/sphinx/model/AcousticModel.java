package org.apache.stanbol.commons.sphinx.model;

import java.util.HashSet;

public class AcousticModel implements BaseModel {
    
    private String ModelLocation=System.getProperty("java.io.tmpdir");
	
	

	@Override
	public HashSet<String> getDefaultModel(String language) {
    	String acousticResource[]={"feat.params", "mdef", "means", "mixture_weights", "noisedict", "transition_matrices", "variances"};
    	HashSet<String> modelName = new HashSet<String>();
    	for(String m: acousticResource){
			modelName.add(String.format("%s-%s", language,m));
    	}
		return modelName;
	}

	@Override
	public void setLocation(String location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HashSet<String> getCustomModel(String language, HashSet<String> models) {
		/*
		HashSet<String> modelName=new HashSet<String>();
		for(String m: models)
			modelName.add(String.format("%s-%s", language,m));*/
		return getDefaultModel(language);
	}
	
	public String toString() {
		return ModelLocation+"/"+"acoustic";
	}
}
