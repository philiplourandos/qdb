package qa.qdb.cms;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import qa.qdb.client.model.NewPostRequest;
import qa.qdb.client.model.PostResponse;
import qa.qdb.client.placeholder.PlaceholderClient;
import qa.qdb.entities.Document;
import qa.qdb.repository.DocumentRepository;

@RestController
@RequestMapping("/cms")
public class CmsEndpoint {

    @Autowired
    private PlaceholderClient cmsClient;

    @Autowired
    private DocumentRepository docRepo;

    @PostMapping("/post/{documentId}")
    public ResponseEntity createPost(@PathVariable("documentId") Long documentId, @RequestBody NewPostRequest post) {
        final Optional<Document> savedDoc = docRepo.findById(documentId);
        
        if (savedDoc.isPresent()) {
            final PostResponse created = cmsClient.createPost(post);
            final Document requiredDoc = savedDoc.get();

            requiredDoc.setPostId(created.getId());

            docRepo.save(requiredDoc);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/post/{documentId}")
    public ResponseEntity getPost(@PathVariable("documentId") Long documentId) {
        final Optional<Document> found = docRepo.findById(documentId);

        if (found.isPresent()) {
            final Document doc = found.get();

            final PostResponse post = cmsClient.getPost(doc.getPostId());

            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
