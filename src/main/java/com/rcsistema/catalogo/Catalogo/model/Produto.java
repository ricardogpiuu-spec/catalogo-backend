package com.rcsistema.catalogo.Catalogo.model;

import com.rcsistema.catalogo.Catalogo.dto.ProdutoResquestDto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Document(collection = "produtos")
public class Produto {

    @Id
    private String id;

    private String title;
    private String imagem;
    private Double preco;

    private String publicId; // 🔥 ADICIONE ISSO

    public Produto(ProdutoResquestDto data){

        this.imagem = data.imagem();
        this.preco = data.preco();
        this.title = data.title();
    }
}