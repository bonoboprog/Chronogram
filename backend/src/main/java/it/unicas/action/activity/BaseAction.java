package it.unicas.action.activity;

import com.opensymphony.xwork2.ActionSupport;
import it.unicas.dto.CommonResponseDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base class for all Struts2 actions in the application.
 * Provides common logging and standardized response handling.
 */
public abstract class BaseAction extends ActionSupport {
    protected final Logger logger = LogManager.getLogger(getClass());
    protected CommonResponseDTO responseDTO;

    /**
     * Sets a success response with a message and optional data.
     * @param message The success message.
     * @param data Optional data to include in the response.
     */
    protected void setSuccess(String message, Object data) {
        this.responseDTO = new CommonResponseDTO(true, message, data);
    }

    /**
     * Sets a success response with only a message.
     * @param message The success message.
     */
    protected void setSuccess(String message) {
        this.responseDTO = new CommonResponseDTO(true, message);
    }

    /**
     * Sets a failure response with a message.
     * @param message The failure message.
     */
    protected void setFailure(String message) {
        this.responseDTO = new CommonResponseDTO(false, message);
    }

    /**
     * Getter for the response DTO, used by Struts2 JSON result type.
     * @return The CommonResponseDTO containing the action's result.
     */
    public CommonResponseDTO getResponseDTO() {
        return responseDTO;
    }
}
