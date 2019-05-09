package dk.erst.delis.web.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dk.erst.delis.dao.DocumentDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.enums.document.DocumentType;
import dk.erst.delis.web.json.JacksonConfig;
import org.junit.Test;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DocumentTableControllerTest {

    @Test
    public void listPOST() throws IOException {

        final List<Document> documentList = new ArrayList<>();

        Document doc = new Document();
        doc.setDocumentType(DocumentType.INVOICE);
        documentList.add(doc);

        DocumentDaoRepository repo = mock(DocumentDaoRepository.class);
        when(repo.findAll(any(DataTablesInput.class))).then(d -> {
            DataTablesOutput<Document> o = new DataTablesOutput<>();
            o.setData(documentList);
            return o;
        });

        DocumentTableController c = new DocumentTableController(repo);

        DataTablesInput input = new DataTablesInput();
        input.addColumn("documentType", false, false, null);
        input.addColumn("search", false, false, null);
        DataTablesOutput<Document> output = c.listPOST(input);

        ObjectMapper m = new JacksonConfig().jsonObjectMapper();

        m.enable(SerializationFeature.INDENT_OUTPUT);
        m.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        assertEquals(1, output.getData().size());

        StringWriter s = new StringWriter();
        m.writer().writeValue(s, output);

        System.out.println(s);

        assertTrue(s.toString().contains(DocumentType.INVOICE.getName()));
    }
}
