package br.edu.ifpb.gps.farnel.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * UC09 (Consolidar Pedidos Diários) para o B2B: à meia-noite, e também quando a aplicação sobe,
 * as recorrências dos colaboradores preenchem os lotes coletivos dos próximos dias.
 */
@Component
@RequiredArgsConstructor
public class RecorrenciaAgendador {

	private static final Logger log = LoggerFactory.getLogger(RecorrenciaAgendador.class);

	private final RecorrenciaService recorrenciaService;

	@EventListener(ApplicationReadyEvent.class)
	public void aoIniciar() {
		executar();
	}

	@Scheduled(cron = "0 0 0 * * *")
	public void meiaNoite() {
		executar();
	}

	private void executar() {
		int gerados = recorrenciaService.gerarTodos();
		log.info("Recorrências B2B: {} marmita(s) gerada(s) nos lotes dos próximos {} dias.",
				gerados, RecorrenciaService.DIAS_GERADOS);
	}

}
