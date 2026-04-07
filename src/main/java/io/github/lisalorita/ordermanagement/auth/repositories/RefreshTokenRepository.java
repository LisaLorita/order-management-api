package io.github.lisalorita.ordermanagement.auth.repositories;

import io.github.lisalorita.ordermanagement.auth.entities.RefreshToken;
import io.github.lisalorita.ordermanagement.users.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByIdentifier(String identifier);
    Optional<RefreshToken> findByUser(User user);
    int deleteByUser(User user);
}

