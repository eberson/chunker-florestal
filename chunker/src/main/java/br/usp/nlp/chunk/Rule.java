package br.usp.nlp.chunk;

import static br.usp.nlp.chunk.rule.extraction.Constants.*;

public class Rule implements Comparable<Rule> {
	
	private final String rule;
	
	private String regex = "";

	public Rule(String rule) {
		super();
		this.rule = rule;
		
		String content = rule.substring(rule.indexOf(">") + 1).trim();
		
		String[] grammaticals = content.split(",");
		
		for(int i = 0; i < grammaticals.length; i++){
			String value = grammaticals[i];
			
			regex += REGEX_POTENTIAL_WORD + "_" + value.replaceAll("\\.", "").trim();
			
			if (i < grammaticals.length - 1){
				regex += " ";
			}
		}
		
		System.out.printf("%s ==> %s\n", rule, regex);
	}
	
	public boolean match(String value){
		return value.matches("^.*" + regex + ".*$");
	}
	
	public String getRule() {
		return rule;
	}

	@Override
	public int compareTo(Rule o) {
		Integer myValue = regex.split("\\s").length;
		Integer otherValue = o.regex.split("\\s").length;
		
		if (myValue.equals(otherValue)){
			return rule.compareTo(o.rule);
		}
		
		return myValue.compareTo(otherValue);
	}
}
