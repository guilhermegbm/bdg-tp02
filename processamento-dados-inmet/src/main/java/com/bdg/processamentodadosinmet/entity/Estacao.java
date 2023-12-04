package com.bdg.processamentodadosinmet.entity;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "estacao", schema = "public")
@Data
public class Estacao {

	@Id
	@Column(name = "codigo_estacao", nullable = false)
	private String codigoEstacao;

	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio_operacao", nullable = false)
	private Date dataInicioOperacao;

	@Column(name = "situacao", nullable = false)
	private String situacao;

	@Column(name = "nome_cidade", nullable = false)
	private String nomeCidade;

	@Column(name = "sigla_estado", nullable = false)
	private String siglaEstado;

	@Column(name = "latitude", nullable = false)
	private BigDecimal latitude;

	@Column(name = "longitude", nullable = false)
	private BigDecimal longitude;

	@Column(name = "altitude", nullable = false)
	private BigDecimal altitude;

}
