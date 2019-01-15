package dk.erst.delis.dao;

import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.enums.identifier.IdentifierPublishingStatus;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class IdentifierDaoRepositoryTest {

	@Autowired
	private IdentifierDaoRepository identifierDaoRepository;
	
	@Test
	public void testLoadIndetifierStat() {
		List<Map<String, Object>> stat = identifierDaoRepository.loadIndetifierStat();
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
	
	@Test
	public void testFindAllByDifferentLastFact() {
		assertTrue(identifierDaoRepository.getPendingForDeactivation(0, 0).isEmpty());
	}

	@Test
	public void revertAll() {
		List<Identifier> identifiers = identifierDaoRepository.findAll();
		for (Identifier identifier : identifiers) {
			identifier.setPublishingStatus(IdentifierPublishingStatus.PENDING);
			identifier.setStatus(IdentifierStatus.ACTIVE);
			identifierDaoRepository.saveAndFlush(identifier);
		}
	}
}
