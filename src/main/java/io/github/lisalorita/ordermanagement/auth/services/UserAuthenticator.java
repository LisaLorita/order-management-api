package io.github.lisalorita.ordermanagement.auth.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.lisalorita.ordermanagement.auth.dtos.LoginRequest;
import io.github.lisalorita.ordermanagement.auth.dtos.LoginResponse;
import io.github.lisalorita.ordermanagement.auth.exceptions.InvalidCredentialsException;
import io.github.lisalorita.ordermanagement.auth.entities.RefreshToken;
import io.github.lisalorita.ordermanagement.auth.infrastructure.JwtTokenProvider;
import io.github.lisalorita.ordermanagement.users.entities.User;
import io.github.lisalorita.ordermanagement.users.repositories.UserRepository;


@Service
public class UserAuthenticator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public UserAuthenticator(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
    }


    public LoginResponse run(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());
        String refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return new LoginResponse(token, refreshToken);

    }
}
