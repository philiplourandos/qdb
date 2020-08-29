package qa.qdb.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import qa.qdb.model.UploadedDocument;
import qa.qdb.repository.DocumentRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
public class RemoveDocumentTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private DocumentRepository docRepo;

    private String validUuid;

    @BeforeEach
    public void setup() throws Exception {
        validUuid = createDocument(TestConst.SUBMITTER.toString());
    }

    private String createDocument(final String submitter) throws Exception {
        final Resource validDocumentResource = new ClassPathResource("documents/valid.pdf");
        final MockMultipartFile uploadFile = new MockMultipartFile("upload", 
                validDocumentResource.getFilename(), MediaType.APPLICATION_PDF_VALUE,
                validDocumentResource.getInputStream());

        final MvcResult result = mvc.perform(multipart(TestConst.ENDPOINT_DOCUMENT_UPLOAD_URL)
                .file(uploadFile)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .param(TestConst.PARAM_SUBMITTER, submitter))
                .andExpect(status().isOk())
                .andReturn();

        final UploadedDocument response = mapper.readValue(result.getResponse().getContentAsString(
                StandardCharsets.UTF_8), UploadedDocument.class);

        return response.getUuid();
    }

    @Test
    public void givenARequestWithNoSubmitter_whenSubmitted_thenFailWithNotFound() throws Exception {
        mvc.perform(delete(String.format(TestConst.ENDPOINT_DELETE_DOCUMENT, "", validUuid)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenARequestWithNoUuid_whenSubmitted_thenFailWithNotFound() throws Exception {
        mvc.perform(delete(String.format(TestConst.ENDPOINT_DELETE_DOCUMENT, TestConst.SUBMITTER, "")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenARequestWithUUIDThatDoesNotBelongToSubmitter_whenSubmitted_thenFailWith404() throws Exception {
        mvc.perform(delete(String.format(TestConst.ENDPOINT_DELETE_DOCUMENT,
                TestConst.SUBMITTER, UUID.randomUUID().toString())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenAMatchingSubmitterAndUuid_whenSubmitted_whenSuccessfullyDelete() throws Exception {
        mvc.perform(delete(String.format(TestConst.ENDPOINT_DELETE_DOCUMENT,
                TestConst.SUBMITTER, validUuid)))
                .andExpect(status().isOk());

        assertTrue(docRepo.findBySubmitterAndUuid(TestConst.SUBMITTER, validUuid).isEmpty());
    }
}
