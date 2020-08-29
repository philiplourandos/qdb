package qa.qdb.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import qa.qdb.entities.Document;

public interface DocumentRepository extends CrudRepository<Document, Long> {

    List<Document> findBySubmitter(Long submitter);

    Optional<Document> findBySubmitterAndUuid(Long submitter, String uuid);
}
