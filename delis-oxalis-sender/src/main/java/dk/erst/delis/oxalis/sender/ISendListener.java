package dk.erst.delis.oxalis.sender;

public interface ISendListener {
	
	public void notifySendStepStart(SendStep sendStep);
	
	public void notifySendStepResult(SendStep sendStep, Object sendStepResult);

	public void notifySendStepError(SendStep sendStep, Exception sendStepException);

	public static class NoneListener implements ISendListener {
		@Override
		public void notifySendStepStart(SendStep sendStep) {
		}

		@Override
		public void notifySendStepResult(SendStep sendStep, Object sendStepResult) {
		}

		@Override
		public void notifySendStepError(SendStep sendStep, Exception sendStepException) {
		}
		
	}

	public static NoneListener NONE = new NoneListener();
	
}
