package tech.sgcor.review.dto;

import java.util.Map;

public record CustomError(int code, Map<String, String> errors) {
}
