package br.usp.nlp.chunk.rule.extraction.identifiers;

import static br.usp.nlp.chunk.rule.extraction.Constants.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GramaticalTagger implements ValueRecognizer{

	@Override
	public boolean apply(String content) {
		return Pattern.matches(REGEX_GRAMATICAL_CHECK, content);
	}

	@Override
	public String get(String content) {
		if (!apply(content)){
			return "";
		}
		
		Matcher matcher = Pattern.compile(REGEX_TAGGER_EXTRACT).matcher(content);
		
		if (matcher.find()){
			return String.format("%s_%s ", matcher.group(2), matcher.group(1));
		}
		
		return "";
	}
	
	public static void main(String[] args) {
//		String value = "=ADVL:np";
		String value = "==H:prop('Brasília' F S)	Brasília";
		
		
				
		GramaticalTagger syntagma = new GramaticalTagger();
		System.out.println(syntagma.apply(value));
		System.out.println(syntagma.get(value));
		
	}
	
	

}
