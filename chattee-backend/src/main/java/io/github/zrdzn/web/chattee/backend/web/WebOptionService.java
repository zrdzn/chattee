package io.github.zrdzn.web.chattee.backend.web;

import panda.std.Result;

import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.conflict;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.internalServerError;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.notFound;

public class WebOptionService {

    private final WebOptionRepository optionRepository;

    public WebOptionService(WebOptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    public Result<WebOption, HttpResponse> createOption(WebOptionCreateRequest optionCreateRequest) {
        return this.optionRepository.createOption(optionCreateRequest)
                .map(option -> new WebOption(option.getId(), option.getName(), option.getValue()))
                .mapErr(error -> switch (error) {
                    case ALREADY_EXISTS -> conflict(WebOptionError.ALREADY_EXISTS.getMessage());
                    case NOT_EXISTS -> throw new IllegalStateException("Not exists error is not allowed here.");
                    case SQL_EXCEPTION -> internalServerError(WebOptionError.SQL_EXCEPTION.getMessage());
                });
    }

    public Result<WebOption, HttpResponse> findOptionByName(String name) {
        return this.optionRepository.findOptionByName(name)
                .map(option -> new WebOption(option.getId(), option.getName(), option.getValue()))
                .mapErr(error -> switch (error) {
                    case ALREADY_EXISTS -> throw new IllegalStateException("Already exists error is not allowed here.");
                    case NOT_EXISTS -> notFound(WebOptionError.NOT_EXISTS.getMessage());
                    case SQL_EXCEPTION -> internalServerError(WebOptionError.SQL_EXCEPTION.getMessage());
                });
    }

}
