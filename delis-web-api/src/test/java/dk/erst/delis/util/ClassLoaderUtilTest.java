package dk.erst.delis.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
public class ClassLoaderUtilTest {

    @Test
    public void findAllWebApiContentEntityClassesTest() {
        List<Class<?>> listClasses = ClassLoaderUtil.findAllWebApiContentEntityClasses();
        Assert.assertTrue(CollectionUtils.isNotEmpty(listClasses));
        for (Class<?> aClass : listClasses) {
            List<Field> fields = ClassLoaderUtil.getAllFieldsByEntity(aClass);
            Assert.assertTrue(CollectionUtils.isNotEmpty(fields));
            String entityName = ClassLoaderUtil.generateEntityByFullNameClass(aClass);
            Assert.assertTrue(StringUtils.isNotBlank(entityName));
            log.info("entityName: " + entityName);
        }
    }
}
