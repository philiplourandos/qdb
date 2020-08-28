package qa.qdb.rest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import qa.qdb.entities.Document;
import qa.qdb.model.DocumentsResponse;
import qa.qdb.model.UploadedDocument;
import qa.qdb.repository.DocumentRepository;

@RestController
@RequestMapping("/document")
public class DocumentEndpoint {
    private static final Logger LOG = LogManager.getLogger(DocumentEndpoint.class);

    private static final String PDF_EXTENSION = ".pdf";

    private final DocumentRepository docRepo;

    public DocumentEndpoint(DocumentRepository docRepo) {
        this.docRepo = docRepo;
    }
            
    @PostMapping("/upload")
    public ResponseEntity add(@RequestParam final MultipartFile upload, 
            @RequestParam("submitter") @NonNull final String submitter) {
        final String filename = upload.getOriginalFilename();

        if (!filename.toLowerCase().endsWith(PDF_EXTENSION)) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        }

        try {
            final Path tmpFile = Files.createTempFile("file-upload", ".tmp");
            Files.write(tmpFile, upload.getBytes());

            final Tika tika = new Tika();
            final String mimeType = tika.detect(tmpFile);

            if (!MediaType.APPLICATION_PDF_VALUE.equals(mimeType)) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
            }
        } catch (IOException io) {
            LOG.error("Failed attempting to determine mime type of the uploaded file", io);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        try {
            final Document newDoc = new Document();
            newDoc.setSubmitter(submitter);
            newDoc.setFilename(filename);
            newDoc.setContent(upload.getBytes());
            newDoc.setUuid(UUID.randomUUID().toString());

            docRepo.save(newDoc);

            return ResponseEntity.ok(new UploadedDocument(newDoc.getUuid()));
        } catch(IOException io) {
            LOG.error("Unable to persist uploaded document to database", io);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{submitter}")
    public ResponseEntity getDocumentsForSubmitter(@PathVariable("submitter") final String submitter) {
        final List<Document> docs = docRepo.findBySubmitter(submitter);

        if (docs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(new DocumentsResponse(docs));
        }
    }
}
