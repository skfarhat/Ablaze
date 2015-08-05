/**
 * 
 */
package util;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Converter to convert from java.time.LocalDate to java.sql.Date and back.
 *
 * @author Sami
 */
@Converter
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

	@Override
	public Timestamp convertToDatabaseColumn(LocalDateTime arg0) {
		return Timestamp.valueOf(arg0); 
	}

	@Override
	public LocalDateTime convertToEntityAttribute(Timestamp arg0) {
		return arg0.toLocalDateTime(); 
	}

}