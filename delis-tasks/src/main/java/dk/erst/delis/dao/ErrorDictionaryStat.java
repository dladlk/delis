package dk.erst.delis.dao;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorDictionaryStat {
	
	private long documentCount;
	private Date minCreateTime;
	private Date maxCreateTime;
	
}