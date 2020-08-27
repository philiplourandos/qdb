package qa.qdb.rest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
public class DocumentEndPointTest {
    @Test
    public void givenAPdfForUpload_whenSubmitted_thenSuccessfullySaveDocument() throws Exception {
        fail();
    }

    @Test
    public void givenATxtFileForUpload_whenSubmitted_thenFailWith415() throws Exception {
        fail();
    }

    @Test
    public void givenAPdFileIncorrectlyNamed_whenSubmitted_thenFailWith415() throws Exception {
        fail();
    }
}
