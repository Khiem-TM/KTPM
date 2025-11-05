package com.scar.bookvault.notification.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notify/v1")
public class NotificationController {
    @PostMapping("/email")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, String> sendEmail(@RequestBody Map<String, String> payload) {
        return Map.of("status", "queued");
    }
}
