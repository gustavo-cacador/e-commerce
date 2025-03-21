package br.com.gustavo.ecommerce.dto;

import br.com.gustavo.ecommerce.entities.ItemPedido;

public class ItemPedidoDTO {

    private Long produtoId;
    private String nome;
    private Double price;
    private Integer quantity;
    private String imgUrl;

    public ItemPedidoDTO() {
    }

    public ItemPedidoDTO(Long produtoId, String nome, Double price, Integer quantity, String imgUrl) {
        this.produtoId = produtoId;
        this.nome = nome;
        this.price = price;
        this.quantity = quantity;
        this.imgUrl = imgUrl;
    }

    public ItemPedidoDTO(ItemPedido entity) {
        produtoId = entity.getProduto().getId();
        nome = entity.getProduto().getName();
        price = entity.getPrice();
        quantity = entity.getQuantity();
        imgUrl = entity.getProduto().getImgUrl();
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public String getNome() {
        return nome;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public Double getSubTotal() {
        return price * quantity;
    }
}
