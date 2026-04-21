package com.rcsistema.catalogo.Catalogo.controller;

import com.rcsistema.catalogo.Catalogo.dto.ProdutoResposivedto;
import com.rcsistema.catalogo.Catalogo.model.Pedidos;
import com.rcsistema.catalogo.Catalogo.model.Produto;
import com.rcsistema.catalogo.Catalogo.repository.PedidoRepository;
import com.rcsistema.catalogo.Catalogo.repository.ProdutoRepository;
import com.rcsistema.catalogo.Catalogo.service.CloudinaryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    @Autowired
    private ProdutoRepository repository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private PedidoRepository pedidoRepository;

    // =====================================
    // LISTAR
    // =====================================
    @GetMapping
    public List<ProdutoResposivedto> getAll() {
        return repository.findAll()
                .stream()
                .map(ProdutoResposivedto::new)
                .toList();
    }

    // =====================================
    // CRIAR
    // =====================================
    @PostMapping
    public ProdutoResposivedto saveProduto(
            @RequestParam String title,
            @RequestParam String preco,
            @RequestParam(required = false) String precoAntigo,
            @RequestParam(required = false) String badge,
            @RequestParam(required = false) String textoOferta,
            @RequestParam(required = false) MultipartFile file,
            @RequestParam(required = false) String imageUrl
    ) {

        Double valorPreco = Double.parseDouble(preco.replace(",", "."));
        Double valorAntigo = 0.0;

        if (precoAntigo != null && !precoAntigo.isBlank()) {
            valorAntigo = Double.parseDouble(precoAntigo.replace(",", "."));
        }

        String finalImage;
        String publicId = null;

        if (file != null && !file.isEmpty()) {
            var upload = cloudinaryService.uploadFile(file);

            finalImage = upload.get("secure_url").toString();
            publicId = upload.get("public_id").toString();

        } else if (imageUrl != null && !imageUrl.isBlank()) {
            finalImage = imageUrl;

        } else {
            throw new RuntimeException("Imagem obrigatória");
        }

        Produto produto = new Produto();

        produto.setTitle(title);
        produto.setPreco(valorPreco);
        produto.setPrecoAntigo(valorAntigo);
        produto.setBadge(badge);
        produto.setTextoOferta(textoOferta);

        // 🔥 LISTA DE IMAGENS
        produto.setImagens(List.of(finalImage));

        produto.setPublicId(publicId);

        Produto salvo = repository.save(produto);

        return new ProdutoResposivedto(salvo);
    }

    // =====================================
    // EDITAR
    // =====================================
    @PutMapping("/{id}")
    public ProdutoResposivedto updateProduto(
            @PathVariable String id,
            @RequestParam String title,
            @RequestParam String preco,
            @RequestParam(required = false) String precoAntigo,
            @RequestParam(required = false) String badge,
            @RequestParam(required = false) String textoOferta,
            @RequestParam(required = false) MultipartFile file,
            @RequestParam(required = false) String imageUrl
    ) {

        Produto produto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Double valorPreco = Double.parseDouble(preco.replace(",", "."));

        Double valorAntigo = 0.0;
        if (precoAntigo != null && !precoAntigo.isBlank()) {
            valorAntigo = Double.parseDouble(precoAntigo.replace(",", "."));
        }

        String finalImage = produto.getImagens().get(0);
        String publicId = produto.getPublicId();

        if (file != null && !file.isEmpty()) {

            if (publicId != null) {
                cloudinaryService.deleteFile(publicId);
            }

            var upload = cloudinaryService.uploadFile(file);

            finalImage = upload.get("secure_url").toString();
            publicId = upload.get("public_id").toString();

        } else if (imageUrl != null && !imageUrl.isBlank()) {
            finalImage = imageUrl;
        }

        produto.setTitle(title);
        produto.setPreco(valorPreco);
        produto.setPrecoAntigo(valorAntigo);
        produto.setBadge(badge);
        produto.setTextoOferta(textoOferta);

        produto.setImagens(List.of(finalImage));
        produto.setPublicId(publicId);

        Produto atualizado = repository.save(produto);

        return new ProdutoResposivedto(atualizado);
    }

    // =====================================
    // DELETAR
    // =====================================
    @DeleteMapping("/{id}")
    public void deleteProduto(@PathVariable String id) {

        Produto produto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (produto.getPublicId() != null) {
            cloudinaryService.deleteFile(produto.getPublicId());
        }

        repository.deleteById(id);
    }
}