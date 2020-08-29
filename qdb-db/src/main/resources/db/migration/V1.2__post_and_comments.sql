ALTER TABLE DOCUMENT ADD COLUMN post_id BIGINT;

CREATE TABLE COMMENTS(id BIGINT PRIMARY KEY AUTO_INCREMENT, document_id BIGINT NOT NULL, comment VARCHAR NOT NULL, created DATETIME NOT NULL);
ALTER TABLE COMMENTS ADD CONSTRAINT fk_comment_document_id FOREIGN KEY (document_id) REFERENCES DOCUMENT(id);