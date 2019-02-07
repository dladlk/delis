package dk.erst.delis.task.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author funtusthan, created by 07.02.19
 */

@Component
public class TaskSchedulerParams {

    @Value("#{${job.documentLoad.interval} eq -1 ? 0x7fffffffffffffffL : ${job.documentLoad.interval}}")
    public Long jobDocumentLoadInterval;

    @Value("#{${job.documentValidate.interval} eq -1 ? 0x7fffffffffffffffL : ${job.documentValidate.interval}}")
    public Long jobDocumentValidateInterval;

    @Value("#{${job.documentDeliver.interval} eq -1 ? 0x7fffffffffffffffL : ${job.documentDeliver.interval}}")
    public Long jobDocumentDeliverInterval;
}
