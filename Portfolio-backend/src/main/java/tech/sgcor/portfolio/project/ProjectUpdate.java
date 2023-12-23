package tech.sgcor.portfolio.project;

import lombok.Data;

import java.util.List;

@Data
public class ProjectUpdate {
    private String name;
    private String type;
    private String url;
    private List<String> details;
}
