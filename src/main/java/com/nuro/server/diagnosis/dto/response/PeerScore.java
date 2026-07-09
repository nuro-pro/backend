package com.nuro.server.diagnosis.dto.response;

public record PeerScore(
        int moisture,
        int wrinkle,
        int pigment,
        int pore,
        int sensitive,
        int oil
) {
    public int total() {
    return (moisture + wrinkle + pigment + pore + sensitive + oil) / 6;
    }
}

