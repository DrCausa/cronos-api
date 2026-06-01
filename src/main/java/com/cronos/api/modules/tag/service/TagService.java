package com.cronos.api.modules.tag.service;

import com.cronos.api.modules.tag.model.Tag;
import com.cronos.api.modules.tag.model.TagCreateRequest;
import com.cronos.api.modules.tag.model.TagResponse;
import com.cronos.api.modules.tag.repository.TagRepository;
import com.cronos.api.modules.workspace.model.WorkspaceRole;
import com.cronos.api.modules.workspace.repository.WorkspaceRepository;

import java.util.List;
import java.util.stream.Collectors;

public class TagService {

    private final TagRepository tagRepository;
    private final WorkspaceRepository workspaceRepository;

    public TagService(TagRepository tagRepository, WorkspaceRepository workspaceRepository) {
        this.tagRepository = tagRepository;
        this.workspaceRepository = workspaceRepository;
    }

    public TagResponse createTag(Integer workspaceId, Integer userId, TagCreateRequest request) {
        WorkspaceRole role = workspaceRepository.getMemberRole(workspaceId, userId);
        if (role == null) {
            throw new SecurityException("No perteneces a este espacio de trabajo.");
        }
        if (role == WorkspaceRole.MEMBER) {
            throw new SecurityException("No tienes permisos suficientes para crear etiquetas.");
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la etiqueta es obligatorio.");
        }

        Tag tag = new Tag();
        tag.setWorkspaceId(workspaceId);
        tag.setName(request.getName().trim().toUpperCase()); // Normalizamos para evitar duplicados visuales
        tag.setColorHex(request.getColorHex() != null ? request.getColorHex() : "#9C27B0");

        Tag savedTag = tagRepository.createTag(tag);
        return new TagResponse(savedTag);
    }

    public List<TagResponse> getWorkspaceTags(Integer workspaceId, Integer userId) {
        WorkspaceRole role = workspaceRepository.getMemberRole(workspaceId, userId);
        if (role == null) {
            throw new SecurityException("Acceso denegado. No eres miembro de este espacio.");
        }

        return tagRepository.findByWorkspaceId(workspaceId)
                .stream()
                .map(TagResponse::new)
                .collect(Collectors.toList());
    }
}
