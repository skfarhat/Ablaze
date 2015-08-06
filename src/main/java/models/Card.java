package models;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import util.LocalDateConverter;


/**
 * 
 * @author Sami Farhat
 *
 */
@Entity
@Table (name="Card")
public class Card {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "name")
	private String name; 
 	
 	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "account", nullable = false)
	private Account account;  
 	
 	@Column(name = "cardType")
 	private String cardType;
 	
 	@Column(name = "cardNumber")
	private String cardNumber; 
 	
 	@Column(name = "expiryDate")
    @Convert(converter = LocalDateConverter.class)
	private LocalDate expiryDate;
	
	@Column(name = "securityCode")
	private String securityCode; 

	public String getCardNumber() {
		return cardNumber;
	}
	public Account getAccount() {
		return account;
	}
	public String getName() {
		return name;
	}
	public String getCardType() {
		return cardType;
	}
	public String getSecurityCode() {
		return securityCode;
	}
	public LocalDate getExpiryDate() {
		return expiryDate;
	}
	
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public void setExpiryDate(LocalDate expiryDate) {
		this.expiryDate = expiryDate;
	}
	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
	public void setName(String name) {
		this.name = name;
	}
}
