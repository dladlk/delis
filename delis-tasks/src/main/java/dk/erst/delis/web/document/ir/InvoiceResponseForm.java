package dk.erst.delis.web.document.ir;

import dk.erst.delis.task.document.response.ApplicationResponseService;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

@Getter
@Setter
public class InvoiceResponseForm extends AbstractApplicationResponseForm {

    @Delegate
    @Getter
    private ApplicationResponseService.InvoiceResponseGenerationData data = new ApplicationResponseService.InvoiceResponseGenerationData();

    @Override
    public boolean isMessageLevelResponse() {
        return false;
    }

    @Override
    public String getDocumentFormatName() {
        return "InvoiceResponse";
    }
}
