package br.com.gustavo.ecommerce.dto;

import br.com.gustavo.ecommerce.entities.Produto;
import jakarta.persistence.Column;

public class ProdutoDTO {

    private Long id;
    private String nome;
    private String descricao;
    private Double preco;
    private String imgUrl;

    // Como não vamos gerar Set, pois queremos inserir os dados, n iremos adicionar um construtor vazio, apenas o construtor com os parâmetros (atributos).

    public ProdutoDTO(Long id, String nome, String descricao, Double preco, String imgUrl) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.imgUrl = imgUrl;
    }

    public ProdutoDTO(Produto entity) {
        id = entity.getId();
        nome = entity.getNome();
        descricao = entity.getDescricao();
        preco = entity.getPreco();
        imgUrl = entity.getImgUrl();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
