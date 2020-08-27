package qa.qdb.model;

import java.io.Serializable;

public class UploadedDocument implements Serializable {
    private String uuid;

    public UploadedDocument() {
    }

    public UploadedDocument(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
