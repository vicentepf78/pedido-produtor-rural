package br.agriplataforma.order.application;

import java.util.List;

public record PaginaPedido<T>(List<T> itens, int pagina, int tamanhoPagina, long total) {}
