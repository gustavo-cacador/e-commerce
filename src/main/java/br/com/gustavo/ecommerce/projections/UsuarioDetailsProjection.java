package br.com.gustavo.ecommerce.projections;

public interface UsuarioDetailsProjection {

    String getUsername();
    String getPassword();
    Long getRoleId();
    String getAuthority();
}
