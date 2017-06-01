package br.usp.nlp.chunk.rule.extraction;

import static br.usp.nlp.chunk.rule.extraction.Constants.LINE_SEPARATOR;
import static br.usp.nlp.chunk.rule.extraction.Constants.PHRASE_TYPE_REGEX;
import static br.usp.nlp.chunk.rule.extraction.Constants.SENTENCE_END;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.usp.nlp.chunk.rule.extraction.identifiers.ValueRecognizer;
import br.usp.nlp.chunk.rule.extraction.identifiers.ValueRecognizerFactory;

public class RuleExtractor {
	
	private final ReentrantLock lock = new ReentrantLock(true);
	
	private static final String LEVEL_REGEX = "={1,}";
	
	private Map<String, GenericRuleInfo> rulesMap =  Collections.synchronizedMap(new HashMap<>());
	
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
			String generated = node.generateRule();
			
			if (generated == null || generated.isEmpty()){
				return;
			}
			
			String[] generatedRules = generated.split(LINE_SEPARATOR);
			
			for (String rule : generatedRules) {
				String statement = rule;
				
				if (filter != null){
					if (!filter.accept(statement)){
						continue;
					}
				}

				if (normalizer != null){
					statement = normalizer.normalize(statement);
				}

				group(statement, node.getIndex());
				rules.add(statement);
			}
		});
		
		return rules;
	}
	
	private void group(String statement, int index){
		String rule = statement.substring(0, statement.indexOf(">") - 2).trim();
		
		try{
			lock.lock();
			
			GenericRuleInfo info;
			
			if (rulesMap.containsKey(rule)){
				info = rulesMap.get(rule);
			}
			else{
				info = new GenericRuleInfo(rule);
				rulesMap.put(rule, info);
			}
			
			info.addRule(statement, index);
		}
		finally{
			lock.unlock();
		}
	}
	
	public void createEvaluation(String target){
		createEvaluation(target, "ALL");
	}

	public void createEvaluation(String target, String expectedRule){
		StringBuilder evaluation = new StringBuilder();
		
		if ("ALL".equals(expectedRule)){
			for(GenericRuleInfo info : rulesMap.values()){
				generateEvalution(evaluation, info);
			}
		}
		else{
			generateEvalution(evaluation, rulesMap.get(expectedRule));
		}
		
		try {
			Files.write(Paths.get(target), evaluation.toString().getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void generateEvalution(StringBuilder evaluation, GenericRuleInfo info) {
		int totalOccurrences = info.getOccurrences();
		
		evaluation.append("generic rule: ").append(info.getRule()).append(" (").append(totalOccurrences).append(")\n");
		
		for(RuleInfo rule : info.getOrderedRules()){
			evaluation.append("\trule: ").append(rule.getRule()).append(" (").append(rule.getOccurrences()).append(")\n");
			evaluation.append("\t\tfrequencia: ").append(String.format("%.9f", rule.getFrequence())).append("\n");
			evaluation.append("\t\tstatements: ");
			
			for (String statement : rule.getStatements()){
				evaluation.append("\t\t\t").append(statement).append("\n");
			}
			
			evaluation.append("\n");
		}
		
		evaluation.append("\n");
	}
	
	public List<Node> createRuleNodes(String sourceFile){
		List<Node> nodes = Collections.synchronizedList(new ArrayList<>());
		
		List<String> sentences = readSentences(sourceFile);
		
		final AtomicInteger position = new AtomicInteger(0);
		
		sentences.parallelStream().forEach(sentence -> {
			Node node = new Node("SENTENCA", 0, extractSimpleSentence(sentence));
			generateImpl(node, sentence);
			nodes.add(node);
		});
		
		return nodes;
	}
	
	private synchronized String extractSimpleSentence(String sentence){
		String[] parts = sentence.split("\\n");
		
		for (String part : parts) {
			Matcher matcher = Pattern.compile("CF.*?\\s(.*)").matcher(part);
			
			if (matcher.find()){
				return matcher.group(1);
			}
		}
		
		return "";
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
		for (ValueRecognizer recognizer : ValueRecognizerFactory.getAllRecognizers()) {
			if (!recognizer.apply(line)){
				continue;
			}
			
			return recognizer.get(line);
		}
		
		return null;
	}
	
	@SuppressWarnings("unused")
	private void createRule(Node node) {
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
		RuleExtractor gen = new RuleExtractor();
		
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
		
		Set<String> rules = gen.generate("Bosque_CF_8.0.ad.treinamento.txt", 
				                         filter, 
				                         normalizer);
		
		rules.stream().forEach(rule -> {
			if (rule.startsWith("np -->")){
				System.out.println(rule);
			}
		});
		
		gen.createEvaluation("C:\\java\\evaluation.txt", "np");
	}
}
