package br.usp.nlp.chunk.rule.extraction.identifiers;

public interface ValueRecognizer {
	
	boolean apply(String content);
	
	String get(String content);

}
