package io.hexlet.spring.controller;


import io.hexlet.spring.dto.PostCreateDTO;
import io.hexlet.spring.dto.PostDTO;
import io.hexlet.spring.dto.PostParamsDTO;
import io.hexlet.spring.dto.PostUpdateDTO;
import io.hexlet.spring.dto.PostPatchDTO;
import io.hexlet.spring.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

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
        return postService.patch(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        postService.destroy(id);
    }
}
