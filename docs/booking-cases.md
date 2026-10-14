- POST (/events/{eventId}/bookings {})
  - add a booking to an event for a customer: 201 with booking details
  - no such event: 404
  - not enough seats: 409 with availableSeats
  - event already started: 409 with startsAt
  - invalid email (blank or malformed): 400
  - invalid seat number (zero or negative): 400
  - incomplete request payload (missing or null fields/body): 400
  - broken json: 400
  - wrong field type: 400
  - not json: 400 when declared as application/json, 415 for text/plain

Invalid requests must not call TicketOffice. These cases are covered by BookingControllerTest.