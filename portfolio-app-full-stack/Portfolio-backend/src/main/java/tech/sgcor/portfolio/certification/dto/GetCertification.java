package tech.sgcor.portfolio.certification.dto;

import tech.sgcor.portfolio.certification.entity.Certification;

import java.util.List;

public record GetCertification(List<Certification> certification) {
}
