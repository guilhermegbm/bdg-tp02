package com.bdg.processamentodadosinmet.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bdg.processamentodadosinmet.entity.Estacao;
import com.bdg.processamentodadosinmet.entity.LeituraEstacao;
import com.bdg.processamentodadosinmet.repository.EstacaoRepository;
import com.bdg.processamentodadosinmet.repository.LeituraEstacaoRepository;

@Service
public class AlimentarDadosService {

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private EstacaoRepository estacaoRepository;

	@Autowired
	private LeituraEstacaoRepository leituraEstacaoRepository;

	private static final SimpleDateFormat SDF_DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat SDF_YYYY_MM_DD_HHMMSS = new SimpleDateFormat("yyyy/MM/dd-HHmmSS");

	public Estacao encontrarEstacaoPorCodigo(String codigoEstacao) throws Exception {
		List<Estacao> estacoesPorCodigo = this.estacaoRepository.findEstacaoByCodigo(codigoEstacao);

		if (estacoesPorCodigo == null || estacoesPorCodigo.isEmpty()) {
			throw new Exception("Não foi encontrada uma estação com o código " + codigoEstacao);
		}

		if (estacoesPorCodigo.size() > 1) {
			throw new Exception("Foi encontrada mais de uma estação com o código " + codigoEstacao);
		}

		return estacoesPorCodigo.get(0);
	}

	@Transactional(rollbackFor = { Exception.class })
	public void alimentarDadosEstacoes() throws Exception {
		String classpathCatalogoEstAuto = "classpath:static/dados_estacoes/CatalogoEstacoesAutomaticas.csv";
		this.processarArquivoCatalogoEstacoes(classpathCatalogoEstAuto);

		String classpathCatalogoEstConv = "classpath:static/dados_estacoes/CatalogoEstacoesConvencionais.csv";
		this.processarArquivoCatalogoEstacoes(classpathCatalogoEstConv);
	}

	private void processarArquivoCatalogoEstacoes(String classpathCatalogoEstAuto) throws Exception {
		File fileCSV = resourceLoader.getResource(classpathCatalogoEstAuto)
				.getFile();

		try (BufferedReader br = new BufferedReader(new FileReader(fileCSV))) {
			String linha;

			boolean primeiraLinha = true;
			while ((linha = br.readLine()) != null) {
				if (primeiraLinha) {
					primeiraLinha = false;
					continue;
				}

				processarLinhaEInserirEstacao(linha);
			}
		}

	}

	private void processarLinhaEInserirEstacao(String line) throws ParseException {
		String[] colunas = line.split(";");

		String nomeCidade = colunas[0];
		String siglaEstado = colunas[1];
		String situacao = colunas[2];
		BigDecimal latitude = new BigDecimal(colunas[3].replace(",", "."));
		BigDecimal longitude = new BigDecimal(colunas[4].replace(",", "."));
		BigDecimal altitude = new BigDecimal(colunas[5].replace(",", "."));
		Date dataInicioOperacao = SDF_DD_MM_YYYY.parse(colunas[6]);
		String codigoEstacao = colunas[7];

		Estacao e = new Estacao();

		e.setNomeCidade(nomeCidade);
		e.setSiglaEstado(siglaEstado);
		e.setSituacao(situacao);
		e.setLatitude(latitude);
		e.setLongitude(longitude);
		e.setAltitude(altitude);
		e.setDataInicioOperacao(dataInicioOperacao);
		e.setCodigoEstacao(codigoEstacao);

		this.estacaoRepository.save(e);
	}

