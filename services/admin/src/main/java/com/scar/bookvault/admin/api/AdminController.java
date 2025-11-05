package com.scar.bookvault.admin.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/v1")
public class AdminController {
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return Map.of("users", 0, "books", 0, "loans", 0);
    }
}
