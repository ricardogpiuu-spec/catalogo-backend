package com.rcsistema.catalogo.Catalogo.repository;

import com.rcsistema.catalogo.Catalogo.model.Pedidos;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PedidoRepository extends MongoRepository<Pedidos, String> {
}
