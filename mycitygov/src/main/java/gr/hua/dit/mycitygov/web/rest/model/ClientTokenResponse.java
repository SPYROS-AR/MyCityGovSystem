package gr.hua.dit.mycitygov.web.rest.model;

public record ClientTokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {}
