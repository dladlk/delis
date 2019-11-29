package dk.erst.delis.data.entities.rule;

public interface IRuleDocument {

	public Long getId();
	
	public boolean isEqualData(IRuleDocument rule);
	
}
