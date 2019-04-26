package dk.erst.delis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.FixedDelayTask;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author funtusthan, created by 08.02.19
 */

@Configuration
@Slf4j
public class SchedulerConfig implements SchedulingConfigurer {

	private static final int POOL_SIZE = 4;

	@Value("${job.interval.sec.documentLoad}")
	private long documentLoad;

	@Value("${job.interval.sec.documentValidate}")
	private long documentValidate;

	@Value("${job.interval.sec.documentDeliver}")
	private long documentDeliver;

	@Value("${job.interval.sec.identifierLoad}")
	private long identifierLoad;

	@Value("${job.interval.sec.identifierPublish}")
	private long identifierPublish;

	@Override
	public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
		List<IntervalTask> taskList = scheduledTaskRegistrar.getFixedDelayTaskList();

		List<IntervalTask> newTaskList = new ArrayList<>();
		for (int i = 0; i < taskList.size(); i++) {
			IntervalTask intervalTask = taskList.get(i);
			long interval = getExpectedInterval(intervalTask);
			if (interval < 0) {
				log.info("Skip interval task " + intervalTask);
				continue;
			}
			FixedDelayTask newTask = new FixedDelayTask(intervalTask.getRunnable(), interval, interval);
			newTaskList.add(newTask);
			log.info("Set interval and delay to " + interval + " for " + newTask);
		}
		scheduledTaskRegistrar.setFixedDelayTasksList(newTaskList);

		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(POOL_SIZE);
		threadPoolTaskScheduler.setThreadNamePrefix("tasks-pool-");
		threadPoolTaskScheduler.initialize();
	}

	private long getExpectedInterval(IntervalTask intervalTask) {
		String t = intervalTask.toString();
		if (t.endsWith("documentDeliver")) {
			return this.documentDeliver * 1000;
		}
		if (t.endsWith("documentLoad")) {
			return this.documentLoad * 1000;
		}
		if (t.endsWith("documentValidate")) {
			return this.documentValidate * 1000;
		}
		if (t.endsWith("identifierLoad")) {
			return this.identifierLoad * 1000;
		}
		if (t.endsWith("identifierPublish")) {
			return this.identifierPublish * 1000;
		}
		return Long.MAX_VALUE;
	}
}