package tech.sgcor.portfolio.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProjectDto {
    @NotNull(message = "user_id is required")
    private Long user_id;
    @NotBlank(message = "name is required and cannot be blank")
    private String name;
    @NotBlank(message = "type is required and cannot be blank")
    private String type;
    @NotBlank(message = "url is required and cannot be blank")
    private String url;
    @NotEmpty(message = "details is required and cannot be empty")
    private List<String> details;
}
