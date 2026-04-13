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
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "imageUrl", required = false) String imageUrl
    ) {

        Double preco = Double.parseDouble(precoStr.replace(",", "."));

        String finalImage;
        String publicId = null;

        if (file != null && !file.isEmpty()) {

            var upload = cloudinaryService.uploadFile(file);

             finalImage = upload.get("secure_url").toString();
            //finalImage = cloudinaryService.uploadFile(file); // ✅
             publicId = upload.get("public_id").toString();

        } else if (imageUrl != null && !imageUrl.isEmpty()) {

            finalImage = imageUrl;

        } else {
            throw new RuntimeException("Imagem obrigatória");
        }

        Produto produto = new Produto();
        produto.setTitle(title);
        produto.setPreco(preco);
        produto.setImagem(finalImage); // ✅ CORRETO
        produto.setPublicId(publicId); // 🔥 SALVA ISSO

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
            @RequestParam("title") String title,
            @RequestParam("preco") String precoStr,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "imageUrl", required = false) String imageUrl
    ) {

        Produto produto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Double preco = Double.parseDouble(precoStr.replace(",", "."));

        String finalImage = produto.getImagem();
        String publicId = produto.getPublicId(); // 🔥 importante

        // 🔥 se enviou nova imagem
        if (file != null && !file.isEmpty()) {

            // 🔥 deleta antiga
            if (publicId != null) {
                cloudinaryService.deleteFile(publicId);
            }

            var upload = cloudinaryService.uploadFile(file);

            // 🔥 sobe nova imagem
            finalImage = upload.get("secure_url").toString();
            publicId = upload.get("public_id").toString();

        } else if (imageUrl != null && !imageUrl.isEmpty()) {
            finalImage = imageUrl;
        }

        produto.setTitle(title);
        produto.setPreco(preco);
        produto.setImagem(finalImage);
        produto.setPublicId(publicId); // ✅ agora correto
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

        // 🔥 PEGA IMAGEM DO CLIENTE (SEM URL COMPLETA)
        String imagemCliente = null;

        if (imageUrl != null && !imageUrl.isEmpty()) {
            imagemCliente = imageUrl
                    .split("/upload/")[1]
                    .replaceAll("^v\\d+/", "");
        }

        // 🔥 IMAGEM BASE DO PRODUTO (CANECA)
        String baseImage = produto.getImagem()
                .split("/upload/")[1]
                .replaceAll("^v\\d+/", "");




        // 🔥 MONTA MOCKUP
        String mockupUrl = "https://res.cloudinary.com/" + cloudName + "/image/upload/";

        String transform = "";

        if (imagemCliente != null) {
            transform =
                    "l_" + imagemCliente +
                            ",w_300,h_300,c_fill,g_auto" +
                            ",r_20" +
                            ",e_distort:0:0:300:20:280:180:20:160" +
                            ",x_0,y_10" +
                            "/" ;

        }

        mockupUrl += transform;
        // 🔥 TEXTO
        if (texto != null && !texto.isEmpty()) {
            String textoFormatado = texto.replace(" ", "%20");
            mockupUrl +=
                    "l_text:Arial_30:" + textoFormatado +
                            ",co_black,g_south,y_0/"; // 🔥 texto embaixo
        }


        // 🔥 FINALIZA COM IMAGEM BASE
        mockupUrl += baseImage;

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



