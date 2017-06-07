package br.usp.nlp.chunk;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import br.usp.nlp.chunk.rule.extraction.RuleExtractor;

public class Limpa {

	public static void main(String[] args) throws Exception {
		ClassLoader cl = RuleExtractor.class.getClassLoader();
		URI uri = cl.getResource("avaliacao.txt").toURI();

//		StringBuilder resultado = new StringBuilder();

		try (Stream<String> stream = Files.lines(Paths.get(uri))) {
			stream.forEach(line -> {
				List<String> ngramas = new ArrayList<>();
				
				limpa(ngramas, line);
				
				ngramas.stream().forEach(System.out::println);
			});
		}

//		try {
//			Files.write(Paths.get("C:/java/avaliacao_corrigida.txt"), resultado.toString().getBytes());
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
	}

	public static String limpa(List<String> ngramas, String sentenca) {
		String retorno = sentenca;
		String pattern = "(.*np\\[[^\\]]*)(np\\[[^\\]]*\\])(.*\\].*)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(sentenca);
		
		if (m.find()) {
			ngramas.add(m.group(2));
			retorno = m.group(1) + m.group(2).replaceAll("np\\[", "").replaceAll("\\]", "") + m.group(3);
			retorno = limpa(ngramas, retorno);
		} else {
			String pattern2 = "(np\\[[^\\]]*\\])";
			Pattern r2 = Pattern.compile(pattern2);
			Matcher m2 = r2.matcher(sentenca);
			
			while (m2.find()) {
				ngramas.add(m2.group(1));
			}
		}
		
		return retorno;
	}
	
	public static String limpaFuncionando(String sentenca) {
		String retorno = sentenca;
		String pattern = "(.*np\\[[^\\]]*)(np\\[[^\\]]*\\])(.*\\].*)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(sentenca);

		if (m.find()) {
			System.out.println(m.group(2));
			retorno = m.group(1) + m.group(2).replaceAll("np\\[", "").replaceAll("\\]", "") + m.group(3);
			retorno = limpaFuncionando(retorno);
		} else {
			String pattern2 = "(np\\[[^\\]]*\\])";
			Pattern r2 = Pattern.compile(pattern2);
			Matcher m2 = r2.matcher(sentenca);

			while (m2.find()) {
				System.out.println(m2.group(1));
			}
		}

		return retorno;
	}
}
