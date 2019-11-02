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
		String q = "select  \n" + 
				"    d.id_pk as 'document.id', \n" + 
				"    date(d.create_time) as 'document.receved_date',  \n" + 
				"    d.document_date as 'document.issue_date',  \n" + 
				"    d.document_status as 'document.status',  \n" + 
				"    d.document_type as 'documnet.type',  \n" + 
				"    d.ingoing_document_format as 'document.format',  \n" + 
				"    d.last_error as 'document.last_error',  \n" + 
				"     \n" + 
				"     \n" + 
				"    d.receiver_country as 'receiver.country', \n" + 
				"    d.receiver_id_raw as 'receiver.number', \n" + 
				"    d.receiver_name as 'receiver.name', \n" + 
				"     \n" + 
				"    d.sender_country as 'sender.country', \n" + 
				"    d.sender_id_raw as 'sender.number', \n" + 
				"    d.sender_name as 'sender.name', \n" + 
				"     \n" + 
				"    ed.id_pk as 'error.id', \n" + 
				"    ed.code as 'error.code', \n" + 
				"    ed.error_type as 'error.type', \n" + 
				"    ed.flag as 'error.flag', \n" + 
				"    ed.message as 'error.message', \n" + 
				"    ed.location as 'error.location', \n" + 
				"     \n" + 
				"    jde.detailed_location as 'document.error.location', \n" + 
				"    jd.create_time as 'document.error.time', \n" + 
				"    jd.type as 'document.error.type'\n" + 
				"from document d \n" + 
				"    left join journal_document jd on jd.document_id = d.id_pk and jd.success = false and jd.type like 'VALIDATE%' \n" + 
				"    left join journal_document_error jde on jde.journal_document_id_pk = jd.id_pk \n" + 
				"    left join error_dictionary ed on ed.id_pk = jde.error_dictionary_id_pk";
		
		Query query = entityManager.createNativeQuery(q);
		return query.getResultList();
	}
}
