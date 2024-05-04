package tech.sgcor.portfolio.project.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProjectUpdate {
    private String name;
    private String type;
    private String url;
    private String description;
    private String image_link;
    private List<String> details;
}
