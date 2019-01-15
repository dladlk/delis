package dk.erst.delis.task.identifier.load;

import dk.erst.delis.data.entities.identifier.Identifier;

import java.util.Iterator;

public abstract class AbstractIdentifierStreamReader implements Iterator<Identifier> {

	public abstract boolean hasNext();
	
	public abstract Identifier next();
	
	public void close() {
	}
	
}
