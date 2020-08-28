package qa.qdb.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import qa.qdb.entities.Document;
import qa.qdb.repository.DocumentRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import qa.qdb.model.UploadedDocument;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DocumentEndPointTest {

    private static final Pattern UUID_REGEX = Pattern.compile("\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}");
    
    @Autowired
    private MockMvc mvc;

    @Autowired
    private DocumentRepository docRepo;

    @Autowired
    private ObjectMapper mapper;
    
    @Test
    public void givenAPdfForUpload_whenSubmitted_thenSuccessfullySaveDocument() throws Exception {
        //given
        final String submitter = "philip";
        final Resource uploadResource = new ClassPathResource("documents/valid.pdf");
        final MockMultipartFile uploadFile = new MockMultipartFile("upload", 
                uploadResource.getFilename(), MediaType.APPLICATION_PDF_VALUE,
                uploadResource.getInputStream());

        //when
        final MvcResult result = mvc.perform(multipart("/document/upload")
                .file(uploadFile)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .param("submitter", submitter))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").isString())
                .andExpect(jsonPath("$.uuid").value(Matchers.matchesRegex(UUID_REGEX)))
                .andReturn();

        //then
        final UploadedDocument reponse = mapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                UploadedDocument.class);
        
        final List<Document> savedDocs = docRepo.findBySubmitter(submitter);
        assertNotNull(savedDocs);
        assertEquals(1, savedDocs.size());

        final Document savedDoc = savedDocs.get(0);
        assertEquals(submitter, savedDoc.getSubmitter());
        assertEquals(uploadResource.getFilename(), savedDoc.getFilename());
        assertEquals(reponse.getUuid(), savedDoc.getUuid());
    }

    @Test
    public void givenAValidPdfFile_whenUploadedWithNoSubmitter_theFailWithBadRequest() throws Exception {
        //given
        final Resource uploadResource = new ClassPathResource("documents/valid.pdf");
        final MockMultipartFile uploadFile = new MockMultipartFile("upload", 
                uploadResource.getFilename(), MediaType.APPLICATION_PDF_VALUE,
                uploadResource.getInputStream());

        //when
        mvc.perform(multipart("/document/upload")
                .file(uploadFile)
                .contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void givenATxtFileForUpload_whenSubmitted_thenFailWith415() throws Exception {
        final Resource uploadResource = new ClassPathResource("documents/invalid-file.txt");
        final MockMultipartFile uploadFile = new MockMultipartFile("upload", 
                uploadResource.getFilename(), MediaType.APPLICATION_PDF_VALUE,
                uploadResource.getInputStream());

        //when
        mvc.perform(multipart("/document/upload")
                .file(uploadFile)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .param("submitter", "philip"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void givenAPdFileIncorrectlyNamed_whenSubmitted_thenFailWith415() throws Exception {
        final Resource uploadResource = new ClassPathResource("documents/invalid-extension.ps");
        final MockMultipartFile uploadFile = new MockMultipartFile("upload", 
                uploadResource.getFilename(), MediaType.APPLICATION_PDF_VALUE,
                uploadResource.getInputStream());

        //when
        mvc.perform(multipart("/document/upload")
                .file(uploadFile)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .param("submitter", "philip"))
                .andExpect(status().isUnsupportedMediaType());
    }
}
