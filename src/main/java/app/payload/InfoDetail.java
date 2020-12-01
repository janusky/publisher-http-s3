package app.payload;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

/**
 * <p>
 * Detalles de información.
 * </p>
 * 
 * @author janusky@gmail.com
 * @version 1.0 - 16 sep. 2019 15:45:43
 *
 */
@Data
@Builder
public class InfoDetail {
	private String name;
	private String status;
	private Map<String, Object> details;
}
