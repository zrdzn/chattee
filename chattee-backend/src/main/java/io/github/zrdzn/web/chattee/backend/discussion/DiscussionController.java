package io.github.zrdzn.web.chattee.backend.discussion;

import io.github.zrdzn.web.chattee.backend.user.User;
import io.github.zrdzn.web.chattee.backend.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/discussion")
public class DiscussionController {

    private final DiscussionService discussionService;
    private final UserService userService;

    @Autowired
    public DiscussionController(DiscussionService discussionService, UserService userService) {
        this.discussionService = discussionService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Discussion> postDiscussion(@RequestBody @Valid DiscussionWriteDto discussionDto) {
        Optional<User> authorMaybe = this.userService.getUser(discussionDto.getAuthorId());
        if (authorMaybe.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Discussion discussion = new Discussion();
        discussion.setAuthor(authorMaybe.get());
        discussion.setTitle(discussionDto.getTitle());
        discussion.setDescription(discussionDto.getDescription());

        return ResponseEntity.ok(this.discussionService.createDiscussion(discussion));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Discussion> getDiscussion(@PathVariable long id) {
        return ResponseEntity.of(this.discussionService.getDiscussion(id));
    }

    @GetMapping
    public ResponseEntity<List<Discussion>> getDiscussions() {
        return ResponseEntity.ok(this.discussionService.getAllDiscussions());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscussion(@PathVariable long id) {
        this.discussionService.removeDiscussion(id);
        return ResponseEntity.noContent().build();
    }

}
