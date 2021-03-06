package models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import util.LocalDateTimeConverter;

/**
 * 
 * @author Sami Farhat
 *
 */

@Entity
@Table (name="User")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name="firstName")	
	private String firstName; 

	@Column(name="lastName")
	private String lastName; 

	@Column(name="password")
//	@Type(type="hashedString")
	private String password; 

	@Column(name = "lastLogin")
    @Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime lastLogin; 
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
	private Set<Account> accounts= new HashSet<Account>(0);

	/** Constructor */ 
	public User() { }

	public String getFullName() { 
		return firstName + " " + lastName; 
	}
	public String getFirstName() {
		return firstName;
	}

	public String getPassword() {
		return password;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public String toString() {
		return getFullName(); 
	}

	public Set<Account> getAccounts() {
		return accounts;
	}

}
