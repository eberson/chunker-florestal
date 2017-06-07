package br.usp.nlp.chunk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Evaluation {
	
	private static final String[] ALL_RULES = { "np --> adj, n, adj.",
			                                    "np --> adj, n, v-pcp.",
			                                    "np --> adj, n.",
			                                    "np --> adj, num, n.",
			                                    "np --> adj.",
			                                    "np --> adv, adj, n.",
			                                    "np --> adv, art, adj, n.",
			                                    "np --> adv, art, n, adj.",
			                                    "np --> adv, art, n.",
			                                    "np --> adv, art, prop.",
			                                    "np --> adv, n, adj.",
			                                    "np --> adv, n.",
			                                    "np --> adv, num, n, adj.",
			                                    "np --> adv, num, n, n.",
			                                    "np --> adv, num, n.",
			                                    "np --> adv, pron-det, n.",
			                                    "np --> adv, pron-det.",
			                                    "np --> adv, pron-indp.",
			                                    "np --> adv, prop.",
			                                    "np --> art, adj, adj, n.",
			                                    "np --> art, adj, n, adj, adj.",
			                                    "np --> art, adj, n, adj.",
			                                    "np --> art, adj, n, conj-s, adv, v-fin.",
			                                    "np --> art, adj, n, num, prp.",
			                                    "np --> art, adj, n, num.",
			                                    "np --> art, adj, n, prp, adv.",
			                                    "np --> art, adj, n, prp, v-inf.",
			                                    "np --> art, adj, n, prp, v-pcp.",
			                                    "np --> art, adj, n, prp.",
			                                    "np --> art, adj, n, v-fin, prp.",
			                                    "np --> art, adj, n, v-fin.",
			                                    "np --> art, adj, n, v-pcp.",
			                                    "np --> art, adj, n-adj.",
			                                    "np --> art, adj, n.",
			                                    "np --> art, adj, num, n.",
			                                    "np --> art, adj, prop, v-fin, v-inf.",
			                                    "np --> art, adj, prop.",
			                                    "np --> art, adj, v-pcp.",
			                                    "np --> art, adj.",
			                                    "np --> art, adv, n, n, adj.",
			                                    "np --> art, adv, n.",
			                                    "np --> art, adv, v-pcp.",
			                                    "np --> art, adv.",
			                                    "np --> art, n, adj, adj, adj.",
			                                    "np --> art, n, adj, adj.",
			                                    "np --> art, n, adj, adv, adj.",
			                                    "np --> art, n, adj, num.",
			                                    "np --> art, n, adj, v-fin.",
			                                    "np --> art, n, adj, v-pcp.",
			                                    "np --> art, n, adj.",
			                                    "np --> art, n, adv, adj, adj.",
			                                    "np --> art, n, adv, adj.",
			                                    "np --> art, n, adv, v-pcp.",
			                                    "np --> art, n, adv.",
			                                    "np --> art, n, num, prp.",
			                                    "np --> art, n, num.",
			                                    "np --> art, n, prp, adv.",
			                                    "np --> art, n, prp, v-inf.",
			                                    "np --> art, n, prp, v-pcp.",
			                                    "np --> art, n, prp.",
			                                    "np --> art, n, v-fin, v-inf.",
			                                    "np --> art, n, v-fin.",
			                                    "np --> art, n, v-inf.",
			                                    "np --> art, n, v-pcp, adv, v-pcp.",
			                                    "np --> art, n, v-pcp, prp, adv.",
			                                    "np --> art, n, v-pcp.",
			                                    "np --> art, n-adj, conj-c, n-adj.",
			                                    "np --> art, n-adj, n-adj, conj-c, n-adj.",
			                                    "np --> art, n-adj, v-fin.",
			                                    "np --> art, n-adj, v-pcp, prp, v-inf.",
			                                    "np --> art, n-adj.",
			                                    "np --> art, n.",
			                                    "np --> art, num, adj, n, adj.",
			                                    "np --> art, num, adj, n.",
			                                    "np --> art, num, n-adj.",
			                                    "np --> art, num, n.",
			                                    "np --> art, num.",
			                                    "np --> art, pron-det, adj, n.",
			                                    "np --> art, pron-det, art, n.",
			                                    "np --> art, pron-det, n, adj.",
			                                    "np --> art, pron-det, n, v-pcp.",
			                                    "np --> art, pron-det, n.",
			                                    "np --> art, pron-det, num, n.",
			                                    "np --> art, pron-det, prop.",
			                                    "np --> art, pron-det, v-pcp, prp.",
			                                    "np --> art, pron-det.",
			                                    "np --> art, prop, adj.",
			                                    "np --> art, prop, adv, v-pcp.",
			                                    "np --> art, prop, num.",
			                                    "np --> art, prop, prp, adv.",
			                                    "np --> art, prop, v-fin.",
			                                    "np --> art, prop, v-inf.",
			                                    "np --> art, prop.",
			                                    "np --> art, v-pcp, n.",
			                                    "np --> art, v-pcp, prop.",
			                                    "np --> art, v-pcp.",
			                                    "np --> n, adj, adj, adj.",
			                                    "np --> n, adj, adj.",
			                                    "np --> n, adj, v-pcp, prp.",
			                                    "np --> n, adj, v-pcp.",
			                                    "np --> n, adj.",
			                                    "np --> n, adv, adj.",
			                                    "np --> n, adv, v-fin, v-ger, prp, adv.",
			                                    "np --> n, adv, v-pcp.",
			                                    "np --> n, adv.",
			                                    "np --> n, n.",
			                                    "np --> n, num, adj.",
			                                    "np --> n, num, n.",
			                                    "np --> n, num, prp.",
			                                    "np --> n, num.",
			                                    "np --> n, prp, v-pcp.",
			                                    "np --> n, prp.",
			                                    "np --> n, v-pcp, adj.",
			                                    "np --> n, v-pcp, adv.",
			                                    "np --> n, v-pcp, conj-c, v-pcp.",
			                                    "np --> n, v-pcp, prp.",
			                                    "np --> n, v-pcp.",
			                                    "np --> n-adj, v-pcp.",
			                                    "np --> n-adj.",
			                                    "np --> n.",
			                                    "np --> num, n, adj.",
			                                    "np --> num, n, adv, adj.",
			                                    "np --> num, n, adv, v-pcp.",
			                                    "np --> num, n, adv.",
			                                    "np --> num, n, art, adj, art, adj, conj-c.",
			                                    "np --> num, n, n.",
			                                    "np --> num, n, prp, adv.",
			                                    "np --> num, n, v-pcp.",
			                                    "np --> num, n.",
			                                    "np --> num, num, n.",
			                                    "np --> num, pron-det, n.",
			                                    "np --> num, prop.",
			                                    "np --> num, v-pcp.",
			                                    "np --> num.",
			                                    "np --> pron-det, adj, n, adj.",
			                                    "np --> pron-det, adj, n.",
			                                    "np --> pron-det, adv, adj.",
			                                    "np --> pron-det, adv, v-pcp.",
			                                    "np --> pron-det, art, n, adj.",
			                                    "np --> pron-det, art, n, v-pcp.",
			                                    "np --> pron-det, art, n-adj.",
			                                    "np --> pron-det, art, n.",
			                                    "np --> pron-det, art, pron-det, n.",
			                                    "np --> pron-det, art, prop.",
			                                    "np --> pron-det, n, adj, adj.",
			                                    "np --> pron-det, n, adj, v-fin.",
			                                    "np --> pron-det, n, adj, v-pcp.",
			                                    "np --> pron-det, n, adj.",
			                                    "np --> pron-det, n, adv, adj.",
			                                    "np --> pron-det, n, adv, v-pcp.",
			                                    "np --> pron-det, n, adv.",
			                                    "np --> pron-det, n, num.",
			                                    "np --> pron-det, n, v-fin.",
			                                    "np --> pron-det, n, v-pcp.",
			                                    "np --> pron-det, n-adj.",
			                                    "np --> pron-det, n.",
			                                    "np --> pron-det, num, n.",
			                                    "np --> pron-det, pron-det, n.",
			                                    "np --> pron-det, pron-indp.",
			                                    "np --> pron-det, prop.",
			                                    "np --> pron-det, v-pcp, n.",
			                                    "np --> pron-det.",
			                                    "np --> pron-indp, adj.",
			                                    "np --> pron-indp, adv, adj.",
			                                    "np --> pron-indp, prp, adv.",
			                                    "np --> pron-indp, v-fin, v-inf.",
			                                    "np --> pron-indp, v-fin, v-pcp.",
			                                    "np --> pron-indp, v-fin.",
			                                    "np --> pron-indp.",
			                                    "np --> pron-pers, art, n, prp, v-fin.",
			                                    "np --> pron-pers, v-fin.",
			                                    "np --> pron-pers.",
			                                    "np --> prop, adj, n.",
			                                    "np --> prop, adj.",
			                                    "np --> prop, adv, v-pcp.",
			                                    "np --> prop, num.",
			                                    "np --> prop, v-fin.",
			                                    "np --> prop, v-pcp.",
			                                    "np --> prop.",
			                                    "np --> prp, num, n.",
			                                    "np --> v-pcp, n, adj.",
			                                    "np --> v-pcp, n.",
			                                    "np --> v-pcp, num, n." };
	
	private static final String[] RULES_98_PERCENT = { "np --> art, n.",
		                                               "np --> prop.",
		                                               "np --> n.",
		                                               "np --> art, prop.",
		                                               "np --> pron-indp.",
		                                               "np --> pron-pers.",
		                                               "np --> art, n, adj.",
		                                               "np --> num, n.",
		                                               "np --> pron-det, n.",
		                                               "np --> n, adj.",
		                                               "np --> art, adj, n.",
		                                               "np --> pron-det.",
		                                               "np --> adj, n.",
		                                               "np --> n, num.",
		                                               "np --> art, pron-det, n.",
		                                               "np --> art, n-adj.",
		                                               "np --> art, n, v-pcp.",
		                                               "np --> num.",
		                                               "np --> pron-det, n, adj.",
		                                               "np --> n, v-pcp.",
		                                               "np --> art, num, n.",
		                                               "np --> pron-det, art, n.",
		                                               "np --> adv, num, n.",
		                                               "np --> art, n, num.",
		                                               "np --> art, n, prp, adv.",
		                                               "np --> art, n, adv, adj.",
		                                               "np --> art, n, adj, adj.",
		                                               "np --> pron-det, adj, n.",
		                                               "np --> art, adj, n, adj.",
		                                               "np --> num, n, adj.",
		                                               "np --> prop, num.",
		                                               "np --> pron-det, num, n.",
		                                               "np --> art, v-pcp.",
		                                               "np --> art, pron-det." };
	
	private static final String[] RULES_90_PERCENT = { "np --> art, n.",
                                                       "np --> prop.",
                                                       "np --> n.",
                                                       "np --> art, prop.",
                                                       "np --> pron-indp.",
                                                       "np --> pron-pers.",
                                                       "np --> art, n, adj.",
                                                       "np --> num, n.",
                                                       "np --> pron-det, n.",
                                                       "np --> n, adj.",
                                                       "np --> art, adj, n."};
	
	private final List<String> testData = Collections.synchronizedList(new ArrayList<>());
	private final List<String> sourceData = Collections.synchronizedList(new ArrayList<>());
	
	private final String source;
	private final String test;
	
	public Evaluation(String source, String test) {
		super();
		
		this.source = source;
		this.test = test;
		
		loadTestData();
		execute();
	}
	
	private void loadTestData(){
		try(Stream<String> stream = Files.lines(Paths.get(test))){
			stream.forEach(l -> {
				testData.add(l.replaceAll("\\s{2,}", " "));
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}
	
	private void execute(){
		Chunker chunker = new Chunker(ALL_RULES);
		
		final StringBuilder output = new StringBuilder();
		
		try(Stream<String> stream = Files.lines(Paths.get(source))){
			stream.forEach(line -> {
				String result = chunker.evaluate(line).replaceAll("\\s{2,}", " ");
				output.append(result).append("\n");
				sourceData.add(result);
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		try {
			Files.write(Paths.get("C:\\java\\evaluation_output.txt"), output.toString().getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void evaluate(){
		long npEsperados = 0;
		long npGerados = 0;
		AtomicLong falsePositive = new AtomicLong(0);
		AtomicLong falseNegativo = new AtomicLong(0);
		AtomicLong truePositive = new AtomicLong(0);
		
		for(int i = 0; i < sourceData.size(); i++){
			String sentencaGerada = sourceData.get(i);
			List<String> ngramasGerado = new ArrayList<>();
			
			String sentencaEsperada = testData.get(i);
			List<String> ngramasEsperado = new ArrayList<>();
			
			limpa(ngramasGerado, sentencaGerada);
			limpa(ngramasEsperado, sentencaEsperada);
			
			npEsperados += ngramasEsperado.size();
			npGerados += ngramasGerado.size();
			
			ngramasGerado.stream().forEach(ngrama -> {
				if (ngramasEsperado.contains(ngrama)){
					truePositive.incrementAndGet();
				}
				else{
					falsePositive.incrementAndGet();
				}
			});

			ngramasEsperado.stream().forEach(ngrama -> {
				if (!ngramasGerado.contains(ngrama)){
					falseNegativo.incrementAndGet();
				}
			});
		}
		
		System.out.println("Total Esperado: " + npEsperados);
		System.out.println("Total Gerado: " + npGerados);
		System.out.println("Acurácia: " + truePositive.get());
		System.out.println("Falso Positivo: " + falsePositive.get());
		System.out.println("Falso Negativo: " + falseNegativo.get());
	}

	private String limpa(List<String> ngramas, String sentenca) {
		String retorno = sentenca;
		String patternInterna = "(.*np\\[[^\\]]*)(np\\[[^\\]]*\\])(.*\\].*)";
		Pattern regexInterna = Pattern.compile(patternInterna);
		Matcher matcherInterno = regexInterna.matcher(sentenca);
		
		if (matcherInterno.find()) {
			ngramas.add(matcherInterno.group(2));
			retorno = matcherInterno.group(1) + matcherInterno.group(2).replaceAll("np\\[", "").replaceAll("\\]", "") + matcherInterno.group(3);
			retorno = limpa(ngramas, retorno);
		} else {
			String patternExterno = "(np\\[[^\\]]*\\])";
			Pattern regexExterno = Pattern.compile(patternExterno);
			Matcher matcherExterno = regexExterno.matcher(sentenca);
			
			while (matcherExterno.find()) {
				ngramas.add(matcherExterno.group(1));
			}
		}
		
		return retorno;
	}

	public static void main(String[] args) {
		String source = "C:\\java\\source_avaliacao.txt";
		String test = "C:\\java\\avaliacao_corrigida.txt";
		
		
		Evaluation evaluation = new Evaluation(source, test);
		evaluation.evaluate();
	}	
}	

