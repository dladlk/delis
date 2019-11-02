package dk.erst.delis.web.error;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.ErrorDictionaryDaoRepository;
import dk.erst.delis.dao.JournalDocumentErrorDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.ErrorDictionary;
import dk.erst.delis.task.document.process.validate.SchemaValidator;
import dk.erst.delis.task.document.process.validate.result.ErrorRecord;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ErrorDictionaryService {

	private ErrorDictionaryDaoRepository errorDictionaryDaoRepository;
	private JournalDocumentErrorDaoRepository journalDocumentErrorDaoRepository;

	@Autowired
	public ErrorDictionaryService(ErrorDictionaryDaoRepository errorDictionaryDaoRepository, JournalDocumentErrorDaoRepository journalDocumentErrorDaoRepository) {
		this.errorDictionaryDaoRepository = errorDictionaryDaoRepository;
		this.journalDocumentErrorDaoRepository = journalDocumentErrorDaoRepository;
	}

	public List<Document> documentList(Long errorDictionaryId) {
		List<Document> documentList = journalDocumentErrorDaoRepository.loadDocumentByErrorId(errorDictionaryId);
		return documentList;
	}

	public ErrorDictionary getErrorDictionary(long id) {
		return errorDictionaryDaoRepository.findById(id).orElse(null);
	}

	public ErrorDictionary save(ErrorDictionary entity) {
		return this.errorDictionaryDaoRepository.save(entity);
	}

	public List<ErrorDictionary> findAllByHash(int hash) {
		return this.errorDictionaryDaoRepository.findAllByHashOrderByIdAsc(hash);
	}

	public StatData reorg() {
		StatData sd = new StatData();
		long start = System.currentTimeMillis();

		/*
		 * Reorg is done in 2 steps:
		 * 
		 * 1) walk through list of all errors and renormalize / recalculate hash on them
		 * 
		 * 2) check that there are no records, equal by hash and contents. If there are - replace links to first of them and delete unnecessary.
		 */
		renormalizeAll(sd);
		cleanDuplicates(sd);

		log.info("Done ErrorDictionaryService.reorg in " + (System.currentTimeMillis() - start) + " ms with result " + sd);

		return sd;
	}

	private void cleanDuplicates(StatData sd) {
		List<Object[]> duplicatedByHash = errorDictionaryDaoRepository.findDuplicatedByHash();
		for (Object[] objects : duplicatedByHash) {
			int hash = ((Number) objects[0]).intValue();

			List<ErrorDictionary> allByHash = errorDictionaryDaoRepository.findAllByHashOrderByIdAsc(hash);

			List<ErrorDictionary> uniqueListToLeave = new ArrayList<ErrorDictionary>();
			for (ErrorDictionary ed : allByHash) {
				Long duplicatedById = null;
				for (ErrorDictionary kept : uniqueListToLeave) {
					if (kept.equals(ed)) {
						duplicatedById = kept.getId();
					}
				}
				if (duplicatedById == null) {
					uniqueListToLeave.add(ed);
				} else {
					int updatedCount = journalDocumentErrorDaoRepository.updateErrorDictionaryId(ed.getId(), duplicatedById);
					sd.increase("JOURNAL_UPDATED", updatedCount);
					errorDictionaryDaoRepository.delete(ed);
					sd.increment("DELETED");
				}
			}
		}
	}

	private void renormalizeAll(StatData sd) {
		long previousId = 0L;
		boolean someFound = false;
		do {
			List<ErrorDictionary> part = errorDictionaryDaoRepository.loadListForReorg(previousId, PageRequest.of(0, 10));
			if (!part.isEmpty()) {
				someFound = true;
				previousId = part.get(part.size() - 1).getId();
				for (ErrorDictionary errorDictionary : part) {
					sd.increment("LOADED");

					int hashBefore = errorDictionary.calculateHash();

					if (log.isDebugEnabled()) {
						log.debug("Before:\t" + errorDictionary);
					}

					normalize(errorDictionary, true);

					int hashAfter = errorDictionary.calculateHash();

					if (hashBefore != hashAfter) {
						errorDictionary.setHash(hashAfter);
						errorDictionaryDaoRepository.save(errorDictionary);
						sd.increment("UPDATED");
					} else if (errorDictionary.getHash() != hashAfter) {
						errorDictionary.setHash(hashAfter);
						errorDictionaryDaoRepository.save(errorDictionary);
						sd.increment("REHASHED");
					} else {
						sd.increment("UNCHANGED");
					}
					if (log.isDebugEnabled()) {
						log.debug("After:\t" + errorDictionary);
					}
				}
			} else {
				someFound = false;
			}
		} while (someFound);
	}

	public void normalize(ErrorDictionary errorDictionary, boolean fixOldBugs) {
		errorDictionary.setCode(cutString(errorDictionary.getCode(), 50));
		errorDictionary.setFlag(cutString(errorDictionary.getFlag(), 20));
		errorDictionary.setLocation(cutString(errorDictionary.getLocation(), 500));
		errorDictionary.setMessage(cutString(errorDictionary.getMessage(), 1024));

		if (!fixOldBugs) {
			return;
		}
		
		if (errorDictionary.getErrorType().isXSD()) {
			/*
			 * Old version of SchemaValidator placed line XX, column YY to Location - which generates different instances for same error
			 */
			if (errorDictionary.getLocation().contains(", column ")) {
				errorDictionary.setLocation("");
			}
			SchemaValidator.cleanupXsdErrorRecord(errorDictionary);
		} else if (errorDictionary.getErrorType().isSCH()) {
			if (errorDictionary.getLocation().indexOf("namespace-uri()") > 0) {
				errorDictionary.setLocation(ErrorRecord.cleanupNamespaces(errorDictionary.getLocation()));
			}
			/*
			 * Old version left indexes in location of schematrons
			 */
			if (errorDictionary.getLocation().indexOf("[") > 0) {
				errorDictionary.setLocation(ErrorRecord.cleanupIndexes(errorDictionary.getLocation()));
			}
		}

	}

	private String cutString(String message, int maxLength) {
		if (message != null) {
			if (message.length() > maxLength) {
				return message.substring(0, maxLength - 5) + "...";
			}
		}
		return message;
	}
}
