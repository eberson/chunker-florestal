package br.usp.nlp.chunk;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class Evaluation {
	
	public static void main2(String[] args) {
		String source = "C:\\java\\source_avaliacao.txt";
		String test = "C:\\java\\avaliacao_corrigida.txt";
		
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
		
		final List<String> testData = Collections.synchronizedList(new ArrayList<>());
		final List<String> sourceData = Collections.synchronizedList(new ArrayList<>());
		
		try(Stream<String> stream = Files.lines(Paths.get(test))){
			stream.forEach(l -> {
				testData.add(l.replaceAll("\\s{2,}", " "));
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		
		try(Stream<String> stream = Files.lines(Paths.get(source))){
			stream.forEach(line -> {
				sourceData.add(chunker.evaluate(line).replaceAll("\\s{2,}", " "));
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		int counter = 0;
		
		System.out.println(sourceData.size());
		System.out.println(testData.size());
		
		for(String generated : sourceData){
			if (testData.contains(generated)){
				counter++;
			}
		}
		
		System.out.println(counter);
		
		for (int i = 0; i < sourceData.size(); i++){
			System.out.printf("%s ==> %s\n", sourceData.get(i), testData.get(i));
			
		}
	}	
}	

