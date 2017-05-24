package br.usp.nlp.chunk.rule.extraction.identifiers;

import static br.usp.nlp.chunk.rule.extraction.Constants.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GramaticalCategory implements ValueRecognizer{

	@Override
	public boolean apply(String content) {
		return Pattern.matches(REGEX_GRAMATICAL_CHECK, content);
	}

	@Override
	public String get(String content) {
		if (!apply(content)){
			return "";
		}
		
		Matcher matcher = Pattern.compile(REGEX_GRAMATICAL_EXTRACT).matcher(content);
		
		if (matcher.find()){
			return matcher.group();
		}
		
		return "";
	}
	
	public static void main(String[] args) {
//		String value = "=ADVL:np";
		String value = "==H:prop('Brasília' F S)	Brasília";
		
		
				
		GramaticalCategory syntagma = new GramaticalCategory();
		System.out.println(syntagma.apply(value));
		System.out.println(syntagma.get(value));
		
	}
	
	

}
