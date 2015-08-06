package models;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 
 * @author Sami
 *
 */
@Entity
@Table (name="Account")
public class Account {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name="accountNumber")	
	private Integer accountNumber; 

	/**
	 * <p> 
	 * usually has the following format: <br> 
	 * xx-xx-xx (e.g. 12-34-56) 
	 * </p> 
	 */
	@Column(name="sortCode")	
	private String sortCode; 
	
	@Column(name="name")	
	private String name; 
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "account")
	private Set<Card> cards = new HashSet<Card>(0);
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user", nullable = false)
	private User user;
	
	public Integer getAccountNumber() {
		return accountNumber;
	}
	public String getSortCode() {
		return sortCode;
	}
	public Set<Card> getCards() {
		return cards;
	}
//	public LocalDate getDateCreated() {
//		return dateCreated;
//	}
	public String getName() {
		return name;
	}
	public User getUser() {
		return user;
	}
	
	public void setSortCode(String sortCode) {
		this.sortCode = sortCode;
	}
	public void setAccountNumber(Integer accountNumber) {
		this.accountNumber = accountNumber;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	@Override
	public String toString() {
		return name; 
	}
}
