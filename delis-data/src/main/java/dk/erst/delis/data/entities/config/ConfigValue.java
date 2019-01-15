package dk.erst.delis.data.entities.config;

import javax.persistence.*;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.data.enums.config.ConfigValueType;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(indexes = {
		
		@Index(name="CV_CONFIG_VALUE_TYPE", columnList="CONFIG_VALUE_TYPE", unique = true),

})
public class ConfigValue extends AbstractEntity {

	@Enumerated(EnumType.STRING)
	@Column(name="CONFIG_VALUE_TYPE", nullable = false)
	private ConfigValueType configValueType;
	
	@Column(nullable = false)
	private String value;
}
