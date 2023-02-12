package io.github.zrdzn.web.chattee.backend.plan;

import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import io.javalin.community.routing.annotations.Delete;
import io.javalin.community.routing.annotations.Endpoints;
import io.javalin.community.routing.annotations.Get;
import io.javalin.community.routing.annotations.Post;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;

import static io.github.zrdzn.web.chattee.backend.web.ContextExtensions.bodyAsClass;
import static io.github.zrdzn.web.chattee.backend.web.ContextExtensions.pathParamAsLong;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.badRequest;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.created;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.notFound;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.ok;

@Endpoints
public class DiscussionEndpoints {

    public static final String ENDPOINT = "/api/v1/discussions";

    private final DiscussionFacade discussionFacade;

    public DiscussionEndpoints(DiscussionFacade discussionFacade) {
        this.discussionFacade = discussionFacade;
    }

    @OpenApi(
            path = ENDPOINT,
            methods = { HttpMethod.POST },
            summary = "Create a discussion.",
            description = "Creates a discussion and returns it.",
            tags = { "Discussion" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Bearer authorization token.",
                            required = true,
                            example = "Bearer <your-token>"
                    )
            },
            requestBody = @OpenApiRequestBody(
                    content = @OpenApiContent(from = Discussion.class)
            ),
            responses = {
                    @OpenApiResponse(
                            status = "201",
                            description = "Created discussion.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "400",
                            description = "Error message when fields were considered incorrect during validation process.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message related to the unauthorized access in case of any failure.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Post(ENDPOINT)
    public void createDiscussion(Context context) {
        context.async(() ->
                bodyAsClass(context, Discussion.class, "Discussion body is empty or invalid.")
                        .filter(discussion -> discussion.getTitle() != null, ignored -> badRequest("'title' must not be null."))
                        .filter(discussion -> discussion.getTitle().length() > 3, ignored -> badRequest("'title' must be longer than 3 characters."))
                        .filter(discussion -> discussion.getTitle().length() < 31, ignored -> badRequest("'title' must be shorter than 101 characters."))
                        .filter(discussion -> discussion.getTitle() != null, ignored -> badRequest("'description' must not be null."))
                        .filter(discussion -> discussion.getTitle().length() > 10, ignored -> badRequest("'description' must be longer than 10 characters."))
                        .filter(discussion -> discussion.getTitle().length() < 2001, ignored -> badRequest("'description' must be shorter than 2001 characters."))
                        .flatMap(this.discussionFacade::createDiscussion)
                        .peek(shop -> context.status(HttpStatus.CREATED).json(created("Discussion has been created.")))
                        .onError(error -> context.status(error.code()).json(error)));
    }

    @OpenApi(
            path = ENDPOINT,
            methods = { HttpMethod.GET },
            summary = "Get all discussions.",
            description = "Returns all discussions.",
            tags = { "Discussion" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Bearer authorization token.",
                            required = true,
                            example = "Bearer <your-token>"
                    )
            },
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "Resulted discussions.",
                            content = { @OpenApiContent(from = Discussion[].class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message related to the unauthorized access in case of any failure.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Get(ENDPOINT)
    public void getAllDiscussions(Context context) {
        context.async(() ->
                this.discussionFacade.getAllDiscussions()
                        .peek(discussions -> context.status(HttpStatus.OK).json(discussions))
                        .onError(error -> context.status(error.code()).json(error)));
    }

    @OpenApi(
            path = ENDPOINT + "/{id}",
            methods = { HttpMethod.GET },
            summary = "Get a discussion.",
            description = "Returns a discussion.",
            tags = { "Discussion" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Bearer authorization token.",
                            required = true,
                            example = "Bearer <your-token>"
                    )
            },
            pathParams = @OpenApiParam(name = "id", description = "A numeric identifier associated with a record in the database.", required = true),
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "Resulted discussion.",
                            content = { @OpenApiContent(from = Discussion.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message related to the unauthorized access in case of any failure.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "404",
                            description = "Error message related to empty result based on your request.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Get(ENDPOINT + "/{id}")
    public void getDiscussion(Context context) {
        context.async(() ->
                pathParamAsLong(context, "id", "Specified identifier is not a valid long number.")
                        .flatMap(this.discussionFacade::getDiscussion)
                        .peek(discussionMaybe -> discussionMaybe.ifPresentOrElse(discussion -> context.status(HttpStatus.OK).json(discussion),
                                () -> context.status(HttpStatus.NOT_FOUND).json(notFound("Could not find this discussion."))))
                        .onError(error -> context.status(error.code()).json(error)));
    }

    @OpenApi(
            path = ENDPOINT + "/{id}",
            methods = { HttpMethod.DELETE },
            summary = "Remove a discussion.",
            description = "Removes a discussion.",
            tags = { "Discussion" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Bearer authorization token.",
                            required = true,
                            example = "Bearer <your-token>"
                    )
            },
            pathParams = @OpenApiParam(name = "id", description = "A numeric identifier associated with a record in the database.", required = true),
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "Removed discussion.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message related to the unauthorized access in case of any failure.",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Delete(ENDPOINT + "/{id}")
    public void removeDiscussion(Context context) {
        context.async(() ->
                pathParamAsLong(context, "id", "Specified identifier is not a valid long number.")
                        .flatMap(this.discussionFacade::removeDiscussion)
                        .peek(ignored -> context.status(HttpStatus.OK).json(ok("Discussion has been removed.")))
                        .onError(error -> context.status(error.code()).json(error)));
    }

}
