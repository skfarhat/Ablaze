package models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

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
	private String password; 

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
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

	public Integer getId() {
		return id;
	}

	@Override
	public String toString() {
		return getFullName(); 
	}


}
