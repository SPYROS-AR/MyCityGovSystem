package gr.hua.dit.mycitygov.core.service.model;

public record CreateCitizenResult(
        boolean created,
        String reason,
        CitizenView citizenView
) {
    public static CreateCitizenResult success(final CitizenView citizenView) {
        return new CreateCitizenResult(true, null, citizenView);
    }

    public static CreateCitizenResult failure(final String reason) {
        if (reason == null) throw new NullPointerException("reason is null");
        if (reason.isBlank()) throw new IllegalArgumentException("reason is empty");
            return new CreateCitizenResult(false, reason, null);
    }
}
