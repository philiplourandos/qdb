package qa.qdb.client.placeholder;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import qa.qdb.client.model.Comment;
import qa.qdb.client.model.NewPostRequest;
import qa.qdb.client.model.PostResponse;

@FeignClient(value = "placeholder", url = "https://jsonplaceholder.typicode.com")
public interface PlaceholderClient {

    @PostMapping(path = "/posts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    PostResponse createPost(@RequestBody NewPostRequest request);

    @GetMapping(path = "/posts/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    PostResponse getPost(@PathVariable("id") long id);

    @PostMapping(path = "/comments", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Comment createComment(@RequestBody Comment comment);
}
