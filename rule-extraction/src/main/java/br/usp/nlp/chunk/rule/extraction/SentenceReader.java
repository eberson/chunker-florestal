package br.usp.nlp.chunk.rule.extraction;

import static br.usp.nlp.chunk.rule.extraction.Constants.LINE_SEPARATOR;
import static br.usp.nlp.chunk.rule.extraction.Constants.SENTENCE_END;
import static br.usp.nlp.chunk.rule.extraction.Constants.SENTENCE_START;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SentenceReader {
	
	private Path curretPath(String file) throws Exception{
		ClassLoader cl = RuleExtractor.class.getClassLoader();
		URI uri = cl.getResource(file).toURI();
		
		return Paths.get(uri);
	}
	
	public List<String> read(String file){
		List<String> result = new ArrayList<>();
		SentenceMaker maker = new SentenceMaker();
		
		try(Stream<String> stream = Files.lines(curretPath(file), Charset.forName("ISO-8859-1"))){
			stream.forEach(line -> {
				if (line.isEmpty()){
					return;
				}
				
				if (line.contains(SENTENCE_START)){
					maker.start().include(line);
					return;
				}
				
				if (line.matches(SENTENCE_END)){
					result.add(maker.include(line).get());
					maker.end();
					return;
				}
				
				if(maker.isOpen()){
					maker.include(line);
				}
			});
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		
		return result;
	}
	
	private class SentenceMaker{
		private StringBuilder sentence;
		private boolean open = false;
		
		
		public SentenceMaker start(){
			open = true;
			sentence = new StringBuilder();
			return this;
		}
		
		public SentenceMaker include(String content){
			sentence.append(content).append(LINE_SEPARATOR);
			return this;
		}
		
		public String get(){
			return sentence.toString();
		}
		
		public SentenceMaker end(){
			open = false;
			return this;
		}
		
		public boolean isOpen(){
			return open;
		}
	}

	public static void main(String[] args) {
		
//		SentenceReader reader = new SentenceReader();
//		List<String> sentences = reader.read("Bosque_CF_8.0.ad.reduzido.txt");
//		
//		System.out.println(String.format("%d sentenças lidas", sentences.size()));
//		System.out.println(sentences.get(0));
		
		String[] values = { "=SUBJ:np",
				            "==H:prop('PT' M S)	PT",
				            "=SA:pp",
				            "==H:prp('em' <sam->)	em",
				            "==P<:np",
				            "===>N:art('o' <-sam> <artd> M S)	o",
				            "===H:n('governo' M S)	governo",};
		
		String regex = ":(prop|prp|art|n|v-pcp)\\(.*\\).*?([a-zA-Z]{1,})";
		
		for (String value : values) {
			Matcher matcher = Pattern.compile(regex).matcher(value);
			
			if (matcher.find()){
				System.out.printf("%s_%s ", matcher.group(2), matcher.group(1));
			}
		}
	}

}
