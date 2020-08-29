package qa.qdb.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
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
import qa.qdb.entities.Document;
import qa.qdb.model.DocumentsResponse;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class GetSubmitterDocumentsTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void setup() throws Exception {
        final Resource validDocumentResource = new ClassPathResource("documents/valid.pdf");
        final MockMultipartFile uploadFile = new MockMultipartFile("upload", 
                validDocumentResource.getFilename(), MediaType.APPLICATION_PDF_VALUE,
                validDocumentResource.getInputStream());

        mvc.perform(multipart(TestConst.ENDPOINT_DOCUMENT_UPLOAD_URL)
                .file(uploadFile)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .param(TestConst.PARAM_SUBMITTER, TestConst.SUBMITTER.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void givenExistingDocuments_whenRequestWithSubmitterThatHasDocs_thenSuccessfullyReturnDocuments()
            throws Exception {
        final MvcResult result = mvc.perform(get(String.format(
                    TestConst.ENDPOINT_GET_DOCUMENTS_URL, TestConst.SUBMITTER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documents.*", hasSize(1)))
                .andReturn();

        final DocumentsResponse response = mapper.readValue(result.getResponse().getContentAsString(
                StandardCharsets.UTF_8), DocumentsResponse.class);
        final Document saved = response.getDocuments().get(0);
        assertNotNull(saved.getId());
        assertNotNull(saved.getUuid());
        assertNotNull(saved.getContent());
        assertEquals("valid.pdf", saved.getFilename());
        assertEquals(TestConst.SUBMITTER, saved.getSubmitter());
    }

    @Test
    public void givenNonExistantSubmitterName_whenSubmitted_thenReturnA404() throws Exception {
        mvc.perform(get(String.format(
                    TestConst.ENDPOINT_GET_DOCUMENTS_URL, "888")))
                .andExpect(status().isNotFound());
    }
}
