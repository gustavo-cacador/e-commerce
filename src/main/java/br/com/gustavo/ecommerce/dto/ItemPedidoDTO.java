package br.com.gustavo.ecommerce.dto;

import br.com.gustavo.ecommerce.entities.ItemPedido;

public class ItemPedidoDTO {

    private Long produtoId;
    private String nome;
    private Double preco;
    private Integer quantidade;

    public ItemPedidoDTO() {
    }

    public ItemPedidoDTO(Long produtoId, String nome, Double preco, Integer quantidade) {
        this.produtoId = produtoId;
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
    }

    public ItemPedidoDTO(ItemPedido entity) {
        produtoId = entity.getProduto().getId();
        nome = entity.getProduto().getNome();
        preco = entity.getPreco();
        quantidade = entity.getQuantidade();
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

    public Double getSubTotal() {
        return preco * quantidade;
    }
}
