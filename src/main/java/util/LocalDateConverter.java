/**
 * 
 */
package util;

import java.sql.Timestamp;
import java.time.LocalDate;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Converter to convert from java.time.LocalDate to java.sql.Date and back.
 *
 * @author Sami
 */
@Converter
public class LocalDateConverter implements AttributeConverter<LocalDate, Timestamp>  {

	@Override
	public Timestamp convertToDatabaseColumn(LocalDate arg0) {
		return Timestamp.valueOf(arg0.atStartOfDay()); 
	}

	@Override
	public LocalDate convertToEntityAttribute(Timestamp arg0) {
		return arg0.toLocalDateTime().toLocalDate();  
	}

}