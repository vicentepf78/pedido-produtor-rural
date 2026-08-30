package br.agriplataforma;

public final class ApoioCsrf {

	private ApoioCsrf() {}

	public static String token(String corpo) {
		String chave = "\"token\":\"";
		int de = corpo.indexOf(chave);
		if (de < 0) {
			throw new AssertionError("token CSRF ausente: " + corpo);
		}
		de += chave.length();
		return corpo.substring(de, corpo.indexOf('"', de));
	}
}
