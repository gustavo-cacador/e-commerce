package br.com.gustavo.ecommerce.dto;

import br.com.gustavo.ecommerce.entities.Usuario;

public class ClienteDTO {

    private Long id;
    private String nome;

    public ClienteDTO() {
    }

    public ClienteDTO(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public ClienteDTO(Usuario entity) {
        id = entity.getId();
        nome = entity.getNome();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
