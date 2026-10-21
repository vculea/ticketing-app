package ro.frit.ticketing.booking;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@Document(collection = "bookings")
@FieldNameConstants
public class Booking {
    @Id
    private String id;

    @NotBlank
    private String eventId;

    @NotBlank
    @Email
    private String customerEmail;

    @Positive
    private int seats;

    @NotNull
    private LocalDateTime bookedAt;
}
