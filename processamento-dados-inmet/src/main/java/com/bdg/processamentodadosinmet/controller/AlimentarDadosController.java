package com.bdg.processamentodadosinmet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bdg.processamentodadosinmet.service.AlimentarDadosService;

@CrossOrigin
@RestController
@RequestMapping("/alimentar-dados")
public class AlimentarDadosController {

	@Autowired
	private AlimentarDadosService alimentarDadosService;

	@PostMapping(path = "/alimentar-dados-estacoes")
	public ResponseEntity<Object> alimentarDadosEstacoes() {
		try {
			alimentarDadosService.alimentarDadosEstacoes();
			return new ResponseEntity<Object>("OK", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erro não tratado: " + e.getMessage());
		}
	}
	
	@PostMapping(path = "/alimentar-dados-leitura-estacoes")
	public ResponseEntity<Object> alimentarDadosLeituraEstacoes() {
		try {
			alimentarDadosService.alimentarDadosLeituraEstacoes();
			return new ResponseEntity<Object>("OK", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erro não tratado: " + e.getMessage());
		}
	}
}
