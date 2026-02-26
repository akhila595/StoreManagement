package com.shopmanagement.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopmanagement.model.PasswordResetToken;
import com.shopmanagement.model.User;
import com.shopmanagement.repository.PasswordResetTokenRepository;
import com.shopmanagement.repository.UserRepository;

@Service
@Transactional
public class PasswordResetService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public PasswordResetService(JavaMailSender mailSender,
                                UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                PasswordResetTokenRepository tokenRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
    }

    // ================= SEND RESET LINK =================
    public void sendPasswordResetLink(String email) {

        User user = userRepository.findByEmailAndStatus(email, "ACTIVE")
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        // Delete old tokens
        tokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(
                LocalDateTime.now().plusMinutes(15)
        );

        tokenRepository.save(resetToken);

        String resetLink = frontendUrl + "/reset-password?token=" + token;

        sendEmail(user.getEmail(), resetLink);
    }

    // ================= RESET PASSWORD =================
    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid reset token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token expired");
        }

        if (newPassword == null || newPassword.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
        tokenRepository.delete(resetToken);
    }

    private void sendEmail(String email, String resetLink) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText(
                "Click the link below to reset your password:\n\n"
                        + resetLink
                        + "\n\nThis link expires in 15 minutes."
        );

        mailSender.send(message);
    }
}