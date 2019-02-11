package dk.erst.delis.data.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@MappedSuperclass
public class AbstractCreateUpdateEntity extends AbstractCreateEntity {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATE_TIME", nullable = false)
    private Date updateTime;
	
    public AbstractCreateUpdateEntity() {
    	super();
        this.updateTime = new Date();
    }

    @PreUpdate
    public void onPreUpdate() {
        this.updateTime = new Date();
    }

}
