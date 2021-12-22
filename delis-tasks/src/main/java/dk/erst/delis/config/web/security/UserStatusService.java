package dk.erst.delis.config.web.security;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dk.erst.delis.data.entities.user.User;

@Service
public class UserStatusService {
	
	private int passwordLifeDays;
	private int maxInvalidLoginCount;
	private int invalidLoginLockMinutes;
	
	@Autowired
	public UserStatusService(
			
			@Value("${auth.passwordLifeDays:60}") int passwordLifeDays,

			@Value("${auth.maxInvalidLoginCount:5}") int maxInvalidLoginCount,

			@Value("${auth.invalidLoginLockMinutes:15}") int invalidLoginLockMinutes
			
			) {
		this.passwordLifeDays = passwordLifeDays;
		this.maxInvalidLoginCount = maxInvalidLoginCount;
		this.invalidLoginLockMinutes = invalidLoginLockMinutes;
		
	}

	public boolean isEnabled(User user) {
		return user != null && !user.isDisabled();
	}

	public boolean isAccountNonExpired(User user) {
		return true;
	}

	public boolean isCredentialsNonExpired(User user) {
		return user != null && !isPasswordExpired(user.getPasswordChangeTime(), this.passwordLifeDays);
	}

	private static boolean isPasswordExpired(Date passwordChangeTime, int passwordLifeDays) {
		if (passwordChangeTime != null) {
			Date passwordExpirationDate = calculatePasswordExpirationDate(passwordChangeTime, passwordLifeDays);
			return Calendar.getInstance().getTime().after(passwordExpirationDate);
		}
		return false;
	}
	
	public Date getPasswordExpirationTime(User user) {
		if (user != null) {
			return calculatePasswordExpirationDate(user.getPasswordChangeTime(), passwordLifeDays);
		}
		return null;
	}

	public static Date calculatePasswordExpirationDate(Date passwordChangeTime, int passwordLifeDays) {
		if (passwordChangeTime == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(passwordChangeTime);
		c.add(Calendar.DATE, passwordLifeDays);
		Date passwordExpirationDate = c.getTime();
		return passwordExpirationDate;
	}

	public boolean isAccountNonLocked(User user) {
		if (user.getInvalidLoginCount() != null && user.getInvalidLoginCount() >= this.maxInvalidLoginCount) {
			Date lastInvalidLoginTime = user.getLastInvalidLoginTime();
			if (lastInvalidLoginTime != null) { 
				Calendar c = Calendar.getInstance();
				c.setTime(lastInvalidLoginTime);
				c.add(Calendar.MINUTE, this.invalidLoginLockMinutes);
				
				Date unlockAfterInvalidLoginTime = c.getTime();
				boolean locked = Calendar.getInstance().getTime().before(unlockAfterInvalidLoginTime);
				return !locked;
			}
		}
		return true;
	}
}
