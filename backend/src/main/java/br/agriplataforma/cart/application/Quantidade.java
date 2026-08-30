package br.agriplataforma.cart.application;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class Quantidade {

	private Quantidade() {}

	public static int exigirInteiraPositiva(BigDecimal valor) {
		if (valor == null || valor.signum() <= 0) {
			throw invalida();
		}
		try {
			BigInteger inteiro = valor.stripTrailingZeros().toBigIntegerExact();
			if (inteiro.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
				throw invalida();
			}
			return inteiro.intValueExact();
		} catch (ArithmeticException excecao) {
			throw invalida();
		}
	}

	private static ExcecaoCarrinho invalida() {
		return new ExcecaoCarrinho("QUANTIDADE_INVALIDA", "Informe uma quantidade inteira positiva.");
	}
}
