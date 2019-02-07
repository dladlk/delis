package dk.erst.delis.task.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author funtusthan, created by 07.02.19
 */

@Component
public class TaskSchedulerParams {

    @Value("#{${job.documentLoad.interval} <= 0 ? ${job.default.interval}: ${job.documentLoad.interval}}")
    public Long jobDocumentLoadInterval;

    @Value("#{${job.documentValidate.interval} <= 0 ? ${job.default.interval}: ${job.documentValidate.interval}}")
    public Long jobDocumentValidateInterval;

    @Value("#{${job.documentDeliver.interval} <= 0 ? ${job.default.interval}: ${job.documentDeliver.interval}}")
    public Long jobDocumentDeliverInterval;
}
