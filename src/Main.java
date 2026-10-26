import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Main {
    public static void main(String[] args) {

        BookingService service = new BookingService();

        MeetingRoom room1 = new MeetingRoom(1, "Atlas", 4);
        MeetingRoom room2 = new MeetingRoom(2, "Orion", 8);

        service.addRoom(room1);
        service.addRoom(room2);

        System.out.println("Meeting rooms added successfully!");
        LocalDate date = LocalDate.of(2026, 10, 26);

        service.createBooking(new Booking(
                101, 1, "Asha",
                LocalDateTime.of(date, LocalTime.of(10, 0)),
                LocalDateTime.of(date, LocalTime.of(11, 0)),
                BookingStatus.ACTIVE
        ));

        try {
            service.createBooking(new Booking(
                    102, 1, "Rahul",
                    LocalDateTime.of(date, LocalTime.of(10, 30)),
                    LocalDateTime.of(date, LocalTime.of(11, 30)),
                    BookingStatus.ACTIVE
            ));
        } catch (IllegalArgumentException e) {
            System.out.println("BK102 rejected: " + e.getMessage());
        }

        service.createBooking(new Booking(
                103, 1, "Priya",
                LocalDateTime.of(date, LocalTime.of(11, 0)),
                LocalDateTime.of(date, LocalTime.of(12, 0)),
                BookingStatus.ACTIVE
        ));

        service.createBooking(new Booking(
                104, 2, "Rahul",
                LocalDateTime.of(date, LocalTime.of(10, 30)),
                LocalDateTime.of(date, LocalTime.of(11, 30)),
                BookingStatus.ACTIVE
        ));

        service.createBooking(new Booking(
                105, 1, "Asha",
                LocalDateTime.of(date, LocalTime.of(9, 30)),
                LocalDateTime.of(date, LocalTime.of(10, 0)),
                BookingStatus.ACTIVE
        ));

        service.cancelBooking(101);

        service.createBooking(new Booking(
                106, 1, "Rahul",
                LocalDateTime.of(date, LocalTime.of(10, 0)),
                LocalDateTime.of(date, LocalTime.of(11, 0)),
                BookingStatus.ACTIVE
        ));

        try {
            service.rescheduleBooking(
                    103,
                    LocalDateTime.of(date, LocalTime.of(10, 30)),
                    LocalDateTime.of(date, LocalTime.of(11, 30))
            );
        } catch (IllegalArgumentException e) {
            System.out.println("BK103 reschedule rejected: " + e.getMessage());
        }

        System.out.println("\n--- BOOKINGS PER ROOM ---");
        System.out.println(service.getActiveBookingCountPerRoom());

        System.out.println("\n--- BOOKED MINUTES PER ROOM ---");
        System.out.println(service.getBookedMinutesPerRoom());

        System.out.println("\n--- BUSIEST ROOM ---");
        System.out.println(service.getBusiestRoom());

        System.out.println("\n--- ORGANIZER USAGE ---");
        System.out.println(service.getOrganizerUsage());

        System.out.println("\n--- DAILY BOOKINGS ---");
        System.out.println(service.getDailyBookingCount(date));


        System.out.println("\n--- FINAL BOOKING CHECKS ---");

        System.out.println("BK101 status: "
                + service.getBooking(101).getStatus());

        try {
            service.getBooking(102);
        } catch (IllegalArgumentException e) {
            System.out.println("BK102: Not stored");
        }

        Booking bk103 = service.getBooking(103);
        System.out.println("BK103 status: " + bk103.getStatus());
        System.out.println("BK103 time: "
                + bk103.getStartTime().toLocalTime()
                + " - "
                + bk103.getEndTime().toLocalTime());
    }


}