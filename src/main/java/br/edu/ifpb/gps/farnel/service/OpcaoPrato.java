package br.edu.ifpb.gps.farnel.service;

import br.edu.ifpb.gps.farnel.model.ContratoEmpresarial.DivisaoRefeicao;
import br.edu.ifpb.gps.farnel.model.Prato;

/** Prato do cardápio com a divisão do valor (empresa x colaborador) pela subvenção vigente. */
public record OpcaoPrato(Prato prato, DivisaoRefeicao divisao) {
}
