package com.rcsistema.catalogo.Catalogo.model;

import com.rcsistema.catalogo.Catalogo.dto.ProdutoResquestDto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

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
    private List<String> imagens;
    private Double precoAntigo;
    private Double preco;
    private String badge;
    private String textoOferta;

    private String publicId; // 🔥 ADICIONE ISSO

    public Produto(ProdutoResquestDto data){

        this.imagens = data.imagens();
        this.preco = data.preco();
        this.precoAntigo = data.precoAntigo();
        this.title = data.title();
        this.badge = data.badge();
        this.textoOferta = data.textoOferta();
    }
}