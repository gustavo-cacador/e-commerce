package br.com.gustavo.ecommerce.repositories;

import br.com.gustavo.ecommerce.entities.Usuario;
import br.com.gustavo.ecommerce.projections.UsuarioDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query(nativeQuery = true, value = """
			SELECT tb_usuario.email AS username, tb_usuario.senha, tb_role.id AS roleId, tb_role.authority
			FROM tb_usuario
			INNER JOIN tb_user_role ON tb_usuario.id = tb_user_role.user_id
			INNER JOIN tb_role ON tb_role.id = tb_user_role.role_id
			WHERE tb_usuario.email = :email
		""")
    List<UsuarioDetailsProjection> searchUserAndRolesByEmail(String email);

	// utilizamos optional aqui pois caso não exista nome de usuario ele retorna optional vazio
	// caso nome de usuario exista ele retorna um usuario dentro do optional
	Optional<Usuario> findByEmail(String email);
}
