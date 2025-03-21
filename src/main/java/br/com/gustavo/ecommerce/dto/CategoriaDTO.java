package br.com.gustavo.ecommerce.dto;

import br.com.gustavo.ecommerce.entities.Categoria;

public class CategoriaDTO {

    private Long id;
    private String name;

    public CategoriaDTO() {

    }

    public CategoriaDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public CategoriaDTO(Categoria entity) {
        id = entity.getId();
        name = entity.getName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
