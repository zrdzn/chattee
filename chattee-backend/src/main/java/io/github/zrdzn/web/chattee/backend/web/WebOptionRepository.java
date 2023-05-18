package io.github.zrdzn.web.chattee.backend.web;

import panda.std.Result;

public interface WebOptionRepository {

    Result<WebOption, WebOptionError> createOption(WebOptionCreateRequest optionCreateRequest);

    Result<WebOption, WebOptionError> findOptionByName(String name);

}
