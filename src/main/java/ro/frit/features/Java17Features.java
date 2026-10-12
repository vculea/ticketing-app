package ro.frit.features;

import ro.frit.ticheting.domain.Event;
import ro.frit.ticheting.domain.Venue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Java17Features {
    static void main() {
        switchExpression();
    }

    static List<Event> events() {
        return Arrays.asList(
                new Event("1", "Rock Night", new Venue("Stadium A", "Cluj"), LocalDateTime.of(2024, 6, 15, 20, 0), 1000, 500),
                new Event("2", "Jazz Evening", new Venue("Stadium B", "Bucuresti"), LocalDateTime.of(2024, 7, 20, 19, 30), 1500, 1200),
                new Event("3", "La Travita", new Venue("Stadium C", "Iasi"), LocalDateTime.of(2024, 8, 10, 18, 0), 800, 600),
                new Event("4", "Concert D", new Venue("Stadium D", "Oradea"), LocalDateTime.of(2024, 9, 5, 21, 0), 2000, 1800)
        );
    }

    private static void switchExpression() {
        System.out.println("Try with resources");
        String day = "Monday";
        int newDay = switch (day) {
            case "Monday" -> 1;
            case "Tuesday" -> 2;
            case "Wednesday" -> 3;
            case "Thursday" -> 4;
            case "Friday" -> 5;
            case "Saturday" -> 6;
            case "Sunday" -> 7;
            default -> -1;
        };
        System.out.println(newDay);
    }
}
