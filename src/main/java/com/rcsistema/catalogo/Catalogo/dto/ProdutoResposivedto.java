package com.rcsistema.catalogo.Catalogo.dto;

import com.rcsistema.catalogo.Catalogo.model.Produto;

import java.util.List;

public record ProdutoResposivedto(String id, String title, List<String> imagens, Double preco, Double precoAntigo, String badge,
                                  String textoOferta) {

public ProdutoResposivedto (Produto produto){
    this(produto.getId(), produto.getTitle(), produto.getImagens(), produto.getPreco(), produto.getPrecoAntigo(),
            produto.getBadge(), produto.getTextoOferta());
}
}
