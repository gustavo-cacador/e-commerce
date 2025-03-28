package br.com.gustavo.ecommerce.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "tb_item_pedido")
public class OrderItem {

    @EmbeddedId
    private ItemPedidoPK id = new ItemPedidoPK();

    private Integer quantity;
    private Double price;

    public OrderItem() {
    }

    public OrderItem(Order pedido, Product produto, Integer quantity, Double price) {
        id.setPedido(pedido);
        id.setProduto(produto);
        this.quantity = quantity;
        this.price = price;
    }

    public Order getPedido() {
        return id.getPedido();
    }

    public void setPedido(Order pedido) {
        id.setPedido(pedido);
    }

    public Product getProduto() {
        return id.getProduto();
    }

    public void setProduto(Product produto) {
        id.setProduto(produto);
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem that = (OrderItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
