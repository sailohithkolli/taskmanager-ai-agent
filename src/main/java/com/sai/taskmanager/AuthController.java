package com.sai.taskmanager;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/register")
    public String Register(@RequestBody User user)
    {
        return authService.register(user);
    }
    @PostMapping("/login")
    public String Login(@RequestBody User user)
    {
        return authService.login(user.getUsername(),user.getPassword());
    }
}
