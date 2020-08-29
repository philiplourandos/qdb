package qa.qdb.cms;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import qa.qdb.client.model.PostResponse;
import qa.qdb.client.placeholder.PlaceholderClient;
import qa.qdb.entities.Document;
import qa.qdb.repository.DocumentRepository;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GetPostTest {
    private static final Long POST_ID = 565656L;

    private static final String TITLE = "the greatest show on earth";
    private static final String BODY = "well...the body does not fit the title";
    
    @Autowired
    private MockMvc mvc;

    @MockBean
    private PlaceholderClient mockClient;

    @Autowired
    private DocumentRepository docRepo;

    @Test
    public void givenValidDocumentIdWithPost_whenRequested_thenReturnPostSuccessfully() throws Exception {
        //given
        final PostResponse mockResponse = new PostResponse(TITLE, BODY, 0, POST_ID);
        when(mockClient.getPost(any(Long.class))).thenReturn(mockResponse);

        final Optional<Document> found = docRepo.findBySubmitter(778L).stream().findFirst();

        //when
        mvc.perform(get(String.format(TestConst.ENDPOINT_GET_POST, found.get().getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(is(TITLE)))
                .andExpect(jsonPath("$.body").value(is(BODY)));

        //then
        verify(mockClient, times(1)).getPost(eq(POST_ID));
    }

    @Test
    public void givenNonExistantDocumentId_whenSubmitted_theFailWith404() throws Exception {
        mvc.perform(get(String.format(TestConst.ENDPOINT_GET_POST, "12345")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenNoDocumentIdInUrl_whenRequested_thenFailWith404() throws Exception {
        mvc.perform(get(String.format(TestConst.ENDPOINT_GET_POST, "")))
                .andExpect(status().isNotFound());
    }
}
