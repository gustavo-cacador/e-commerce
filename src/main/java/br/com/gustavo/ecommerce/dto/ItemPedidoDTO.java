package br.com.gustavo.ecommerce.dto;

import br.com.gustavo.ecommerce.entities.ItemPedido;

public class ItemPedidoDTO {

    private Long id;
    private String nome;
    private Double preco;
    private Integer quantidade;

    public ItemPedidoDTO() {
    }

    public ItemPedidoDTO(Long id, String nome, Double preco, Integer quantidade) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
    }

    public ItemPedidoDTO(ItemPedido entity) {
        id = entity.getProduto().getId();
        nome = entity.getProduto().getNome();
        preco = entity.getProduto().getPreco();
        quantidade = entity.getQuantidade();
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

    public Integer getQuantidade() {
        return quantidade;
    }

    public Double getSubTotal() {
        return preco * quantidade;
    }
}
