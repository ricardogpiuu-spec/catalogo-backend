package com.rcsistema.catalogo.Catalogo.repository;

import com.rcsistema.catalogo.Catalogo.model.Produto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProdutoRepository extends MongoRepository<Produto, String> {
}
