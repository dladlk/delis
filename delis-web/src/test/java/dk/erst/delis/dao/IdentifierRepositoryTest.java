package dk.erst.delis.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class IdentifierRepositoryTest {

	@Autowired
	private IdentifierRepository identifierRepository;
	
	@Test
	public void testLoadIndetifierStat() {
		List<Map<String, Object>> stat = identifierRepository.loadIndetifierStat();
		int index = 0;
		for (Map<?, ?> map : stat) {
			System.out.println("List "+index);
			Set<?> keySet = map.keySet();
			for (Iterator<?> iterator = keySet.iterator(); iterator.hasNext();) {
				Object key = iterator.next();
				System.out.println("\t"+key +"("+ key.getClass() +")" +" = "+map.get(key) +"("+ map.get(key).getClass() +")");
				
			}
			index++;
		}
	}

}
