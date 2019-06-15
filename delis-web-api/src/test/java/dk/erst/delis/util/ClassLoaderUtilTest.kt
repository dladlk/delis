package dk.erst.delis.util

import org.junit.Test
import org.junit.runner.RunWith

import org.springframework.test.context.junit4.SpringRunner

import kotlin.test.assertTrue

/**
 * @author funtusthan, created by 21.03.19
 */

@RunWith(SpringRunner::class)
class ClassLoaderUtilTest {

    @Test
    fun findAllWebApiContentEntityClassesTest() {
        val listClasses = ClassLoaderUtil.findAllWebApiContentEntityClasses()
        assertTrue(listClasses.isNotEmpty())
        listClasses.forEach{
            val fields = ClassLoaderUtil.getAllFieldsByEntity(it)
            assertTrue(fields.isNotEmpty())
            val entityName = ClassLoaderUtil.generateEntityByFullNameClass(it)
            assertTrue(entityName.isNotBlank())
            println("entityName: $entityName")
        }
    }
}
