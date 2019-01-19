package dk.erst.delis.task.identifier.publish.sbdh;

import lombok.extern.slf4j.Slf4j;
import no.difi.oxalis.api.lang.OxalisContentException;
import no.difi.oxalis.sniffer.document.NoSbdhParser;
import no.difi.oxalis.sniffer.sbdh.SbdhWrapper;
import no.difi.vefa.peppol.common.model.Header;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@Slf4j
public class SBDHTranslatorService {

    private static final String CHARSET_NAME = "utf-8";

    public String translate(String sourceDocString) {
        String result = sourceDocString;
        try {
            byte[] sourceDocBytes = sourceDocString.getBytes(CHARSET_NAME);
            NoSbdhParser sbdhParser = new NoSbdhParser();
            Header header = sbdhParser.parse(new ByteArrayInputStream(sourceDocBytes));
            if(header != null) {
                SbdhWrapper sbdhWrapper = new SbdhWrapper();
                byte[] wrappedDocBytes = sbdhWrapper.wrap(new ByteArrayInputStream(sourceDocBytes), header);
                result = new String(wrappedDocBytes, CHARSET_NAME);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (OxalisContentException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void addHeader(Path source, Path target) {
        try {
            List<String> lines = Files.readAllLines(source);
            String fileContent = StringUtils.join(lines, "");
            String translatedContent = translate(fileContent);
            Files.write(target, translatedContent.getBytes(CHARSET_NAME));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
