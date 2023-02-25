package io.github.zrdzn.web.chattee.backend.web;

import java.util.Optional;
import io.javalin.http.Context;
import io.javalin.http.Header;
import panda.std.Result;

public class ContextExtensions {

    public static <T> Result<T, Exception> bodyAsClass(Context context, Class<T> bodyClass) {
        return Result.supplyThrowing(() -> context.bodyAsClass(bodyClass));
    }

    public static <T> Result<T, HttpResponse> bodyAsClass(Context context, Class<T> bodyClass, String error) {
        return bodyAsClass(context, bodyClass).mapErr(ignored -> HttpResponse.badRequest(error));
    }

    public static <T> Result<T, Exception> pathParamAsClass(Context context, Class<T> type, String id) {
        return Result.supplyThrowing(() -> context.pathParamAsClass(id, type).get());
    }

    public static <T> Result<T, HttpResponse> pathParamAsClass(Context context, Class<T> type, String id, String error) {
        return pathParamAsClass(context, type, id).mapErr(ignored -> HttpResponse.badRequest(error));
    }

    public static Result<Long, Exception> pathParamAsLong(Context context, String id) {
        return pathParamAsClass(context, Long.class, id);
    }

    public static Result<Long, HttpResponse> pathParamAsLong(Context context, String id, String error) {
        return pathParamAsLong(context, id).mapErr(ignored -> HttpResponse.badRequest(error));
    }

    public static Result<String, Exception> pathParamAsString(Context context, String id) {
        return pathParamAsClass(context, String.class, id);
    }

    public static Result<String, HttpResponse> pathParamAsString(Context context, String id, String error) {
        return pathParamAsString(context, id).mapErr(ignored -> HttpResponse.badRequest(error));
    }

    public static Optional<String> extractTokenFromContext(Context context) {
        String token = context.sessionAttribute("sessionid");
        if (token == null) {
            token = context.header(Header.AUTHORIZATION);
        }
        return Optional.ofNullable(token);
    }

}
