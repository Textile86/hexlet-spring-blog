package io.hexlet.spring.controller;

import io.hexlet.spring.dto.TagCreateDTO;
import io.hexlet.spring.dto.TagDTO;
import io.hexlet.spring.dto.TagUpdateDTO;
import io.hexlet.spring.service.TagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagsController {

    @Autowired
    private TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagDTO>> index() {
        List<TagDTO> tagDTOs = tagService.index();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(tagDTOs.size()))
                .body(tagDTOs);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagDTO create(@Valid @RequestBody TagCreateDTO dto) {
        return tagService.create(dto);
    }

    @GetMapping("/{id}")
    public TagDTO show(@PathVariable Long id) {
        return tagService.show(id);
    }

    @PutMapping("/{id}")
    public TagDTO update(@PathVariable Long id, @Valid @RequestBody TagUpdateDTO dto) {
        return tagService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        tagService.destroy(id);
    }
}
