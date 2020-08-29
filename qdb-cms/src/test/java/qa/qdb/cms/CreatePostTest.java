package qa.qdb.cms;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import qa.qdb.client.model.NewPostRequest;
import qa.qdb.client.model.PostResponse;
import qa.qdb.client.placeholder.PlaceholderClient;
import qa.qdb.entities.Document;
import qa.qdb.repository.DocumentRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CreatePostTest {

    private static final Long POST_ID = 4785638L;

    @MockBean
    private PlaceholderClient mockClient;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DocumentRepository docRepo;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void givenAnExistingDocument_whenUserCreatesASuccessfulPost_thenLinkPostToDocument() throws Exception {
        //given
        final Document foundDoc = docRepo.findBySubmitter(TestConst.VALID_SUBMITTER_ID).get(0);
        final NewPostRequest request = new NewPostRequest("title", "body", TestConst.VALID_SUBMITTER_ID);

        final String requestContent = mapper.writeValueAsString(request);

        final PostResponse mockedRepsonse = new PostResponse("", "", 0, POST_ID);

        when(mockClient.createPost(any(NewPostRequest.class))).thenReturn(mockedRepsonse);

        //when
        mvc.perform(post(
                String.format(TestConst.ENDPOINT_CREATE_POST, foundDoc.getId().toString()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent))
                .andExpect(status().isCreated());

        //then
        final Optional<Document> updatedDoc = docRepo.findById(foundDoc.getId());
        updatedDoc.ifPresentOrElse(a -> assertEquals(POST_ID, a.getPostId()), () -> fail());

        verify(mockClient, times(1)).createPost(any(NewPostRequest.class));
    }

    @Test
    public void givenANonExistantDocumentWithAValidPostPayload_whenSubmitted_thenReturn404() throws Exception {
        //given
        final NewPostRequest request = new NewPostRequest("title", "body", TestConst.VALID_SUBMITTER_ID);
        final String requestContent = mapper.writeValueAsString(request);

        final PostResponse mockedRepsonse = new PostResponse("", "", 0, POST_ID);

        when(mockClient.createPost(any(NewPostRequest.class))).thenReturn(mockedRepsonse);

        //when
        mvc.perform(post(
                String.format(TestConst.ENDPOINT_CREATE_POST, "9110990990"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent))
                .andExpect(status().isNotFound());

        //then
        verify(mockClient, never()).createPost(any(NewPostRequest.class));
    }
}
