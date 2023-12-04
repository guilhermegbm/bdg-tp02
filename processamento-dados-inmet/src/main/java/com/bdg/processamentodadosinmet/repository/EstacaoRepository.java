package com.bdg.processamentodadosinmet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.bdg.processamentodadosinmet.entity.Estacao;

public interface EstacaoRepository extends CrudRepository<Estacao, String> {

	@Query("from Estacao e where e.codigoEstacao = :codigoEstacao")
	public List<Estacao> findEstacaoByCodigo(@Param("codigoEstacao") String codigoEstacao);
}
