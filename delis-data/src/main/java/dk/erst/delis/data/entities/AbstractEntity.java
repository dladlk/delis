package dk.erst.delis.data.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * @author funtusthan, created by 12.01.19
 */


@Getter
@Setter
@ToString
@MappedSuperclass
public class AbstractEntity {

    @Id
    @Column(name = "ID_PK")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_TIME", nullable = false, updatable = false)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATE_TIME", nullable = false)
    private Date updateTime;

    public AbstractEntity() {
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    @PreUpdate
    public void onPreUpdate() {
        this.updateTime = new Date();
    }
}
