package br.com.gustavo.ecommerce.entities;

import jakarta.persistence.*;

import java.time.Instant;


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
    private Pedido pedido; // O id que vai gerar do Pagamento é o mesmo id do pedido_id

    public Pagamento() {
    }

    public Pagamento(Long id, Instant momento, Pedido pedido) {
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

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
}
