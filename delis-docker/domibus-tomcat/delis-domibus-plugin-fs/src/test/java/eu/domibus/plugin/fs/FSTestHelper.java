package eu.domibus.plugin.fs;

import org.apache.commons.io.IOUtils;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;

public class FSTestHelper {
    public static File copyResource(Class<?> aClass, File tempDirectory, String resourceName) throws Exception {
        File file = new File(tempDirectory, resourceName);
        try (InputStream is = aClass.getResourceAsStream(resourceName); FileOutputStream fos = new FileOutputStream(file);) {
            IOUtils.copy(is, fos);
        }
        return file;
    }

    public static void setField(Object o, String fieldName, Object value) {
        Field field = ReflectionUtils.findField(o.getClass(), fieldName);
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, o, value);
    }
}
