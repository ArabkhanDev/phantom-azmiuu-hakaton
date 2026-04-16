package az.microservices.azmiuuhakaton.repository;


import aj.org.objectweb.asm.commons.Remapper;
import az.microservices.azmiuuhakaton.enums.UserRole;
import az.microservices.azmiuuhakaton.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<User> findByRole(UserRole role);
}
