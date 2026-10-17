package com.middleware.tvmaze.controller;

import com.middleware.tvmaze.model.ShowSearchDTO;
import com.middleware.tvmaze.service.TvMazeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TvMazeController {

    private final TvMazeService tvMazeService;

    // Inyección por constructor estricta (Evita alertas en VS Code)
    public TvMazeController(TvMazeService tvMazeService) {
        this.tvMazeService = tvMazeService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<ShowSearchDTO>> search(@RequestParam("search_query") String searchQuery) {
        return ResponseEntity.ok(tvMazeService.searchShows(searchQuery));
    }

    @GetMapping("/shows/{show_id}")
    public ResponseEntity<Map<String, Object>> getShow(@PathVariable("show_id") Long showId) {
        Map<String, Object> show = tvMazeService.getShowById(showId);
        if (show == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(show);
    }

    @PostMapping("/comments")
    public ResponseEntity<Map<String, String>> addComment(@RequestBody Map<String, Object> payload) {
        Long showId = Long.valueOf(payload.get("show_id").toString());
        String comment = (String) payload.get("comment");
        Integer rating = (Integer) payload.get("rating");

        return ResponseEntity.ok(tvMazeService.saveComment(showId, comment, rating));
    }
}
