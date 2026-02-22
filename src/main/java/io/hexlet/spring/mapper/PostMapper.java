package io.hexlet.spring.mapper;

import io.hexlet.spring.dto.PostCreateDTO;
import io.hexlet.spring.dto.PostDTO;
import io.hexlet.spring.dto.PostPatchDTO;
import io.hexlet.spring.dto.PostUpdateDTO;
import io.hexlet.spring.model.Post;
import org.mapstruct.*;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class, TagMapper.class },
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public interface PostMapper {

    @Mapping(target = "userId", source = "user.id")
    PostDTO toDTO(Post post);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "tags", ignore = true)
    Post toEntity(PostCreateDTO dto);

    @Mapping(target = "tags", ignore = true)
    void updateEntity(PostUpdateDTO dto, @MappingTarget Post post);

    @Mapping(target = "tags", ignore = true)
    void updateEntityFromPatch(PostPatchDTO dto, @MappingTarget Post post);
}
