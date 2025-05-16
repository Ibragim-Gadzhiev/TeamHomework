package ru.astondevs.dto;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;
import java.util.stream.Collectors;

@Relation(collectionRelation = "users", itemRelation = "user")
public record UserResponseWrapper(
        UserResponseDto content,
        List<Link> links
) {
    public static UserResponseWrapper wrap(UserResponseDto dto) {
        return new UserResponseWrapper(
                dto,
                List.of(
                        Link.of("/api/users/" + dto.id()).withSelfRel(),
                        Link.of("/api/users").withRel("users"),
                        Link.of("/api/users/" + dto.id()).withRel("update"),
                        Link.of("/api/users/" + dto.id()).withRel("delete")
                )
        );
    }

    public static List<UserResponseWrapper> wrapAll(List<UserResponseDto> dtos) {
        return dtos.stream()
                .map(UserResponseWrapper::wrap)
                .collect(Collectors.toList());
    }
}
