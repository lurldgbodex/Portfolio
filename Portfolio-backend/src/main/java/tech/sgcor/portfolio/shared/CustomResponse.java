package tech.sgcor.portfolio.shared;

import org.springframework.http.HttpStatus;

public record CustomResponse(int code, String message, HttpStatus status) {
}
