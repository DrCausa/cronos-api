package com.cronos.api.modules.tag.controller;

import com.cronos.api.modules.tag.model.TagCreateRequest;
import com.cronos.api.modules.tag.model.TagResponse;
import com.cronos.api.modules.tag.service.TagService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TagController {

    private static final Logger log = LoggerFactory.getLogger(TagController.class);
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    public void create(Context ctx) {
        try {
            Integer workspaceId = Integer.parseInt(ctx.pathParam("workspaceId"));
            Integer userId = ctx.attribute("userId");
            TagCreateRequest request = ctx.bodyAsClass(TagCreateRequest.class);

            TagResponse response = tagService.createTag(workspaceId, userId, request);
            ctx.status(201).json(response);

        } catch (SecurityException e) {
            ctx.status(403).json(java.util.Map.of("error", "Forbidden", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(java.util.Map.of("error", "Bad Request", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al crear etiqueta", e);
            ctx.status(500).json(java.util.Map.of("error", "Internal Server Error", "message", "Error interno."));
        }
    }

    public void getAllByWorkspace(Context ctx) {
        try {
            Integer workspaceId = Integer.parseInt(ctx.pathParam("workspaceId"));
            Integer userId = ctx.attribute("userId");

            List<TagResponse> tags = tagService.getWorkspaceTags(workspaceId, userId);
            ctx.status(200).json(tags);

        } catch (SecurityException e) {
            ctx.status(403).json(java.util.Map.of("error", "Forbidden", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al listar etiquetas", e);
            ctx.status(500).json(java.util.Map.of("error", "Internal Server Error", "message", "Error interno."));
        }
    }
}
