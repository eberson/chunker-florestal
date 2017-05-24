package br.usp.nlp.chunk.rule.extraction;

import static br.usp.nlp.chunk.rule.extraction.Constants.LINE_SEPARATOR;
import static br.usp.nlp.chunk.rule.extraction.Constants.PHRASE_TYPE_REGEX;
import static br.usp.nlp.chunk.rule.extraction.Constants.SENTENCE_END;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.usp.nlp.chunk.rule.extraction.identifiers.ValueRecognizer;
import br.usp.nlp.chunk.rule.extraction.identifiers.ValueRecognizerFactory;

public class TaggerExtractor {
	
	private static final String LEVEL_REGEX = "={1,}";
	
	public Set<String> generate(String sourceFile){
		return generate(sourceFile, null);
	}

	public Set<String> generate(String sourceFile, RuleFilter filter){
		return generate(sourceFile, null, null);
	}

	public Set<String> generate(String sourceFile, RuleFilter filter, Normalizer normalizer){
		Set<String> rules = Collections.synchronizedSet(new TreeSet<>());
		
		List<Node> nodes = createRuleNodes(sourceFile);
		
		nodes.parallelStream().forEach(node -> {
			rules.add(node.generatePhrase() + Constants.LINE_SEPARATOR);
		});
		
		return rules;
	}
	
	public List<Node> createRuleNodes(String sourceFile){
		List<Node> nodes = Collections.synchronizedList(new ArrayList<>());
		
		List<String> sentences = readSentences(sourceFile);
		
		sentences.parallelStream().forEach(sentence -> {
			Node node = new Node("SENTENCA", 0);
			generateImpl(node, sentence);
			nodes.add(node);
		});
		
		return nodes;
	}

	private Node generateImpl(Node rootNode, String sentence){
		String[] lines = sentence.split(LINE_SEPARATOR);
		
		boolean startSentence = false;
		
		Node current = rootNode;
		
		for (String line : lines) {
			if (line.matches(SENTENCE_END)){
				continue;
			}
			
			if (!line.matches(PHRASE_TYPE_REGEX) && !startSentence){
				continue;
			}
			
			if (!startSentence){
				startSentence = true;
				continue;
			}
			
			
			int level = getLevel(line);
			String value = getValue(line);
			
			if (value == null){
				continue;
			}
			
			Node node = new Node(value, level);
			
			
			if (current.getLevel() + 1 == level){
				current.addChild(node);
				
				if (!node.isPunctuation()){
					current = node;
				}
				
				continue;
			}
			
			current.back(level - 1).addChild(node);

			if (!node.isPunctuation()){
				current = node;
			}
		}
		
		return rootNode;
	}
	
	private int getLevel(String line){
		Matcher matcher = Pattern.compile(LEVEL_REGEX).matcher(line);
		
		if (!matcher.find()){
			throw new RuntimeException("Não sei identificar... ("+line+")");
		}
		
		return matcher.group().length();
	}
	
	private String getValue(String line){
		for (ValueRecognizer recognizer : ValueRecognizerFactory.getTaggersRecognizers()) {
			if (!recognizer.apply(line)){
				continue;
			}
			
			return recognizer.get(line);
		}
		
		return null;
	}
	
	@SuppressWarnings("unused")
	private void createRule(Node node) {
//		print(node);
		System.out.println(node.generateRule());
	}
	
	@SuppressWarnings("unused")
	private void print(Node node){
		System.out.printf("%s %s\n", String.join("", Collections.nCopies(node.getLevel(), "=")), node.getValue());
		
		for(Node child : node.getChildren()){
			print(child);
		}
	}
	
	private List<String> readSentences(String sourceFile){
		SentenceReader reader = new SentenceReader();
		return reader.read(sourceFile);
	}
	
	public static void main(String[] args) {
		TaggerExtractor gen = new TaggerExtractor();
		
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
		
		Set<String> rules = gen.generate("Bosque_CF_8.0.ad.txt");
		
		rules.stream().forEach(System.out::println);
		
//		rules.stream().forEach(rule -> {
//			if (rule.startsWith("np -->")){
//				System.out.println(rule);
//			}
//		});
	}
}
