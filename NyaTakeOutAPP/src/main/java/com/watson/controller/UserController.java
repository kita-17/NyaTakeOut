package com.watson.controller;

import com.watson.common.Result;
import com.watson.dto.LoginDTO;
import com.watson.dto.UserDTO;
import com.watson.services.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@RestController
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO dto) {
        return userService.login(dto);
    }

    @GetMapping("/code")
    public Result createCode(String phone) {
        return userService.createCode(phone);
    }

    @PostMapping("/logout")
    public Result logout(UserDTO dto) {
        return userService.logout(dto);
    }
}
