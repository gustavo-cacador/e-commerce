package br.com.gustavo.ecommerce.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tb_pedido")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant momento; // momento em que o pedido foi feito

    private OrderStatus status;


    // Muitos Pedidos para um Usuário
    @ManyToOne
    @JoinColumn(name = "client_id")
    private User cliente;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Payment payment;

    @OneToMany(mappedBy = "id.pedido")
    private Set<OrderItem> items = new HashSet<>();

    public Order() {
    }

    public Order(Long id, Instant momento, OrderStatus status, User cliente, Payment payment) {
        this.id = id;
        this.momento = momento;
        this.status = status;
        this.cliente = cliente;
        this.payment = payment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getMomento() {
        return momento;
    }

    public void setMomento(Instant momento) {
        this.momento = momento;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public User getCliente() {
        return cliente;
    }

    public void setCliente(User cliente) {
        this.cliente = cliente;
    }

    public Payment getPagamento() {
        return payment;
    }

    public void setPagamento(Payment payment) {
        this.payment = payment;
    }

    public Set<OrderItem> getItems() {
        return items;
    }

    public List<Product> getProdutos() {
        return items.stream().map(x -> x.getProduto()).toList();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order pedido = (Order) o;
        return Objects.equals(id, pedido.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
