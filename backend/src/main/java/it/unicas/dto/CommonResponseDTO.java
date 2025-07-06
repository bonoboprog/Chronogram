package it.unicas.dto;

/**
 * A generic Data Transfer Object (DTO) for standardized API responses.
 * It provides a common structure for success/failure status and a message.
 */
public class CommonResponseDTO {
    private boolean success;
    private String message;
    private Object data; // Can hold any specific data (e.g., ActivityDTO, List<ActivityDTO>)

    public CommonResponseDTO(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Constructor for responses without specific data
    public CommonResponseDTO(boolean success, String message) {
        this(success, message, null);
    }

    // Getters for JSON serialization
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}