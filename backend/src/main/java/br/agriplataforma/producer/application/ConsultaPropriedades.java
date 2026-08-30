package br.agriplataforma.producer.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultaPropriedades {

	List<ResumoPropriedade> listarDoAutenticado();

	Optional<ResumoPropriedade> buscarPropria(UUID idPropriedade);

	UUID idProdutorDoAutenticado();
}
