package dk.erst.delis.web.document.ir;

import dk.erst.delis.task.document.response.ApplicationResponseService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractApplicationResponseForm {

    private long documentId;
    private String usecase;
    private boolean generateWithoutSending = true;
    private boolean validate = true;

    public abstract boolean isMessageLevelResponse();

    public abstract String getDocumentFormatName();

    public abstract ApplicationResponseService.ApplicationResponseGenerationData getData();
}