	@Transactional(rollbackFor = { Exception.class })
	public void alimentarDadosLeituraEstacoes() throws Exception {
		HashMap<String, Estacao> mapEstacaoPorCodigo = this.configurarMapEstacaoPorCodigo();

		File diretorioBase = resourceLoader.getResource("classpath:static/dados_leitura_estacoes")
				.getFile();

		for (File subDiretorioAno : diretorioBase.listFiles()) {
			if (!subDiretorioAno.getName()
					.equals("2023")) {
				continue;
			}

			File[] arquivos = subDiretorioAno.listFiles();
			int qtdeArquivos = arquivos.length;

			System.out.println("\n********** Alimentando os dados para o ano de " + subDiretorioAno.getName() + " com " + qtdeArquivos + " arquivos. **********");

			int contadorArquivo = 1;
			for (File arquivo : arquivos) {
				/*if (!arquivo.getName()
						.equals("INMET_CO_DF_A001_BRASILIA_01-01-2019_A_31-12-2019.CSV")) {
					break;
				}*/

				if (contadorArquivo % 50 == 0) {
					System.out.println("Processou " + contadorArquivo + "/" + qtdeArquivos);
				}

				processarArquivoDadosLeituraEstacao(arquivo, mapEstacaoPorCodigo);

				contadorArquivo++;
			}
		}
	}

	private HashMap<String, Estacao> configurarMapEstacaoPorCodigo() {
		HashMap<String, Estacao> mapEstacaoPorCodigo = new HashMap<>();
		Iterable<Estacao> todasAsEstacoes = this.estacaoRepository.findAll();

		for (Estacao estacao : todasAsEstacoes) {
			mapEstacaoPorCodigo.put(estacao.getCodigoEstacao(), estacao);
		}

		return mapEstacaoPorCodigo;
	}

	private void processarArquivoDadosLeituraEstacao(File arquivo, HashMap<String, Estacao> mapEstacaoPorCodigo) throws Exception {
		try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {

			Estacao estacao = null;
			BigDecimal latitude = null;
			BigDecimal longitude = null;
			String codigoEstacaoNaoEncontrada = null;

			String linha;
			int contadorLinhaArquivo = 0;
			while ((linha = br.readLine()) != null) {
				contadorLinhaArquivo++;

				if (contadorLinhaArquivo == 2) {
					// Verificando se é de MG
					String uf = linha.split(";")[1];
					if (!"MG".equals(uf)) {
						return;
					}
				} else if (contadorLinhaArquivo == 4) {
					String codigoEstacao = linha.split(";")[1];
					estacao = mapEstacaoPorCodigo.get(codigoEstacao);
					if (estacao == null) {
						//throw new Exception("Estação não encontrada para o código " + codigoEstacao);
						//System.out.println("Estação não encontrada para o código " + codigoEstacao);
						codigoEstacaoNaoEncontrada = codigoEstacao;
					}
				} else if (contadorLinhaArquivo == 5) {
					latitude = new BigDecimal(linha.split(";")[1].replace(",", "."));
				} else if (contadorLinhaArquivo == 6) {
					longitude = new BigDecimal(linha.split(";")[1].replace(",", "."));
				} else if (contadorLinhaArquivo > 9) {
					String[] colunas = linha.split(";");

					Date dataHora = SDF_YYYY_MM_DD_HHMMSS.parse(colunas[0] + "-" + colunas[1].substring(0, 4) + "00");
					BigDecimal temperaturaMaximaLida = null;
					BigDecimal temperaturaMinimaLida = null;

					try {
						if (colunas[9] != null && !colunas[9].isBlank()) {
							temperaturaMaximaLida = new BigDecimal(colunas[9].replace(",", "."));
						}

						if (colunas[10] != null && !colunas[10].isBlank()) {
							temperaturaMinimaLida = new BigDecimal(colunas[10].replace(",", "."));
						}
					} catch (ArrayIndexOutOfBoundsException exception) {
						//System.out.println("Erro linha " + contadorLinhaArquivo + ": " + exception.getMessage());
					} catch (Exception exception) {
						System.out.println("Erro linha " + contadorLinhaArquivo + ": " + exception.getMessage());
					}

					LeituraEstacao le = new LeituraEstacao();

					le.setDataHoraLeitura(dataHora);
					le.setTemperaturaMaximaLida(temperaturaMaximaLida);
					le.setTemperaturaMinimaLida(temperaturaMinimaLida);
					le.setEstacao(estacao);
					le.setCodigoEstacaoNaoEncontrada(codigoEstacaoNaoEncontrada);
					le.setLatitude(latitude);
					le.setLongitude(longitude);

					this.leituraEstacaoRepository.save(le);
				}

			}
		}
	}
}
