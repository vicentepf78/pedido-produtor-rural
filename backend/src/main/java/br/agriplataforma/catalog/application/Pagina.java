package br.agriplataforma.catalog.application;

import java.util.List;

public record Pagina<T>(List<T> itens, int pagina, int tamanhoPagina, long total) {}
