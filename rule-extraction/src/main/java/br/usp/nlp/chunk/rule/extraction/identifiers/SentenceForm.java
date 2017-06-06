package br.usp.nlp.chunk.rule.extraction.identifiers;

import static br.usp.nlp.chunk.rule.extraction.Constants.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SentenceForm implements ValueRecognizer{

	@Override
	public boolean apply(String content) {
		return Pattern.matches(REGEX_SENTENCE_FORM_CHECK, content);
	}

	@Override
	public String get(String content) {
		if (!apply(content)){
			return "";
		}
		
		Matcher matcher = Pattern.compile(REGEX_SENTENCE_FORM_EXTRACT).matcher(content);
		
		if (matcher.find()){
			return matcher.group(1);
		}
		
		return "";
	}
	
	public static void main(String[] args) {
		
//		String value = "=======H:art('o' <artd> M S)	o";
		String value = "=======N<:icl";
//		String value = "========P:vp";
		
				
		SentenceForm recongnizer = new SentenceForm();
		System.out.println(recongnizer.apply(value));
		System.out.println(recongnizer.get(value));
		
	}
	
	

}
