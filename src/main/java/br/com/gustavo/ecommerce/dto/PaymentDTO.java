package br.com.gustavo.ecommerce.dto;

import br.com.gustavo.ecommerce.entities.Payment;

import java.time.Instant;

public class PaymentDTO {

    private Long id;
    private Instant momento;

    public PaymentDTO() {
    }

    public PaymentDTO(Long id, Instant momento) {
        this.id = id;
        this.momento = momento;
    }

    public PaymentDTO(Payment entity) {
        id = entity.getId();
        momento = entity.getMomento();
    }

    public Long getId() {
        return id;
    }

    public Instant getMomento() {
        return momento;
    }
}
