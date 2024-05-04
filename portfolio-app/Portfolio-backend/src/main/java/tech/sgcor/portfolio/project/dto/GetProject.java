package tech.sgcor.portfolio.project.dto;

import tech.sgcor.portfolio.project.entity.Project;

import java.util.List;

public record GetProject(List<Project> projects) {
}
