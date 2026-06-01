package com.cronos.api.modules.time.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeEntryRequest {
    private String description;
    private Integer projectId;
    private Integer taskId;
    private List<Integer> tagIds;

    public TimeEntryRequest() {}

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getProjectId() { return projectId; }
    public void setProjectId(Integer projectId) { this.projectId = projectId; }
    
    public Integer getTaskId() { return taskId; }
    public void setTaskId(Integer taskId) { this.taskId = taskId; }
    
    public List<Integer> getTagIds() { return tagIds; }
    public void setTagIds(List<Integer> tagIds) { this.tagIds = tagIds; }
}
