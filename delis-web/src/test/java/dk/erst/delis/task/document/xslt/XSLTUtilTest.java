package dk.erst.delis.task.document.xslt;

import dk.erst.delis.task.document.parse.XSLTUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
@SpringBootTest
public class XSLTUtilTest {

    @Test
    public void test() throws Exception {
        Path path1 = Paths.get("../delis-resources/transformation/bis3_to_oioubl/v_2018-03-14_34841/BIS-Billing-INV_2_OIOUBL_INV.xslt");
        Path path2 = Paths.get("../delis-resources/validation/sch/bis3/cen_2018-03-15_1/CEN-EN16931-UBL.xslt");
//        testTransformerCreation(path1, 10);
        testTransformerCreation(path2, 10);

    }

    private void testTransformerCreation(Path xslFilePath, int creationTimes) throws Exception {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used Memory before: " + usedMemoryBefore);
        long start = System.currentTimeMillis();
        for (int i = 0; i < creationTimes; i++) {
            XSLTUtil.getTransformer(new FileInputStream(xslFilePath.toFile()), xslFilePath);
        }
        long finish = System.currentTimeMillis();
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Memory increased: " + (usedMemoryAfter-usedMemoryBefore));
        System.out.println("Time spent: " + (finish - start));
    }

}
