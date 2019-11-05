package dk.erst.delis.task.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import dk.erst.delis.common.util.StatData;

@Component
public class TaskSchedulerMonitor {

	private Map<String, TaskResult[]> lastResultMap;
	
	public TaskSchedulerMonitor() {
		this.lastResultMap = new HashMap<String, TaskSchedulerMonitor.TaskResult[]>();
	}

	public TaskResult build(String taskName) {
		return new TaskResult(taskName, this);
	}
	
	public synchronized String getLast(String taskName) {
		TaskResult[] taskResults = this.lastResultMap.get(taskName);
		return buildInfo(taskResults);
	}

	private String buildInfo(TaskResult[] taskResults) {
		if (taskResults == null) {
			return "Not yet run";
		}
		TaskResult prev = taskResults[1];
		TaskResult last = taskResults[0];
		StringBuilder sb = new StringBuilder();
		if (last != null) {
			sb.append("Last run ");
			sb.append(last);
		}
		if (prev != null) {
			sb.append(", previous ");
			sb.append(prev);
		}
		return sb.toString();
	}

	private synchronized void addResult(TaskResult result) {
		TaskResult[] taskResults = this.lastResultMap.get(result.getTaskName());
		if (taskResults == null) {
			taskResults = new TaskResult[2];
			taskResults[0] = result;
			this.lastResultMap.put(result.getTaskName(), taskResults);
		} else {
			taskResults[1] = taskResults[0];
			taskResults[0] = result;
		}
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
			long lastRunMs = System.currentTimeMillis() - this.startTime.getTime();
			sb.append(Math.round(lastRunMs / 1000.0));
			sb.append(" sec ago");
			sb.append(" for ");
			sb.append(this.duration);
			sb.append(" ms");
			if (!success) {
				sb.append(" FAILURE");
			}
			if (result != null) {
				String resultText = null;
				if (result instanceof StatData) {
					StatData s = (StatData)result;
					if (!s.isEmpty()) {
						resultText=  String.valueOf(result);
					}
				} else if (result instanceof Exception) {
					resultText = ((Exception)result).getMessage();
				} else if (result instanceof List<?>) {
					List<?> list = (List<?>) result;
					if (!list.isEmpty()) {
						resultText=  String.valueOf(list.size()) + " elements";
					}
				} else {
					resultText = String.valueOf(result);
				}
				if (StringUtils.isNotEmpty(resultText)) {
					sb.append(", ");
					sb.append(resultText);
				}
			}
			return sb.toString();
		}
	}
}
