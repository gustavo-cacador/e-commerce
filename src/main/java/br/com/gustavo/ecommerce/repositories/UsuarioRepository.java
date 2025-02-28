package br.com.gustavo.ecommerce.repositories;

import br.com.gustavo.ecommerce.entities.Usuario;
import br.com.gustavo.ecommerce.projections.UsuarioDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    //Usuario findByEmail(String email);

    @Query(nativeQuery = true, value = """
			SELECT tb_usuario.email AS username, tb_usuario.senha, tb_role.id AS roleId, tb_role.authority
			FROM tb_usuario
			INNER JOIN tb_user_role ON tb_usuario.id = tb_user_role.user_id
			INNER JOIN tb_role ON tb_role.id = tb_user_role.role_id
			WHERE tb_usuario.email = :email
		""")
    List<UsuarioDetailsProjection> searchUserAndRolesByEmail(String email);
}
