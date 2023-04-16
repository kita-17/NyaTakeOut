package com.watson.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/image")
public class ImageController {
    @Value("${bocchi.path}")
    private String basePath;

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        FileInputStream fs = new FileInputStream(basePath + name);
        ServletOutputStream writer = response.getOutputStream();
        response.setContentType("image/jpeg");
        int len;
        byte[] bytes = new byte[1024];
        while ((len = fs.read(bytes)) != -1) {
            writer.write(bytes, 0, len);
            writer.flush();
        }
        writer.close();
        fs.close();
    }
}
