package com.banking.service;

import com.banking.model.SignupRequest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class AuthService {
    private final JdbcTemplate jdbcTemplate;
    private final Random random = new Random();

    public AuthService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void signup(SignupRequest request) {
        Integer userId = jdbcTemplate.queryForObject(
                """
                INSERT INTO login_data (name, email, password)
                VALUES (?, ?, crypt(?, gen_salt('bf')))
                RETURNING id
                """,
                Integer.class,
                request.getName(),
                request.getEmail(),
                request.getPassword()
        );

        String iban = generateIban();

        jdbcTemplate.update(
                """
                INSERT INTO account_data (user_id, iban)
                VALUES (?, ?)
                """,
                userId,
                iban
        );
    }

    public boolean emailExists(String email) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM login_data WHERE email = ?",
                Integer.class,
                email
        );

        return count != null && count > 0;
    }
    public boolean passwordMatches(String email, String password) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM login_data
                WHERE email = ?
                AND password = crypt(?, password)
                """,
                Integer.class,
                email,
                password
        );

        return count != null && count > 0;
    }

    private String generateIban() {
        return "RO" + String.format("%032d", Math.abs(random.nextLong()));
    }
}
