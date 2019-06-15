package dk.erst.delis.dao;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import dk.erst.delis.data.entities.document.Document;

public interface DocumentTableRepository extends DataTablesRepository<Document, Long> {

}
