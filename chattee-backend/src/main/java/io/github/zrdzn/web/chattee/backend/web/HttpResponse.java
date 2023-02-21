package io.github.zrdzn.web.chattee.backend.web;

import io.javalin.http.HttpStatus;

public record HttpResponse(
        HttpStatus code,
        String message
) {

    public static HttpResponse ok(String message) {
        return new HttpResponse(HttpStatus.OK, message);
    }

    public static HttpResponse created(String message) {
        return new HttpResponse(HttpStatus.CREATED, message);
    }

    public static HttpResponse accepted(String message) {
        return new HttpResponse(HttpStatus.ACCEPTED, message);
    }

    public static HttpResponse unauthorized(String message) {
        return new HttpResponse(HttpStatus.UNAUTHORIZED, message);
    }

    public static HttpResponse forbidden(String message) {
        return new HttpResponse(HttpStatus.FORBIDDEN, message);
    }

    public static HttpResponse badRequest(String message) {
        return new HttpResponse(HttpStatus.BAD_REQUEST, message);
    }

    public static HttpResponse notFound(String message) {
        return new HttpResponse(HttpStatus.NOT_FOUND, message);
    }

    public static HttpResponse conflict(String message) {
        return new HttpResponse(HttpStatus.CONFLICT, message);
    }

    public static HttpResponse internalServerError(String message) {
        return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

}
