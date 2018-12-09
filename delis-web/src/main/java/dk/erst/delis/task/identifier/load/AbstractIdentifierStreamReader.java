package dk.erst.delis.task.identifier.load;

import dk.erst.delis.data.Identifier;

public abstract class AbstractIdentifierStreamReader {

	public void start() {
	}
	
	public abstract Identifier next();
	
	public void close() {
	}
	
}
