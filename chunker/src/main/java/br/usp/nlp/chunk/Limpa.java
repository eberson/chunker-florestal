package br.usp.nlp.chunk;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import br.usp.nlp.chunk.rule.extraction.RuleExtractor;

public class Limpa {

	public static void main(String[] args) throws Exception {
		ClassLoader cl = RuleExtractor.class.getClassLoader();
		URI uri = cl.getResource("avaliacao.txt").toURI();
		
		StringBuilder resultado = new StringBuilder();
		
		try(Stream<String> stream = Files.lines(Paths.get(uri))){
			stream.forEach(line -> {
				resultado.append(limpa(line)).append("\n");
			});
		}
		
		try {
			Files.write(Paths.get("C:/java/avaliacao_corrigida.txt"), resultado.toString().getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String limpa(String sentenca) {
		String retorno = sentenca;
		String pattern = "(.*np\\[[^\\]]*)(np\\[[^\\]]*\\])(.*\\].*)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(sentenca);

		if (m.find()) {
			// System.out.println("Found value: " + m.group(0));
//			System.out.println("um: " + m.group(1));
//			System.out.println("dois: " + m.group(2));
//			System.out.println("tres: " + m.group(3));

			retorno = m.group(1) + m.group(2).replaceAll("np\\[", "").replaceAll("\\]", "") + m.group(3);
			System.out.println("----------------------------" + retorno);
			retorno = limpa(retorno);
		} else {
			System.out.println("NO MATCH");
		}
		return retorno;
	}
}
