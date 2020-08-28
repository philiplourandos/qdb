package qa.qdb.converter;

import java.sql.Blob;
import java.sql.SQLException;
import javax.sql.rowset.serial.SerialBlob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class BlobConverter implements Converter<byte[], Blob> {
    private static final Logger LOG = LogManager.getLogger(BlobConverter.class);
    
    @Override
    public Blob convert(final byte[] source) {
        try {
            return new SerialBlob(source);
        } catch (SQLException e) {
            LOG.error("", e);

            throw new RuntimeException("Unable to read BLOB", e);
        }
    }
}
