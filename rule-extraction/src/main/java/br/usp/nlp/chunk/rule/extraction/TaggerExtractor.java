package br.usp.nlp.chunk.rule.extraction;

import static br.usp.nlp.chunk.rule.extraction.Constants.LINE_SEPARATOR;
import static br.usp.nlp.chunk.rule.extraction.Constants.PHRASE_TYPE_REGEX;
import static br.usp.nlp.chunk.rule.extraction.Constants.SENTENCE_END;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
	
	public void createCorpus(String sourceFile){
		Set<String> rules = generate(sourceFile);
		
		StringBuilder taggedPhrase = new StringBuilder();
		StringBuilder npPhrase = new StringBuilder();
		
		for (String rule : rules){
			taggedPhrase.append(makeTaggedPhrase(rule)).append(" .").append(Constants.LINE_SEPARATOR);
			npPhrase.append(makeNPPhrase(rule)).append(" .").append(Constants.LINE_SEPARATOR);
		}
		
		try {
			Files.write(Paths.get("C:/java/tagged_phrase.txt"), taggedPhrase.toString().getBytes());
			Files.write(Paths.get("C:/java/np_phrase.txt"), npPhrase.toString().getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String makeTaggedPhrase(String rule) {
		return rule.replaceAll("np\\[", "")
				   .replaceAll("\\[|\\]", "")
				   .replaceAll("\\s{2,}", " ");
	}
	
	private String makeNPPhrase(String rule) {
		return rule.replaceAll("_" + Constants.REGEX_ONLY_GRAMATICAL, "")
	               .replaceAll("\\[,\\]", ",")
	               .replaceAll("\\s{1,}\\]", "]")
	               .replaceAll("\\s{2,}", " ");
	}


	public Set<String> generate(String sourceFile){
		Set<String> rules = Collections.synchronizedSet(new TreeSet<>());
		
		List<Node> nodes = createRuleNodes(sourceFile);
		
		nodes.parallelStream().forEach(node -> {
			String phrase = node.generatePhrase().trim();
			
			if (!phrase.isEmpty()){
				rules.add(phrase + Constants.LINE_SEPARATOR);
			}
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
	
	private List<String> readSentences(String sourceFile){
		SentenceReader reader = new SentenceReader();
		return reader.read(sourceFile);
	}
	
	public static void main(String[] args) {
		TaggerExtractor gen = new TaggerExtractor();
		
        gen.createCorpus("Bosque_CF_8.0.ad.txt");
		
//		Set<String> rules = gen.generate("Bosque_CF_8.0.ad.txt");
//		
//		rules.stream().forEach(System.out::println);
	}
}
