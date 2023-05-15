package com.rklymus.ittask;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService service;

    @GetMapping("/{id}")
    public Post get(@PathVariable Integer id) {
        return service.get(id);
    }

    @PostMapping
    public Post create(@RequestBody @Valid PostRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public Post update(@PathVariable Integer id, @RequestBody @Valid PostRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
