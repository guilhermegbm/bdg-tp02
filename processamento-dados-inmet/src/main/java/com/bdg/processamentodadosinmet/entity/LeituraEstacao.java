package com.bdg.processamentodadosinmet.entity;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "leitura_estacao", schema = "public")
@SequenceGenerator(name = "seq_id_leitura_estacao", sequenceName = "seq_id_leitura_estacao", schema = "public", allocationSize = 1)
@Data
public class LeituraEstacao {

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_id_leitura_estacao")
	private Long id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_hora_leitura", nullable = false)
	private Date dataHoraLeitura;

	@Column(name = "temperatura_maxima_lida", nullable = true)
	private BigDecimal temperaturaMaximaLida;

	@Column(name = "temperatura_minima_lida", nullable = true)
	private BigDecimal temperaturaMinimaLida;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo_estacao", nullable = true)
	private Estacao estacao;

	@Column(name = "codigo_estacao_nao_encontrada", nullable = true)
	private String codigoEstacaoNaoEncontrada;

	@Column(name = "latitude", nullable = false)
	private BigDecimal latitude;

	@Column(name = "longitude", nullable = false)
	private BigDecimal longitude;
}
