package models;
import java.time.LocalDate;


/**
 * 
 * @author Sami Farhat
 *
 */
public class Card {
	
	private Long id; 
	private User cardholder;  
	private String cardNumber; 
	private LocalDate expiryDate; 
	private String securityCode; 

	
}
