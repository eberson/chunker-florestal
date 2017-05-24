package br.usp.nlp.chunk.rule.extraction;

public final class Constants {

	private Constants() {
		super();
	}
	
	public static final String SENTENCE_START = "<s>";
	public static final String SENTENCE_END = "</s>|&&|=\\.";
	
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	public static final String PHRASE_TYPE_REGEX = "^(UTT|STA|QUE|CMD|EXC):.*$";
	
	public static final String REGEX_ONLY_GRAMATICAL = "(n|prop|adj|n-adj|v-fin|v-inf|v-pcp|v-ger|art|pron-pers|pron-det|pron-indp|adv|num|prp|intj|conj-s|conj-c)";
	public static final String REGEX_ONLY_PUNCTUATION = "\\[(,|:|;|\\.|\\.\\.\\.|\\?|!)\\]";
	
	public static final String REGEX_GRAMATICAL_CHECK = "^.*:(n|prop|adj|n-adj|v-fin|v-inf|v-pcp|v-ger|art|pron-pers|pron-det|pron-indp|adv|num|prp|intj|conj-s|conj-c)\\(.*";
	public static final String REGEX_GRAMATICAL_EXTRACT = "(?<=:).*(?=\\()";

}
