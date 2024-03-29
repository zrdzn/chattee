package io.github.zrdzn.web.chattee.backend.discussion;

import io.github.zrdzn.web.chattee.backend.account.auth.AuthService;
import io.github.zrdzn.web.chattee.backend.discussion.post.PostDetails;
import io.github.zrdzn.web.chattee.backend.discussion.post.PostService;
import io.github.zrdzn.web.chattee.backend.web.HttpResponse;
import io.github.zrdzn.web.chattee.backend.web.security.RoutePrivilege;
import io.javalin.community.routing.annotations.Delete;
import io.javalin.community.routing.annotations.Endpoints;
import io.javalin.community.routing.annotations.Get;
import io.github.zrdzn.web.chattee.backend.discussion.post.Post;
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
import static io.github.zrdzn.web.chattee.backend.web.HttpResponse.ok;

@Endpoints
public class DiscussionEndpoints {

    public static final String ENDPOINT = "/api/v1/discussions";

    private final DiscussionService discussionService;
    private final AuthService authService;
    private final PostService postService;

    public DiscussionEndpoints(DiscussionService discussionService, AuthService authService, PostService postService) {
        this.discussionService = discussionService;
        this.authService = authService;
        this.postService = postService;
    }

    @OpenApi(
            path = ENDPOINT,
            methods = { HttpMethod.POST },
            summary = "Create a discussion",
            description = "Creates a discussion and returns it",
            tags = { "Discussion" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Authorization token",
                            example = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
                    )
            },
            requestBody = @OpenApiRequestBody(
                    content = @OpenApiContent(from = DiscussionCreateRequest.class)
            ),
            responses = {
                    @OpenApiResponse(
                            status = "201",
                            description = "Created discussion",
                            content = { @OpenApiContent(from = Discussion.class) }
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
    public void createDiscussion(Context context) {
        this.authService.authorizeFor(context, RoutePrivilege.DISCUSSION_OPEN)
                .peek(authDetails -> bodyAsClass(context, DiscussionCreateRequest.class, "Discussion body is empty or invalid.")
                        .filterNot(discussion -> StringUtils.isEmpty(discussion.getTitle()), ignored -> badRequest("'title' must not be null."))
                        .filter(discussion -> discussion.getTitle().length() > 3, ignored -> badRequest("'title' must be longer than 3 characters."))
                        .filter(discussion -> discussion.getTitle().length() < 101, ignored -> badRequest("'title' must be shorter than 101 characters."))
                        .filterNot(discussion -> StringUtils.isEmpty(discussion.getDescription()), ignored -> badRequest("'description' must not be null."))
                        .filter(discussion -> discussion.getDescription().length() > 10, ignored -> badRequest("'description' must be longer than 10 characters."))
                        .filter(discussion -> discussion.getDescription().length() < 201, ignored -> badRequest("Description must be shorter than 201 characters."))
                        .flatMap(discussion -> this.discussionService.createDiscussion(discussion, authDetails.getAccountId()))
                        .peek(discussion -> context.status(HttpStatus.CREATED).json(discussion))
                        .onError(error -> context.status(error.code()).json(error)))
                .onError(error -> context.status(error.code()).json(error));
    }

    @OpenApi(
            path = ENDPOINT,
            methods = { HttpMethod.GET },
            summary = "Get all discussions",
            description = "Returns all discussions",
            tags = { "Discussion" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Authorization token",
                            example = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
                    )
            },
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "Resulted discussions",
                            content = { @OpenApiContent(from = Discussion[].class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message caused by unauthorized access",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Get(ENDPOINT)
    public void getAllDiscussions(Context context) {
        this.authService.authorizeFor(context, RoutePrivilege.DISCUSSION_VIEW_ALL)
                .peek(authDetails -> this.discussionService.getAllDiscussions()
                        .peek(discussions -> context.status(HttpStatus.OK).json(discussions))
                        .onError(error -> context.status(error.code()).json(error)))
                .onError(error -> context.status(error.code()).json(error));
    }

    @OpenApi(
            path = ENDPOINT + "/{id}/posts",
            methods = { HttpMethod.GET },
            summary = "Get posts",
            description = "Returns posts",
            tags = { "Discussion" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Authorization token",
                            example = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
                    )
            },
            pathParams = {
                    @OpenApiParam(
                            name = "id",
                            description = "A numeric identifier associated with a record in the database",
                            required = true
                    )
            },
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            description = "Resulted posts",
                            content = { @OpenApiContent(from = Post[].class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message caused by unauthorized access",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Get(ENDPOINT + "/{id}/posts")
    public void findAllPostsByDiscussionId(Context context) {
        this.authService.authorizeFor(context, RoutePrivilege.DISCUSSION_VIEW)
                .peek(authDetails -> pathParamAsLong(context, "id", "Specified identifier is not a valid long number.")
                        .flatMap(this.postService::findPostsByDiscussionId)
                        .peek(posts -> context.status(HttpStatus.OK).json(posts))
                        .onError(error -> context.status(error.code()).json(error)))
                .onError(error -> context.status(error.code()).json(error));
    }

    @OpenApi(
            path = ENDPOINT + "/{id}",
            methods = { HttpMethod.GET },
            summary = "Get a discussion",
            description = "Returns a discussion",
            tags = { "Discussion" },
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
                            description = "Resulted discussion",
                            content = { @OpenApiContent(from = Discussion.class) }
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
    public void getDiscussion(Context context) {
        this.authService.authorizeFor(context, RoutePrivilege.DISCUSSION_VIEW)
                .peek(authDetails -> pathParamAsLong(context, "id", "Specified identifier is not a valid long number.")
                        .flatMap(this.discussionService::getDiscussion)
                        .peek(discussion -> context.status(HttpStatus.OK).json(discussion))
                        .onError(error -> context.status(error.code()).json(error)))
                .onError(error -> context.status(error.code()).json(error));
    }

    @OpenApi(
            path = ENDPOINT + "/{id}",
            methods = { HttpMethod.DELETE },
            summary = "Remove a discussion",
            description = "Removes a discussion",
            tags = { "Discussion" },
            headers = {
                    @OpenApiParam(
                            name = "Authorization",
                            description = "Bearer authorization token",
                            example = "Bearer <your-token>"
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
                            description = "Removed discussion",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    ),
                    @OpenApiResponse(
                            status = "401",
                            description = "Error message caused by unauthorized access",
                            content = { @OpenApiContent(from = HttpResponse.class) }
                    )
            })
    @Delete(ENDPOINT + "/{id}")
    public void removeDiscussion(Context context) {
        this.authService.authorizeFor(context, RoutePrivilege.DISCUSSION_DELETE)
                .peek(authDetails -> pathParamAsLong(context, "id", "Specified identifier is not a valid long number.")
                        .flatMap(this.discussionService::removeDiscussion)
                        .peek(ignored -> context.status(HttpStatus.OK).json(ok("Discussion has been removed.")))
                        .onError(error -> context.status(error.code()).json(error)))
                .onError(error -> context.status(error.code()).json(error));
    }

    @OpenApi(
            path = ENDPOINT + "/{id}/latest-post",
            methods = { HttpMethod.GET },
            summary = "Get latest post",
            description = "Returns latest post",
            tags = { "Discussion" },
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
    @Get(ENDPOINT + "/{id}/latest-post")
    public void getLatestPost(Context context) {
        this.authService.authorizeFor(context, RoutePrivilege.POST_VIEW)
                .peek(authDetails -> pathParamAsLong(context, "id", "Specified identifier is not a valid long number.")
                        .flatMap(this.discussionService::findLatestPostById)
                        .peek(post -> context.status(HttpStatus.OK).json(post))
                        .onError(error -> context.status(error.code()).json(error)))
                .onError(error -> context.status(error.code()).json(error));
    }

}
