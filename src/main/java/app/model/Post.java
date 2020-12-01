package app.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Post implements Serializable {
	/**
	 * Serial default.
	 */
	private static final long serialVersionUID = -3100625559516522069L;

//	@Builder.Default
//	private java.time.LocalDateTime createdDate = LocalDateTime.now();

	/** Transaction identifier. */
	private String transaction;
	
	/** Customer transaction identifier. */
	private String transactionCustomer;

	@Builder.Default
	private String idCustomer = "0123456789";

	private Attach attach;
}