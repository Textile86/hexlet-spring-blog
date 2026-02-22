package io.hexlet.spring.mapper;

import io.hexlet.spring.dto.TagCreateDTO;
import io.hexlet.spring.dto.TagDTO;
import io.hexlet.spring.dto.TagUpdateDTO;
import io.hexlet.spring.model.Tag;
import org.mapstruct.*;

@Mapper(
        uses = { JsonNullableMapper.class },
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public interface TagMapper {
    TagDTO toDTO(Tag tag);
    Tag toEntity(TagCreateDTO dto);
    void updateEntity(TagUpdateDTO dto, @MappingTarget Tag tag);
}
