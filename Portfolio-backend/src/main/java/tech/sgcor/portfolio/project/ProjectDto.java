package tech.sgcor.portfolio.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import tech.sgcor.portfolio.validation.IsValidList;

import java.util.List;

@Data
@Builder
public class ProjectDto {
    @NotNull(message = "make una provide user_id")
    private Long user_id;
    @NotBlank(message = "make una provide name")
    private String name;
    @NotBlank(message = "make una provide type")
    private String type;
    @NotBlank(message = "make una provide url")
    private String url;

    @IsValidList(message = "na list details dey expect and e no fit empty")
    private List<String> details;
}
