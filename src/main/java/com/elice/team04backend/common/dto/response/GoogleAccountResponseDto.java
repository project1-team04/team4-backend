package com.elice.team04backend.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleAccountResponseDto {
    private final String id;

    private final String email;

    @JsonProperty("verified_email")
    private final Boolean verifiedEmail;

    private final String name;

    @JsonProperty("given_name")
    private final String givenName;

    @JsonProperty("family_name")
    private final String familyName;

    private final String picture;
}

