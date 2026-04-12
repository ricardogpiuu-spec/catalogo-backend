package com.rcsistema.catalogo.Catalogo.model;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pedidos")
@Data
public class Pedidos {

    @Id
    private String id;

    private String produtoId;
    private String texto;
    private String imagem;
    private String mockupUrl;

    // getters e setters
}