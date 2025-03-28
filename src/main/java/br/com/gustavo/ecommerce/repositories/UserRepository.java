package br.com.gustavo.ecommerce.repositories;

import br.com.gustavo.ecommerce.entities.User;
import br.com.gustavo.ecommerce.projections.UserDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(nativeQuery = true, value = """
			SELECT tb_usuario.email AS username, tb_usuario.senha, tb_role.id AS roleId, tb_role.authority
			FROM tb_usuario
			INNER JOIN tb_user_role ON tb_usuario.id = tb_user_role.user_id
			INNER JOIN tb_role ON tb_role.id = tb_user_role.role_id
			WHERE tb_usuario.email = :email
		""")
    List<UserDetailsProjection> searchUserAndRolesByEmail(String email);

	// utilizamos optional aqui pois caso não exista nome de usuario ele retorna optional vazio
	// caso nome de usuario exista ele retorna um usuario dentro do optional
	Optional<User> findByEmail(String email);
}
