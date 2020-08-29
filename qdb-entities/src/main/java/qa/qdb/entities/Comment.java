package qa.qdb.entities;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("COMMENTS")
public class Comment {
    @Id
    private Long id; 
    @Column("document_id")
    private Long documentId;

    private String comment;
    
    private LocalDateTime created;

    public Comment() {
    }

    public Comment(Long id, Long documentId, String comment, LocalDateTime created) {
        this.id = id;
        this.documentId = documentId;
        this.comment = comment;
        this.created = created;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }
}
