package qa.qdb.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import qa.qdb.entities.Comment;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findByDocumentId(Long documentId);
}
