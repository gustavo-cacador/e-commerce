package br.com.gustavo.ecommerce.entities;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "tb_pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant momento; // momento em que o pedido foi feito

    private PedidoStatus status;


    // Muitos Pedidos para um Usuário
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Usuario cliente;
}
