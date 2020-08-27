package qa.qdb.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import qa.qdb.entities.Document;

public interface DocumentRepository extends CrudRepository<Document, Long> {

    public List<Document> findBySubmitter(String submitter);
}
