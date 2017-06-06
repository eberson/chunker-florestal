package br.usp.nlp.chunk;

import static br.usp.nlp.chunk.rule.extraction.Constants.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	}
	
	public boolean match(String value){
		return value.matches("^.*" + regex + ".*$");
	}
	
	public String[] resolve(String phrase, String[] tokens){
		
		Matcher matcher = Pattern.compile(regex).matcher(phrase);
		
		
		while (matcher.find()){
			String group = matcher.group();

			String[] groupedToken = group.split("\\s");

			int start = 0;
			int end = 0;

			outer: for (int i = 0; i < tokens.length; i++) {
				if (tokens[i].replaceAll("np\\[", "").replaceAll("\\]", "").trim()
						.equalsIgnoreCase(groupedToken[0].replaceAll("np\\[", "").replaceAll("\\]", "").trim())) {
					start = i;

					if (groupedToken.length == 1) {
						end = i;
						
						tokens[start] = "np[" + tokens[start];
						tokens[end] = tokens[end] + "]";
						
						break;
					}

					int c = i + 1;

					for (int j = 1; j < groupedToken.length; j++, c++) {
						if (tokens[c].replaceAll("np\\[", "").replaceAll("\\]", "").trim()
								.equalsIgnoreCase(groupedToken[j].replaceAll("np\\[", "").replaceAll("\\]", "").trim())) {
							if (j == groupedToken.length - 1) {
								end = c;

								tokens[start] = "np[" + tokens[start];
								tokens[end] = tokens[end] + "]";

								break outer;
							}

							continue;
						}

						continue outer;
					}
				}
			}
		}
		
		
		return tokens;
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
		
		return otherValue.compareTo(myValue);
	}
}
