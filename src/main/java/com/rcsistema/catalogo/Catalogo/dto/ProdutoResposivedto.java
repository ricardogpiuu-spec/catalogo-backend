package com.rcsistema.catalogo.Catalogo.dto;

import com.rcsistema.catalogo.Catalogo.model.Produto;

public record ProdutoResposivedto(String id, String title, String imagem, Double preco, Double precoAntigo) {

public ProdutoResposivedto (Produto produto){
    this(produto.getId(), produto.getTitle(), produto.getImagem(), produto.getPreco(), produto.getPrecoAntigo());
}
}
