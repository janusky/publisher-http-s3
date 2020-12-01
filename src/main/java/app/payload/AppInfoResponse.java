package app.payload;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * <p>
 * Application information.
 * </p>
 * 
 * @see InfoDetail
 * 
 * @author janusky@gmail.com
 * @version 1.0 - 16 sep. 2019
 *
 */
@Data
@Builder
public class AppInfoResponse {
	private String system;
	private String application;
	private String version;

	/**
	 * Environment in which the application is running. It can only contain the
	 * following values:
	 * 
	 * <pre>
	 * • DEVELOPMENT • TESTING • STAGING • PRODUCTION
	 * </pre>
	 */
	private String environment;

	/**
	 * General state of the application. It must contain the value of the most
	 * critical state of the submodules defined in details. It must contain one of
	 * the following values (in order from least critical to most critical):
	 * 
	 * <pre>
	 * • UP • DOWN
	 * </pre>
	 */
	private String status;

	/**
	 * Descriptive message that specifies the error occurred due to which the
	 * application is not operational ('UP'). It should only be specified in case
	 * the application is not in operational state.
	 */
	private String message;

	/**
	 * Details of the health status of the application. It can contain the details
	 * of: the application modules, the databases, the external applications with
	 * which it interacts, the environment in which it is running, etc.
	 */
	private List<InfoDetail> details;
}
