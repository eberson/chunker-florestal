package br.usp.nlp.chunk.rule.extraction.identifiers;

import java.util.ArrayList;
import java.util.List;

public final class ValueRecognizerFactory {

	private ValueRecognizerFactory() {
		super();
	}
	
	private static final List<ValueRecognizer> ALL_RECOGNIZERS = new ArrayList<>();
	
	public static synchronized List<ValueRecognizer> getAllRecognizers(){
		if (ALL_RECOGNIZERS.isEmpty()){
			ALL_RECOGNIZERS.add(new Syntagma());
			ALL_RECOGNIZERS.add(new GramaticalCategory());
			ALL_RECOGNIZERS.add(new PunctuationCategory());
		}
		
		return ALL_RECOGNIZERS;
	}
	
	

}
