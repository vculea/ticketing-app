package ro.fruit.packeting;

import lombok.Builder;

@Builder(toBuilder = true)
public record BookingRequest(String event, String email, int tickets) {

}
