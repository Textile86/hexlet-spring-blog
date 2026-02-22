package io.hexlet.spring.controller;

import io.hexlet.spring.dto.*;
import io.hexlet.spring.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public Page<PostDTO> index(PostParamsDTO params,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size
    ) {
        return postService.index(params, page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostDTO create(@Valid @RequestBody PostCreateDTO dto) {
        return postService.create(dto);
    }

    @GetMapping("/{id}")
    public PostDTO show(@PathVariable Long id) {
        return postService.show(id);
    }

    @PutMapping("/{id}")
    public PostDTO update(@PathVariable Long id, @Valid @RequestBody PostUpdateDTO dto) {
        return postService.update(id, dto);
    }

    @PatchMapping("/{id}")
    public PostDTO patch(@PathVariable Long id, @Valid @RequestBody PostPatchDTO dto) {
        return postService.patch(id,dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        postService.destroy(id);
    }
}
