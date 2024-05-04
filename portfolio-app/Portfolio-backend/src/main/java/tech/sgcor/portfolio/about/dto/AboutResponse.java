package tech.sgcor.portfolio.about.dto;

import java.util.Map;

public record AboutResponse(
        Long id,
        String name,
        String title,
        String address,
        String email,
        String dob,
        String phoneNumber,
        String summary,
        Map<String, String> socials
        ) {}
