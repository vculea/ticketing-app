package ro.frit.ticheting.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor

public class Booking {
    private String id;
    private String eventId;
    private String customerEmail;
    private int seats;

}
