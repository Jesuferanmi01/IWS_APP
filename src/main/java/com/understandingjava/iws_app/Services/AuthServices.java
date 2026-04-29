package com.understandingjava.iws_app.Services;

import com.understandingjava.iws_app.Execeptions.CustomException;
import com.understandingjava.iws_app.DTOs.LoginRequestDTO;
import com.understandingjava.iws_app.DTOs.LoginResponseDTO;
import com.understandingjava.iws_app.Models.Users;
import com.understandingjava.iws_app.Repos.IAuthRepo;
import org.springframework.stereotype.Service;

@Service
public class AuthServices {

    private final  AuthHelpersServices helpers;
    private  final IAuthRepo userRepository;

    public AuthServices(AuthHelpersServices helpers, IAuthRepo userRepository) {
        this.helpers = helpers;
        this.userRepository = userRepository;
    }

    public LoginResponseDTO loginUser(LoginRequestDTO request) {
        //log.info("[AUTH SERVICE] Login attempt — username={}", request.getUsername());

        String userInput = request.getUsername().trim();
        boolean isEmail  = userInput.endsWith(".com");

        //log.debug("[AUTH SERVICE] Login type resolved as {}", isEmail ? "EMAIL" : "USER_CODE");

        Users user;
        if (isEmail) {
            user = userRepository.findByEmail(userInput);
            if (user == null) {
                throw new CustomException.NotFoundException(
                        "No account found with email '" + userInput + "'.");
            }
        } else {
            user = userRepository.findByUserCode(userInput);
            if (user == null) {
                throw new CustomException.NotFoundException(
                        "No account found with user code '" + userInput + "'.");
            }
        }

        if (!helpers.passwordMatch(request.getPassword(), user.getPassword())) {
            throw new CustomException.ValidationException(
                    "Invalid credentials. Please check your password.");
        }

        String token = helpers.generateToken(user);
        //log.info("[AUTH SERVICE] Login successful — userCode={}", user.getUserCode());

        return LoginResponseDTO.builder()
                .token(token)
                .message("Login successful.")
                .name(user.getFirstName() + " " + user.getLastName())
                .role(user.getUserType().name())
                .userCode(user.getUserCode())
                .email(user.getEmail())
                .build();
    }
}
