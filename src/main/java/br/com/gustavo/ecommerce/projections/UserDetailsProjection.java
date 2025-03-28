package br.com.gustavo.ecommerce.projections;

public interface UserDetailsProjection {

    String getUsername();
    String getSenha();
    Long getRoleId();
    String getAuthority();
}
