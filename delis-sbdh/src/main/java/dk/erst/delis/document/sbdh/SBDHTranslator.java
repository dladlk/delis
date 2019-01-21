package dk.erst.delis.document.sbdh;

import no.difi.oxalis.api.lang.OxalisContentException;
import no.difi.oxalis.sniffer.document.NoSbdhParser;
import no.difi.oxalis.sniffer.sbdh.SbdhWrapper;
import no.difi.vefa.peppol.common.model.Header;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SBDHTranslator {

    public void addHeader(Path source, Path target) {
        try {
            byte[] sourceDocBytes = Files.readAllBytes(source);
            NoSbdhParser sbdhParser = new NoSbdhParser();
            SbdhWrapper sbdhWrapper = new SbdhWrapper();
            Header header = sbdhParser.parse(new ByteArrayInputStream(sourceDocBytes));
            byte[] wrappedDocBytes = sbdhWrapper.wrap(new ByteArrayInputStream(sourceDocBytes), header);
            Files.write(target, wrappedDocBytes);
        } catch (IOException | OxalisContentException e) {
            e.printStackTrace();
        }
    }
}
