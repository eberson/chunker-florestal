package br.usp.nlp.chunk;

import static br.usp.nlp.chunk.rule.extraction.Constants.REGEX_POTENTIAL_WORD;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import br.usp.nlp.chunk.rule.extraction.Constants;
import br.usp.nlp.chunk.rule.extraction.Normalizer;
import br.usp.nlp.chunk.rule.extraction.RuleExtractor;
import br.usp.nlp.chunk.rule.extraction.RuleFilter;
import br.usp.nlp.chunk.rule.extraction.SentenceReader;

public class Chunker {
	
	private Set<Rule> rules = Collections.synchronizedSet(new TreeSet<>());
	
	public Chunker(String source) {
		super();
		
		buildRules();
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
		
		Normalizer normalizer = (rule) -> {
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
	
	public void evaluate(String text){
		for(Rule rule : rules){
			if (rule.match(text)){
				System.out.printf("%s matches with rule %s\n", text, rule.getRule());
			}
		}
	}



	public static void main(String[] args) {
		Chunker chunker = new Chunker("Bosque_CF_8.0.ad.reduzido.txt");
		
		String entrada = "A_partir_de_prp agora_adv , a_art taxa_n deve_v-fin sempre_adv recuar_v-inf  .";
		
		chunker.evaluate(entrada);
		
		String target = "A_partir_de agora , np[a taxa] deve sempre recuar  .";
		
	}

}
