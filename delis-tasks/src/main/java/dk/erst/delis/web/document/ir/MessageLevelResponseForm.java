package dk.erst.delis.web.document.ir;

import dk.erst.delis.task.document.response.ApplicationResponseService;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

@Getter
@Setter
public class MessageLevelResponseForm extends AbstractApplicationResponseForm {
    @Delegate
    @Getter
    private ApplicationResponseService.MessageLevelResponseGenerationData data = new ApplicationResponseService.MessageLevelResponseGenerationData();

    @Override
    public boolean isMessageLevelResponse() {
        return true;
    }

    @Override
    public String getDocumentFormatName() {
        return "MessageLevelResponse";
    }
}