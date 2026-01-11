package gr.hua.dit.citySmsService.core.model;

/**
 * Domain object representing the result of an SMS transmission attempt
 *
 * @param status the status of the operation (OK/FAILED)
 */
public record SendSmsResult(String status) {}
