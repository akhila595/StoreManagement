package com.shopmanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopmanagement.model.PasswordResetToken;
import com.shopmanagement.model.User;

public interface PasswordResetTokenRepository
extends JpaRepository<PasswordResetToken, Long> {

Optional<PasswordResetToken> findByToken(String token);

void deleteByUser(User user);



}