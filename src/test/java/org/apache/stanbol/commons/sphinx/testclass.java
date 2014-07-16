package org.apache.stanbol.commons.sphinx;

import org.apache.stanbol.commons.sphinx.impl.ModelProviderImpl;

public class testclass {

	public static void main(String[] args) {
	
		ModelProviderImpl MP = new ModelProviderImpl(new ClasspathDataFileProvider("DUMMY"));
    	LanguageModel model = MP.getDefaultModel("en");
    	System.out.println(model.getModelFiles().get("acoustic"));
	}
}
