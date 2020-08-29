package qa.qdb.client.model;

import java.io.Serializable;

public class NewPostRequest implements Serializable {
    private String title;
    private String body;
    private long userId;

    public NewPostRequest() {
    }

    public NewPostRequest(String title, String body, long userId) {
        this.title = title;
        this.body = body;
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
