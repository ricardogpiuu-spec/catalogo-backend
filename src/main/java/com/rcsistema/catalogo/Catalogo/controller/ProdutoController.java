package com.rcsistema.catalogo.Catalogo.controller;

import com.rcsistema.catalogo.Catalogo.dto.ProdutoResposivedto;
import com.rcsistema.catalogo.Catalogo.dto.ProdutoResquestDto;
import com.rcsistema.catalogo.Catalogo.model.Pedidos;
import com.rcsistema.catalogo.Catalogo.model.Produto;
import com.rcsistema.catalogo.Catalogo.repository.PedidoRepository;
import com.rcsistema.catalogo.Catalogo.repository.ProdutoRepository;
import com.rcsistema.catalogo.Catalogo.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("produtos")

public class ProdutoController {
    private static final Logger log = LoggerFactory.getLogger(ProdutoController.class);
    @Autowired
    private ProdutoRepository repository;
    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping

    public List<ProdutoResposivedto> getAll() {

        List<ProdutoResposivedto> produtoList = repository.findAll().stream().map(ProdutoResposivedto::new).toList();
        return produtoList;
    }

    // @PostMapping
    // public void saveProduto(@RequestBody ProdutoResquestDto data) {
    // System.out.println("Recebido: " + produtoData.getTitle());
    //Produto novo = repository.save(produto);
    // Produto produtoData = new Produto(data);
    //repository.save(produtoData);
    // return;

    //}
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public ProdutoResposivedto saveProduto(
            @RequestParam("title") String title,
            @RequestParam("preco") String precoStr,
            @RequestParam("precoAntigo") String precoString,
            @RequestParam(value = "badge", required = false) String badgeString,
            @RequestParam(value = "textoOferta", required = false) String textoOfertaString,
            @RequestParam(value = "imagens", required = false) List<String> imagens,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "imageUrl", required = false) String imageUrl
    ) {

        Double preco = Double.parseDouble(precoStr.replace(",", "."));
        Double precoantigo = 0.0;

        if (precoString != null && !precoString.isBlank()) {
            precoantigo = Double.parseDouble(precoString.replace(",", "."));
        }

        String publicId = null;

        // se não veio lista, cria vazia
        if (imagens == null) {
            imagens = new ArrayList<>();
        }

        // upload arquivo principal
        if (file != null && !file.isEmpty()) {

            var upload = cloudinaryService.uploadFile(file);

            String finalImage = upload.get("secure_url").toString();
            publicId = upload.get("public_id").toString();

            imagens.add(finalImage);

        } else if (imageUrl != null && !imageUrl.isEmpty()) {

            imagens.add(imageUrl);

        }

        if (imagens.isEmpty()) {
            throw new RuntimeException("Imagem obrigatória");
        }

        Produto produto = new Produto();
        produto.setTitle(title);
        produto.setPreco(preco);
        produto.setPrecoAntigo(precoantigo);
        produto.setBadge(badgeString);
        produto.setTextoOferta(textoOfertaString);
        produto.setImagens(imagens);
        produto.setPublicId(publicId);

        Produto salvo = repository.save(produto);

        log.info("Produto salvo: {} com ID {}", salvo.getTitle(), salvo.getId());

        return new ProdutoResposivedto(salvo);
    }
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public void deleteProduto(@PathVariable String id) {

        Produto produto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // 🔥 deletar imagem do Cloudinary (se tiver)
        if (produto.getPublicId() != null) {
            cloudinaryService.deleteFile(produto.getPublicId());
        }

        repository.deleteById(id);

        log.info("Produto deletado: {}", id);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ProdutoResposivedto updateProduto(
            @PathVariable String id,
            @RequestBody Produto dados
    ) {

        Produto produto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produto.setTitle(dados.getTitle());
        produto.setPreco(dados.getPreco());
        produto.setPrecoAntigo(dados.getPrecoAntigo());
        produto.setBadge(dados.getBadge());
        produto.setTextoOferta(dados.getTextoOferta());

        // SALVA TODAS IMAGENS
        produto.setImagens(dados.getImagens());

        Produto atualizado = repository.save(produto);

        return new ProdutoResposivedto(atualizado);
    }
    @PostMapping("/mockup")
    public String gerarMockup(
            @RequestParam String produtoId,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) String imageUrl
    ) {

        Produto produto = repository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        String cloudName = "dyvec4jx4";

        // 🔥 IMAGEM CLIENTE
        String imagemCliente = null;

        if (imageUrl != null && !imageUrl.isEmpty()) {
            imagemCliente = imageUrl
                    .split("/upload/")[1]
                    .replaceAll("^v\\d+/", "");
        }

        // 🔥 PEGA PRIMEIRA IMAGEM DA LISTA
        String baseImage = produto.getImagens().get(0)
                .split("/upload/")[1]
                .replaceAll("^v\\d+/", "");

        // 🔥 URL BASE
        String mockupUrl =
                "https://res.cloudinary.com/" + cloudName + "/image/upload/";

        String transform = "";

        // 🔥 FOTO CLIENTE
        if (imagemCliente != null) {
            transform +=
                    "l_" + imagemCliente +
                            ",w_700,h_700,c_fill/";
        }

        // 🔥 TEXTO
        if (texto != null && !texto.isEmpty()) {
            String textoFormatado = texto.replace(" ", "%20");

            transform +=
                    "l_text:Arial_30:" + textoFormatado +
                            ",co_black,g_south,y_0/";
        }

        // 🔥 MONTA FINAL
        mockupUrl += transform + baseImage;

        // 🔥 SALVA PEDIDO
        Pedidos pedido = new Pedidos();
        pedido.setProdutoId(produtoId);
        pedido.setTexto(texto);
        pedido.setImagem(imagemCliente);
        pedido.setMockupUrl(mockupUrl);

        pedidoRepository.save(pedido);

        return mockupUrl;
    }

    }



