package com.scar.bookvault.search.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search/v1/books")
public class SearchController {
    @GetMapping
    public List<Map<String,Object>> search(@RequestParam(defaultValue = "") String q) {
        return List.of(Map.of("query", q));
    }
}
