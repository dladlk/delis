package dk.erst.delis.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;

import dk.erst.delis.dao.DocumentDao;
import dk.erst.delis.data.entities.document.Document;

public class DocumentDaoImpl implements DocumentDao {

	@Autowired
	private EntityManager entityManager;

	@Override
	public void updateDocumentStatus(Document document) {
		Query q = entityManager.createQuery("update Document set documentStatus = :documentStatus, lastError = :lastError where id = :id");
		q.setParameter("documentStatus", document.getDocumentStatus());
		q.setParameter("lastError", document.getLastError());
		q.setParameter("id", document.getId());
		q.executeUpdate();
	}

	
	@SuppressWarnings("unchecked")
	public List<Object[]> loadDocumentHistory() {
		String q = "select \n" + 
				"    d.id_pk as 'Document ID',\n" + 
				"    date(d.create_time) as document_receved_date, \n" + 
				"    d.document_date, \n" + 
				"    d.document_status, \n" + 
				"    d.document_type, \n" + 
				"    d.ingoing_document_format as document_format, \n" + 
				"    d.last_error as document_last_error, \n" + 
				"    \n" + 
				"    \n" + 
				"    d.receiver_country,\n" + 
				"    d.receiver_id_raw,\n" + 
				"    d.receiver_name,\n" + 
				"	\n" + 
				"    d.sender_country,\n" + 
				"    d.sender_id_raw,\n" + 
				"    d.sender_name,\n" + 
				"    \n" + 
				"    ed.id_pk as error_id,\n" + 
				"    ed.code as error_code,\n" + 
				"    ed.error_type as error_type,\n" + 
				"    ed.flag as error_flag,\n" + 
				"    ed.message as error_message,\n" + 
				"    ed.location as error_location,\n" + 
				"    \n" + 
				"    jde.detailed_location as document_error_location,\n" + 
				"    jd.create_time as validation_time,\n" + 
				"    jd.type as step_type\n" + 
				"from document d\n" + 
				"    left join journal_document jd on jd.document_id = d.id_pk and jd.success = false\n" + 
				"    left join journal_document_error jde on jde.journal_document_id_pk = jd.id_pk\n" + 
				"    left join error_dictionary ed on ed.id_pk = jde.error_dictionary_id_pk\n";
		
		Query query = entityManager.createNativeQuery(q);
		return query.getResultList();
	}
}
