package br.usp.nlp.chunk;

import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import br.usp.nlp.chunk.rule.extraction.Constants;
import br.usp.nlp.chunk.rule.extraction.RuleExtractor;
import br.usp.nlp.chunk.rule.extraction.RuleFilter;
import br.usp.nlp.chunk.rule.extraction.RuleNormalizer;

public class Chunker {
	
	private Set<Rule> rules = Collections.synchronizedSet(new TreeSet<>());
	
	public Chunker(String source) {
		super();
		
		buildRules();
	}
	
	public Chunker(String... rules) {
		super();
		
		Arrays.asList(rules).stream().forEach(rule -> {
			this.rules.add(new Rule(rule));
		});
	}
	
	private void buildRules(){
		RuleFilter filter = (rule) -> {
			String content = rule.substring(rule.indexOf(">") + 1);
			
			String[] split = content.trim()
					.replaceAll("\\[,\\]", "[@]")
					.replaceAll("\\.", "").split(",");
			
			for(String value : split){
				if (!value.trim().matches(Constants.REGEX_ONLY_GRAMATICAL) &&
						!value.trim().matches("\\[.\\]")){
					return false;
				}
			}
			
			return true;
		};
		
		RuleNormalizer normalizer = (rule) -> {
			return rule.replaceAll(", \\[.\\]", "");
		};
		
		RuleExtractor gen = new RuleExtractor();
		Set<String> rules = gen.generate("Bosque_CF_8.0.ad.txt", 
				                         filter, 
				                         normalizer);
		
		rules.stream().forEach(rule -> {
			if (rule.startsWith("np -->")){
				this.rules.add(new Rule(rule));
			}
		});
	}
	
	public String evaluate(String text){
		text = text.replaceAll("\\s{2,}", " ");
		String[] tokens = text.split("\\s");
		
		for(Rule rule : rules){
			if (rule.match(text)){
				tokens = rule.resolve(text, tokens);
			}
		}
		
		StringBuilder result = new StringBuilder();
		
		Arrays.asList(tokens).stream().forEach(token -> {
			result.append(token).append(" ");
		});
		
		return makeNPPhrase(result.toString());
	}
	
	private String makeNPPhrase(String rule) {
		return rule.replaceAll("_" + Constants.REGEX_ONLY_GRAMATICAL, "")
	               .replaceAll("\\[,\\]", ",")
	               .replaceAll("_", " ")
	               .replaceAll("\\s{1,}\\]", "]")
	               .replaceAll("\\s{2,}", " ");
	}
	

	public static void main(String[] args) {
//		Chunker chunker = new Chunker("Bosque_CF_8.0.ad.avaliacao.txt");
		Chunker chunker = new Chunker("np --> art, n.",
									  "np --> prop.",
									  "np --> n.",
									  "np --> art, prop.",
									  "np --> pron-indp.",
									  "np --> pron-pers.",
									  "np --> art, n, adj.",
									  "np --> num, n.",
									  "np --> pron-det, n.",
									  "np --> n, adj.",
									  "np --> art, adj, n.");
		
		try (Scanner scanner = new Scanner(System.in)) {
			String entrada = "";

			while (!entrada.equalsIgnoreCase("terminar")) {
				System.out.print("Informe a frase \"taggeada\": ");
				entrada = scanner.nextLine();
				System.out.println(chunker.evaluate(entrada));

				System.out.println("");
				System.out.print("Informe continuar ou terminar (para encerrar o sistema): ");
				entrada = scanner.nextLine();
			}
		}		
	}

}
