package br.com.gustavo.ecommerce.projections;

public interface UsuarioDetailsProjection {

    String getUsername();
    String getSenha();
    Long getRoleId();
    String getAuthority();
}
