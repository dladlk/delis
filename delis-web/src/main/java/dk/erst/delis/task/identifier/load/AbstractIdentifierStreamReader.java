package dk.erst.delis.task.identifier.load;

import java.util.Iterator;

import dk.erst.delis.data.Identifier;

public abstract class AbstractIdentifierStreamReader implements Iterator<Identifier> {

	public abstract boolean hasNext();
	
	public abstract Identifier next();
	
	public void close() {
	}
	
}
