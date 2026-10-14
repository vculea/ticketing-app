package ro.frit.ticketing.domain;

import lombok.Builder;
import lombok.NonNull;

@Builder(toBuilder = true)
public record Venue(@NonNull String name, @NonNull String city) {
}