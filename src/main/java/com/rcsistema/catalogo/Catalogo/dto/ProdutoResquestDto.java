package com.rcsistema.catalogo.Catalogo.dto;

import java.util.List;

public record ProdutoResquestDto(String title, List<String> imagens,
                                 Double preco, Double precoAntigo, String badge,
                                 String textoOferta) {
}
