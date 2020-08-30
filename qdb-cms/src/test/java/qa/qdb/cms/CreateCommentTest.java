package qa.qdb.cms;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import qa.qdb.client.model.Comment;
import qa.qdb.client.placeholder.PlaceholderClient;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CreateCommentTest {
    
    @Autowired
    private MockMvc mvc;

    @MockBean
    private PlaceholderClient mockClient;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void givenValidComment_whenSubmitted_thenRespondSuccessfully() throws Exception {
        //given
        final Comment comment = new Comment(1L, 2L, "larry", "larry@loungelizards.com", "misc");        
        final String content = mapper.writeValueAsString(comment);

        when(mockClient.createComment(any(Comment.class))).thenReturn(comment);

        //when
        mvc.perform(post("/cms/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk());

        //then
        verify(mockClient, times(1)).createComment(any(Comment.class));
    }
}
