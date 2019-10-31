package dk.erst.delis.task.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import dk.erst.delis.common.util.StatData;

@Component
public class TaskSchedulerMonitor {

	private Map<String, TaskResult> lastResultMap;

	public TaskSchedulerMonitor() {
		this.lastResultMap = new HashMap<String, TaskSchedulerMonitor.TaskResult>();
	}

	public TaskResult build(String taskName) {
		return new TaskResult(taskName, this);
	}
	
	public TaskResult getLast(String taskName) {
		return this.lastResultMap.get(taskName);
	}

	private void addResult(TaskResult result) {
		this.lastResultMap.put(result.getTaskName(), result);
	}

	public static class TaskResult {

		private String taskName;
		private Date startTime;
		private long duration;
		private boolean success;
		private Object result;

		private TaskSchedulerMonitor monitor;

		public TaskResult(String taskName, TaskSchedulerMonitor monitor) {
			this.taskName = taskName;
			this.monitor = monitor;
			this.startTime = Calendar.getInstance().getTime();
			this.duration = -1;
			this.success = false;
			this.result = null;
		}

		public void success(Object result) {
			this.success = true;
			this.result = result;
			this.duration = System.currentTimeMillis() - startTime.getTime();

			this.monitor.addResult(this);
		}

		public void failure(Exception e) {
			this.success = false;
			this.result = e.getMessage();
			this.duration = System.currentTimeMillis() - startTime.getTime();

			this.monitor.addResult(this);
		}

		public String getTaskName() {
			return taskName;
		}

		public Date getStartTime() {
			return startTime;
		}

		public long getDuration() {
			return duration;
		}

		public boolean isSuccess() {
			return success;
		}

		public Object getResult() {
			return result;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Last run ");
			long lastRunMs = System.currentTimeMillis() - this.startTime.getTime();
			sb.append(Math.round(lastRunMs / 1000.0));
			sb.append(" sec ago");
			sb.append(" for ");
			sb.append(this.duration);
			sb.append(" ms");
			sb.append(", result: ");
			sb.append(this.success ? "success" : "FAILURE");
			if (result != null) {
				if (result instanceof StatData) {
					StatData s = (StatData)result;
					if (!s.isEmpty()) {
						sb.append(", ");
						sb.append(result);
					}
				} else if (result instanceof Exception) {
					sb.append(((Exception)result).getMessage());
				} else if (result instanceof List<?>) {
					List<?> list = (List<?>) result;
					if (!list.isEmpty()) {
						sb.append(", ");
						sb.append(list.size());
						sb.append(" elements");
					}
				} else {
					sb.append(", ");
					sb.append(result);
				}
			}
			return sb.toString();
		}
	}
}
