package dk.erst.delis.document.sbdh;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.sbdh.SbdReader;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;

public class SBDHTranslatorTest {

	private static final Logger log = LoggerFactory.getLogger(SBDHTranslatorTest.class);

	private SBDHTranslator service = new SBDHTranslator();
	private File resourcesFolder = new File("../delis-resources/examples/xml");

	private static String suffix = "_sbdh.xml";
	private static String metadataSuffix = "_metadata.xml";

	
	@Test
	public void testBis3NoUBLVersionId() throws Exception {
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		f.setNamespaceAware(false);
		for (File sourceFile : resourcesFolder.listFiles(pathname -> pathname.isFile() && pathname.getName().contains("BIS3"))) {
			System.out.println("Processing file "+sourceFile);
			
			File resultFile = null;
			
			Document doc = f.newDocumentBuilder().parse(new FileInputStream(sourceFile));
			Element rootElement = doc.getDocumentElement();
			NodeList nodeList = rootElement.getElementsByTagName("cbc:UBLVersionID");
			if (nodeList.getLength() > 0) {
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					node.getParentNode().removeChild(node);
				}
			}
			File inputFile = File.createTempFile(this.getClass().getSimpleName() + "_", "_bis3_no_UBLVersionID.xml");
			inputFile.deleteOnExit();
			try (OutputStream fos = new FileOutputStream(inputFile)) {
				XmlUtil.serialize(doc, fos);
			}
			System.out.println("Generated example without UBLVersionID: " + inputFile);

			resultFile = File.createTempFile(this.getClass().getSimpleName() + "_", "_sbdh.xml");
			resultFile.deleteOnExit();
			Header expected = service.addHeader(inputFile.toPath(), resultFile.toPath());
			System.out.println("Document type: " + expected.getDocumentType().getIdentifier());
			assertTrue(expected.getDocumentType().getIdentifier().endsWith("::2.1"));
		}
	}

	@Test
	public void testInvoiceResponse() throws Exception {
		checkFile(new File(resourcesFolder, "BIS3_InvoiceResponse_Example.xml"));
	}
	
	@Test
	public void testAllResourceExamples() throws Exception {
		for (File sourceFile : resourcesFolder.listFiles(pathname -> pathname.isFile())) {
			checkFile(sourceFile);
		}
	}

	private void checkFile(File sourceFile) throws IOException, FileNotFoundException, SbdhException {
		log.info("Testing " + sourceFile);

		File tempSourceFile = File.createTempFile(sourceFile.getName(), "tmp.xml");
		Files.copy(sourceFile.toPath(), tempSourceFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		File targetFile = File.createTempFile(sourceFile.getName(), suffix);
		File metadataFile = File.createTempFile(sourceFile.getName(), metadataSuffix);
		System.out.println("Prepared temp source file " + tempSourceFile);
		Header expected;
		try {
			expected = service.addHeader(tempSourceFile.toPath(), targetFile.toPath());
		} finally {
			assertTrue(tempSourceFile.delete());
		}
		boolean writeMetadata = service.writeMetadata(expected, "testPartyIdValue", metadataFile.toPath());
		FileInputStream inputStream = new FileInputStream(targetFile);
		SbdReader reader = SbdReader.newInstance(inputStream);
		inputStream.close();
		Header actual = reader.getHeader();
		assertTrue(actual.equals(expected));
		assertTrue(writeMetadata);
		assertTrue(targetFile.delete());
		assertTrue(metadataFile.delete());
	}
}
