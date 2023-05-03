package io.github.zrdzn.web.chattee.backend.discussion.post;

import io.github.zrdzn.web.chattee.backend.account.auth.AuthService;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import io.github.zrdzn.web.chattee.backend.web.security.RoutePrivilege;
import io.javalin.community.routing.annotations.Delete;
import io.javalin.community.routing.annotations.Endpoints;
import io.javalin.community.routing.annotations.Get;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;
import panda.utilities.StringUtils;

import static io.github.zrdzn.web.chattee.backend.web.ContextExtensions.bodyAsClass;
import static io.github.zrdzn.web.chattee.backend.web.ContextExtensions.pathParamAsLong;
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.badRequest;

@Endpoints
public class PostEndpoints {

    public static final String ENDPOINT = "/api/v1/posts";

    private final PostService postService;
    private final AuthService authService;

    public PostEndpoints(PostService postService, AuthService authService) {
        this.postService = postService;
        this.authService = authService;
    }

    @OpenApi(
            path = ENDPOINT,
            methods = { HttpMethod.POST },
            summary = "Create post",
            description = "Creates post and returns it",
            tags = { "Post" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Authorization token",
                            example = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
                    )
            },
            requestBody = @OpenApiRequestBody(
                    content = @OpenApiContent(from = PostCreateRequest.class)
            ),
            responses = {
                    @OpenApiResponse(
                            status = "201",
                            description = "Created post",
                            content = { @OpenApiContent(from = Post.class) }
                    ),
                    @OpenApiResponse(
                            status = "400",
                            description = "Error message when fields were considered incorrect during validation process",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message caused by unauthorized access",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @io.javalin.community.routing.annotations.Post(ENDPOINT)
    public void createPost(Context context) {
        this.authService.authorizeFor(context, RoutePrivilege.POST_CREATE)
                .peek(authDetails -> bodyAsClass(context, PostCreateRequest.class, "Post body is empty or invalid.")
                        .filterNot(post -> StringUtils.isEmpty(post.getContent()), ignored -> badRequest("Content must not be empty."))
                        .filter(post -> post.getContent().length() > 10, ignored -> badRequest("Content must be longer than 10 characters."))
                        .filter(post -> post.getContent().length() < 2049, ignored -> badRequest("Content must be shorter than 2049 characters."))
                        .flatMap(post -> this.postService.createPost(post, authDetails.getAccountId(), post.getDiscussionId()))
                        .peek(post -> context.status(HttpStatus.CREATED).json(post))
                        .onError(error -> context.status(error.code()).json(error)))
                .onError(error -> context.status(error.code()).json(error));
    }

    @OpenApi(
            path = ENDPOINT + "/{id}",
            methods = { HttpMethod.GET },
            summary = "Get post",
            description = "Returns post",
            tags = { "Post" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Authorization token",
                            example = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
                    )
            },
            pathParams = @OpenApiParam(
                    name = "id",
                    description = "A numeric identifier associated with a record in the database",
                    required = true
            ),
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "Resulted post",
                            content = { @OpenApiContent(from = PostDetails.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message caused by unauthorized access",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "404",
                            description = "Error message related to empty result based on your request",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Get(ENDPOINT + "/{id}")
    public void getPost(Context context) {
        this.authService.authorizeFor(context, RoutePrivilege.POST_VIEW)
                .peek(authDetails -> pathParamAsLong(context, "id", "Specified identifier is not a valid long number.")
                        .flatMap(this.postService::getPost)
                        .peek(post -> context.status(HttpStatus.OK).json(post))
                        .onError(error -> context.status(error.code()).json(error)))
                .onError(error -> context.status(error.code()).json(error));
    }

    @OpenApi(
            path = ENDPOINT + "/{id}",
            methods = { HttpMethod.DELETE },
            summary = "Remove post",
            description = "Removes post",
            tags = { "Post" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Authorization token",
                            example = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
                    )
            },
            pathParams = @OpenApiParam(
                    name = "id",
                    description = "A numeric identifier associated with a record in the database",
                    required = true
            ),
            responses = {
                    @OpenApiResponse(
                            status = "204",
                            description = "Removed post",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message caused by unauthorized access",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Delete(ENDPOINT + "/{id}")
    public void removePost(Context context) {
        this.authService.authorizeFor(context, RoutePrivilege.POST_REMOVE)
                .peek(authDetails -> pathParamAsLong(context, "id", "Specified identifier is not a valid long number.")
                        .flatMap(this.postService::removePost)
                        .peek(ignored -> context.status(HttpStatus.NO_CONTENT))
                        .onError(error -> context.status(error.code()).json(error)))
                .onError(error -> context.status(error.code()).json(error));
    }

}
