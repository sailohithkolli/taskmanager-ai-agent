package com.sai.taskmanager;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

   private final UserRepository userRepository;
   private final JwtService jwtService;
   private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public String register(User user)
    {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return jwtService.generateToken(user);
    }
    public String login(String username, String password)
    {
       User user = userRepository.findByUsername(username).orElseThrow(()-> new RuntimeException("User Not Found"));
       if(passwordEncoder.matches(password,user.getPassword())){
           return jwtService.generateToken(user);
       }
       else{
           throw new RuntimeException("Invalid password");
       }
    }
}
