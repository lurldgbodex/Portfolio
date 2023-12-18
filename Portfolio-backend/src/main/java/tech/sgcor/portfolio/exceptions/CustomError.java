package tech.sgcor.portfolio.exceptions;

import org.springframework.http.HttpStatus;

public record CustomError(int code, String error, HttpStatus status) {
}
