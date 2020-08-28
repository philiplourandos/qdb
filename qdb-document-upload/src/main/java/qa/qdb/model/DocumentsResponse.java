package qa.qdb.model;

import java.io.Serializable;
import java.util.List;
import qa.qdb.entities.Document;

public class DocumentsResponse implements Serializable {
    private List<Document> documents;

    public DocumentsResponse() {
    }

    public DocumentsResponse(List<Document> documents) {
        this.documents = documents;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}
