package com.cronos.api.modules.task.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskCreateRequest {
    
    private String name;

    public TaskCreateRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
