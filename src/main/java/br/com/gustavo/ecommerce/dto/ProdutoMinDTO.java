package br.com.gustavo.ecommerce.dto;

import br.com.gustavo.ecommerce.entities.Produto;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ProdutoMinDTO {

    private Long id;
    private String nome;
    private Double preco;
    private String imgUrl;

    public ProdutoMinDTO() {
    }

    public ProdutoMinDTO(Long id, String nome, Double preco, String imgUrl) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.imgUrl = imgUrl;
    }

    public ProdutoMinDTO(Produto entity) {
        id = entity.getId();
        nome = entity.getNome();
        preco = entity.getPreco();
        imgUrl = entity.getImgUrl();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Double getPreco() {
        return preco;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
