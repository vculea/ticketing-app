package ro.frit.ticheting.domain;

import lombok.Builder;

@Builder(toBuilder = true)
public record Venue(String name, String address) {
}
