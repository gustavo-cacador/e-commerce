package br.com.gustavo.ecommerce.dto;

import br.com.gustavo.ecommerce.entities.Pagamento;

import java.time.Instant;

public class PagamentoDTO {

    private Long id;
    private Instant momento;

    public PagamentoDTO() {
    }

    public PagamentoDTO(Long id, Instant momento) {
        this.id = id;
        this.momento = momento;
    }

    public PagamentoDTO(Pagamento entity) {
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
