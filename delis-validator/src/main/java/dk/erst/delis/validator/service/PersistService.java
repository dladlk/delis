package dk.erst.delis.validator.service;

import static dk.erst.delis.validator.DelisValidatorPesistMode.FAILED;
import static dk.erst.delis.validator.DelisValidatorPesistMode.NONE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import com.google.common.io.Files;

import dk.erst.delis.task.document.parse.DocumentInfoService.DocumentInfoData;
import dk.erst.delis.validator.DelisValidatorConfig;
import dk.erst.delis.validator.DelisValidatorPesistMode;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PersistService {

	private DelisValidatorConfig delisValidatorConfig;

	public PersistService(DelisValidatorConfig delisValidatorConfig) {
		this.delisValidatorConfig = delisValidatorConfig;
	}

	public void persist(File tempFile, DocumentInfoData infoData, ValidateResult result) {
		DelisValidatorPesistMode persistMode = delisValidatorConfig.getPersistMode();
		boolean deleteFile = persistMode == NONE || (persistMode == FAILED && result.isSuccess());
		if (deleteFile) {
			if (infoData != null) {
				deleteFile(infoData.getFile());
				deleteFile(infoData.getFileSbd());
			} else {
				deleteFile(tempFile);
			}
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd_HH.mm.ss.SSS")));
			sb.append("_");
			sb.append(result.isSuccess() ? "OK" : "KO");
			sb.append("_");
			sb.append(String.format("%03d", Thread.currentThread().getId()));
			sb.append("_");
			sb.append(result.getDocumentFormat());
			sb.append("_");
			sb.append(result.getFileName());

			String generalPrefix = sb.toString();
			if (generalPrefix.length() > 200) {
				generalPrefix = generalPrefix.substring(0, 200);
			}

			String sbdFileName = generalPrefix + "_sbd.xml";
			String extractedFileName = generalPrefix + "_payload.xml";
			String resultFileName = generalPrefix + "_result.xml";

			File sbdFile = new File(this.delisValidatorConfig.getPersistFolder(), sbdFileName);
			File extractedFile = new File(this.delisValidatorConfig.getPersistFolder(), extractedFileName);
			File resultFile = new File(this.delisValidatorConfig.getPersistFolder(), resultFileName);

			persistResult(result, resultFile);

			if (infoData != null) {
				moveFile(infoData.getFile(), extractedFile);
				moveFile(infoData.getFileSbd(), sbdFile);
			} else {
				moveFile(tempFile, sbdFile);
			}
		}
	}

	protected void persistResult(ValidateResult result, File file) {
		if (result != null && result.getRestResult() != null && result.getRestResult().getBody() != null) {
			try (OutputStream out = new FileOutputStream(file)) {
				StreamUtils.copy(String.valueOf(result.getRestResult().getBody()).getBytes(StandardCharsets.UTF_8), out);
			} catch (Exception e) {
				log.error("Failed to persist result " + result + " to file " + file, e);
			}
		}
	}

	protected void moveFile(File from, File to) {
		if (from == null || to == null) {
			return;
		}
		try {
			Files.move(from, to);
		} catch (IOException e) {
			log.error("Failed to move " + from + " to " + to, e);
		}
	}

	protected void deleteFile(File f) {
		if (f != null) {
			if (!f.delete()) {
				f.deleteOnExit();
				log.info("Cannot delete file " + f + ", requested to delete on exit");
			} else {
				log.info("Successfully deleted file " + f);
			}
		}
	}
}
