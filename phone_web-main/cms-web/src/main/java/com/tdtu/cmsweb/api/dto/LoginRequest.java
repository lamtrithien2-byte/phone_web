package com.tdtu.cmsweb.api.dto;

public record LoginRequest(String username, String password, String role) {
}
