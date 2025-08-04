package avengers.lion.place.dto;

public record GeocodeInfo(
        String formattedAddress,
        double lat,
        double lng
) { }
