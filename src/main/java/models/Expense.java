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
 * @author Sami
 *
 */
@Entity
@Table (name="Expense")
public class Expense {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id; 

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "account", nullable = false)
	private Account account; 

	@Column(name = "description")
	private String description;
	
	@Column(name = "dateIncurred")
    @Convert(converter = LocalDateConverter.class)
	private LocalDate dateIncurred; 
	
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category", nullable = true)
	private Category category; 
	
//	@Column(name = "category")
//	private String category;

	@Column(name = "type")
	private String type;
	
	@Column(name = "subCategory")
	private String subCategory;
	
	@Column(name = "amount")
	private Double amount; 
	
	public void setAccount(Account account) {
		this.account = account;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setDateIncurred(LocalDate dateIncurred) {
		this.dateIncurred = dateIncurred;
	}
	public void setAmount(Double value) {
		this.amount = value;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	
	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @return the account
	 */
	public Account getAccount() {
		return account;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @return the dateIncurred
	 */
	public LocalDate getDateIncurred() {
		return dateIncurred;
	}
	public Category getCategory() {
		return category;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @return the subCategory
	 */
	public String getSubCategory() {
		return subCategory;
	}
	/**
	 * @return the value
	 */
	public Double getAmount() {
		return amount;
	}
	
}
