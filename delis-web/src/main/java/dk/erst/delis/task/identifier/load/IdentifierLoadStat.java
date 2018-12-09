package dk.erst.delis.task.identifier.load;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IdentifierLoadStat {

	private int total;
	private int add;
	private int update;
	private int delete;
	private int equal;
	private int failed;

	public int incrementTotal() {
		return this.total++;
	}

	public int incrementAdd() {
		return this.add++;
	}

	public int incrementUpdate() {
		return this.update++;
	}

	public int incrementDelete() {
		return this.delete++;
	}

	public int incrementEqual() {
		return this.equal++;
	}

	public int incrementFailed() {
		return this.failed++;
	}

}
