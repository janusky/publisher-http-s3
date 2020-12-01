package app.auditoria;

import java.util.Date;

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
public class AuditVo {
	Long id;

	/**
	 * IP address from which the user performs the activity.
	 */
	private String ip;

	/**
	 * <p>
	 * Date and Time of the transaction in a DATE or STRING type field with format
	 * YYYY-MM-DD HH24:MI:SS
	 * </p>
	 * <p>
	 * <strong>Example</strong><blockquote>2016-01-27 15:23:15</blockquote>
	 * </p>
	 */
	private Date currentDate;

	private String idUser;

	private String idApp;

	/**
	 * Identifier of the transaction or operation. Preferably it should be an
	 * identifier with meaning in the domain of the application.
	 */
	private String idTransaction;

	/**
	 * <p>
	 * Description of the transaction. Name of the use or operation case of the web
	 * service.
	 * </p>
	 * <p>
	 * <strong>Example</strong><blockquote>"Consultation-debt",
	 * "Display-Communication", "Compensation-high" </blockquote>
	 * </p>
	 */
	private String descTransaction;

	/**
	 * <p>
	 * Additional data that provide greater precision to the logged activity.
	 * </p>
	 */
	private String dataOperation;
}
