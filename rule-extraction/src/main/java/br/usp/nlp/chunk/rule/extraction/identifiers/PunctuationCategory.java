package br.usp.nlp.chunk.rule.extraction.identifiers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PunctuationCategory implements ValueRecognizer{

	private static final String REGEX_CHECK = "[=]*(,|:|;|\\.|\\.\\.\\.|\\?|!).*";
	private static final String REGEX_EXTRACT = "(,|:|;|\\.|\\.\\.\\.|\\?|!)";
	
	@Override
	public boolean apply(String content) {
		return Pattern.matches(REGEX_CHECK, content);
	}

	@Override
	public String get(String content) {
		if (!apply(content)){
			return "";
		}
		
		Matcher matcher = Pattern.compile(REGEX_EXTRACT).matcher(content);
		
		if (matcher.find()){
			return "[" + matcher.group() + "]";
		}
		
		return "";
	}
	
	public static void main(String[] args) {
		String value = "=ADVL:np";
//		String value = "==H:prop('Brasília' F S)	Brasília";
//		String value = "==:\\n";
		
		
				
		PunctuationCategory syntagma = new PunctuationCategory();
		System.out.println(syntagma.apply(value));
		System.out.println(syntagma.get(value));
		
	}
	
	

}
