package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author Sami
 *
 */
@Entity
@Table (name="Currency")
public class Currency {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "name")
	private String name;
	
	@Column(name = "code")
	private String code;
	
	@Column(name = "symbol")
	private String symbol; 
	
	public String getName() {
		return name;
	}
	public String getCode() {
		return code;
	}
	public String getSymbol() {
		return symbol;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
