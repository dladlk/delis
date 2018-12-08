package dk.erst.delis.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.Document;

public interface DocumentRepository extends PagingAndSortingRepository<Document, Long> {

}
