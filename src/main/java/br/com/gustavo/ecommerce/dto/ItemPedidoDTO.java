package br.com.gustavo.ecommerce.dto;

import br.com.gustavo.ecommerce.entities.ItemPedido;

public class ItemPedidoDTO {

    private Long produtoId;
    private String nome;
    private Double preco;
    private Integer quantidade;
    private String imgUrl;

    public ItemPedidoDTO() {
    }

    public ItemPedidoDTO(Long produtoId, String nome, Double preco, Integer quantidade, String imgUrl) {
        this.produtoId = produtoId;
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
        this.imgUrl = imgUrl;
    }

    public ItemPedidoDTO(ItemPedido entity) {
        produtoId = entity.getProduto().getId();
        nome = entity.getProduto().getNome();
        preco = entity.getPreco();
        quantidade = entity.getQuantidade();
        imgUrl = entity.getProduto().getImgUrl();
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public String getNome() {
        return nome;
    }

    public Double getPreco() {
        return preco;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public Double getSubTotal() {
        return preco * quantidade;
    }
}
