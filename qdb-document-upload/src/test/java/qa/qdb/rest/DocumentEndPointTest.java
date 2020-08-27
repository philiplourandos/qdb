package qa.qdb.rest;

import java.util.regex.Pattern;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DocumentEndPointTest {

    private static final Pattern UUID_REGEX = Pattern.compile("\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}");
    
    @Autowired
    private MockMvc mvc;
    
    @Test
    public void givenAPdfForUpload_whenSubmitted_thenSuccessfullySaveDocument() throws Exception {
        //given
        final Resource uploadResource = new ClassPathResource("documents/valid.pdf");
        final MockMultipartFile uploadFile = new MockMultipartFile("data", 
                uploadResource.getFilename(), MediaType.APPLICATION_PDF_VALUE,
                uploadResource.getInputStream());

        //when
        mvc.perform(multipart("/document/upload")
                .file(uploadFile)
                .param("submitter", "philip"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").isString())
                .andExpect(jsonPath("$.uuid").value(Matchers.matchesRegex(UUID_REGEX)));
    }

    @Test
    public void givenAValidPdfFile_whenUploadedWithNoSubmitter_theFailWithBadRequest() throws Exception {
        //given
        final Resource uploadResource = new ClassPathResource("documents/valid.pdf");
        final MockMultipartFile uploadFile = new MockMultipartFile("data", 
                uploadResource.getFilename(), MediaType.APPLICATION_PDF_VALUE,
                uploadResource.getInputStream());

        //when
        mvc.perform(multipart("/document/upload")
                .file(uploadFile))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void givenATxtFileForUpload_whenSubmitted_thenFailWith415() throws Exception {
        final Resource uploadResource = new ClassPathResource("documents/invalid-file.txt");
        final MockMultipartFile uploadFile = new MockMultipartFile("data", 
                uploadResource.getFilename(), MediaType.APPLICATION_PDF_VALUE,
                uploadResource.getInputStream());

        //when
        mvc.perform(multipart("/document/upload")
                .file(uploadFile)
                .param("submitter", "philip"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void givenAPdFileIncorrectlyNamed_whenSubmitted_thenFailWith415() throws Exception {
        final Resource uploadResource = new ClassPathResource("documents/invalid-extension.ps");
        final MockMultipartFile uploadFile = new MockMultipartFile("data", 
                uploadResource.getFilename(), MediaType.APPLICATION_PDF_VALUE,
                uploadResource.getInputStream());

        //when
        mvc.perform(multipart("/document/upload")
                .file(uploadFile)
                .param("submitter", "philip"))
                .andExpect(status().isUnsupportedMediaType());
    }
}
