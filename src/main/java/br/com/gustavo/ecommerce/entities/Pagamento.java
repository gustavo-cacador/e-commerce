package br.com.gustavo.ecommerce.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;


@Entity
@Table(name = "tb_pagamento")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // O id que vai gerar do Pagamento é o mesmo id do pedido_id

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant momento; // momento em que foi feito o pagamento

    @OneToOne
    @MapsId
    private Order pedido; // O id que vai gerar do Pagamento é o mesmo id do pedido_id

    public Pagamento() {
    }

    public Pagamento(Long id, Instant momento, Order pedido) {
        this.id = id;
        this.momento = momento;
        this.pedido = pedido;
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

    public Order getPedido() {
        return pedido;
    }

    public void setPedido(Order pedido) {
        this.pedido = pedido;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Pagamento pagamento = (Pagamento) o;
        return Objects.equals(id, pagamento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
