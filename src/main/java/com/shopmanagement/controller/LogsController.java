package com.shopmanagement.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin
public class LogsController {

    // If you have logs stored in DB, inject the logsService here
    // private final LogsService logsService;

    // public LogsController(LogsService logsService) {
    //     this.logsService = logsService;
    // }

    @GetMapping
    public List<Map<String, Object>> getLogs() {

        // TODO: Replace with logsService.getAll() if DB exists

        List<Map<String, Object>> logs = new ArrayList<>();

        // Mock 5 logs
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> log = new HashMap<>();
            log.put("timestamp", new Date().toString());
            log.put("level", i % 2 == 0 ? "INFO" : "ERROR");
            log.put("message", "Sample log message #" + i);
            log.put("module", "System");
            logs.add(log);
        }

        return logs;
    }
}
