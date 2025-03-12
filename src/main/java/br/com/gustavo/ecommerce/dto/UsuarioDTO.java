package br.com.gustavo.ecommerce.dto;


import br.com.gustavo.ecommerce.entities.Usuario;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDTO {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;

    private List<String> roles = new ArrayList<>();

    public UsuarioDTO(Usuario entity) {
        id = entity.getId();
        nome = entity.getNome();
        email = entity.getEmail();
        telefone = entity.getTelefone();
        dataNascimento = entity.getDataNascimento();

        // obtendo autoridade ou permissao para um usuario
        for (GrantedAuthority role: entity.getRoles()) {
            roles.add(role.getAuthority());
        }
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public List<String> getRoles() {
        return roles;
    }
}
