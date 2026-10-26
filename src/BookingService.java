import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class BookingService {

        private final Map<Long, MeetingRoom> rooms = new HashMap<>();
        private final Map<Long, Booking> bookings = new HashMap<>();


        public void addRoom(MeetingRoom room) {
            if (room == null) {
                throw new IllegalArgumentException("Room cannot be null");
            }

            if (rooms.containsKey(room.getRoomId())) {
                throw new IllegalArgumentException("Room ID already exists");
            }

            rooms.put(room.getRoomId(), room);
        }


        public void createBooking(Booking booking) {
            if (booking == null) {
                throw new IllegalArgumentException("Booking cannot be null");
            }

            if (bookings.containsKey(booking.getBookingId())) {
                throw new IllegalArgumentException("Booking ID already exists");
            }

            if (!rooms.containsKey(booking.getRoomId())) {
                throw new IllegalArgumentException("Room does not exist");
            }
            if (hasConflict(booking,-1)) {
                throw new IllegalArgumentException("Room is already booked during this time");
            }

            bookings.put(booking.getBookingId(), booking);
        }

        private boolean hasConflict(Booking newBooking,long excludeBookingId) {
            for (Booking existing : bookings.values()){
                if (existing.getBookingId() == excludeBookingId) {
                    continue;
                }
                if (existing.getRoomId()!=newBooking.getRoomId()) {continue;}

                if (existing.getStatus() == BookingStatus.CANCELLED) {continue;}

                if (newBooking.getStartTime().isBefore(existing.getEndTime())&&newBooking.getEndTime().isAfter(existing.getStartTime())) {

                    return true;
                }

            }
            return false ;
        }


        public Booking getBooking(long bookingId) {
            Booking booking = bookings.get(bookingId);

            if (booking == null) {
                throw new IllegalArgumentException("Booking not found");
            }

            return booking;
        }
    public void cancelBooking(long bookingId) {
        Booking booking = getBooking(bookingId);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }

        // Change the booking status to CANCELLED
        booking.setStatus(BookingStatus.CANCELLED);
    }
    public void rescheduleBooking(long bookingId, LocalDateTime newStartTime, LocalDateTime newEndTime) {
        Booking booking = getBooking(bookingId);
        if(booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot reschedule a cancelled booking");

        }
        if(newStartTime==null || newEndTime==null) {
            throw new IllegalArgumentException("Both startTime and endTime cannot be null");
        }
        if(!newStartTime.isBefore(newEndTime)) {
            throw new IllegalArgumentException("New start time cannot be after new end time");
        }
        if (!newStartTime.toLocalDate().equals(newEndTime.toLocalDate())) {
            throw new IllegalArgumentException("Booking must be on the same date");
        }
        long minutes = java.time.Duration.between(newStartTime, newEndTime).toMinutes();
        if(minutes<30||minutes>180)throw new IllegalArgumentException("Booking must be between 30 minutes and 180 minutes");

        java.time.LocalTime openingTime = java.time.LocalTime.of(9,0);
        java.time.LocalTime closingTime = java.time.LocalTime.of(18,0);
       if (newStartTime.toLocalTime().isBefore(openingTime) || newEndTime.toLocalTime().isAfter(closingTime)) {
           throw new IllegalArgumentException("Booking must be between 9am to 6pm");
       }

       Booking proposedBooking = new Booking(booking.getBookingId(),
               booking.getRoomId(),
               booking.getOrganizerName(),
               newStartTime,
               newEndTime,
               booking.getStatus());
       if (hasConflict(proposedBooking,bookingId)) {
           throw new IllegalArgumentException("overlaps with existing booking");
       }
       booking.setStartTime(newStartTime);
       booking.setEndTime(newEndTime);
    }
    public boolean isRoomAvailable(long roomId, LocalDateTime start, LocalDateTime end) {
        if (!rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Room does not exist");
        }

        Booking proposedBooking = new Booking(
                Long.MAX_VALUE, roomId, "Availability Check",
                start, end, BookingStatus.ACTIVE
        );

        return !hasConflict(proposedBooking, -1);
    }
    public List<Booking> getRoomSchedule(long roomId, LocalDate date) {

        if (!rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Room does not exist");
        }

        return bookings.values().stream()
                .filter(b -> b.getRoomId() == roomId)
                .filter(b -> b.getStatus() == BookingStatus.ACTIVE)
                .filter(b -> b.getStartTime().toLocalDate().equals(date))
                .sorted(
                        java.util.Comparator
                                .comparing(Booking::getStartTime)
                                .thenComparingLong(Booking::getBookingId)
                )
                .toList();
    }
    public Map<Long, Long> getActiveBookingCountPerRoom() {
        Map<Long, Long> result = new TreeMap<>();

        for (Long roomId : rooms.keySet()) {
            long count = bookings.values().stream()
                    .filter(b -> b.getRoomId() == roomId)
                    .filter(b -> b.getStatus() == BookingStatus.ACTIVE)
                    .count();

            result.put(roomId, count);
        }

        return result;
    }
    public Map<Long, Long> getBookedMinutesPerRoom() {
        Map<Long, Long> result = new TreeMap<>();

        for (Long roomId : rooms.keySet()) {
            long minutes = bookings.values().stream()
                    .filter(b -> b.getRoomId() == roomId)
                    .filter(b -> b.getStatus() == BookingStatus.ACTIVE)
                    .mapToLong(b -> java.time.Duration.between(
                            b.getStartTime(), b.getEndTime()).toMinutes())
                    .sum();

            result.put(roomId, minutes);
        }

        return result;
    }
    public MeetingRoom getBusiestRoom() {
        Map<Long, Long> minutesPerRoom = getBookedMinutesPerRoom();

        Long busiestRoomId = minutesPerRoom.entrySet().stream()
                .max(Map.Entry.<Long, Long>comparingByValue()
                        .thenComparing(e -> -e.getKey()))
                .map(Map.Entry::getKey)
                .orElseThrow(() ->
                        new IllegalStateException("No rooms available"));

        return rooms.get(busiestRoomId);
    }
    public Map<String, Long> getOrganizerUsage() {
        return bookings.values().stream()
                .filter(b -> b.getStatus() == BookingStatus.ACTIVE)
                .collect(Collectors.groupingBy(
                        Booking::getOrganizerName,
                        TreeMap::new,
                        Collectors.summingLong(b ->
                                java.time.Duration.between(
                                        b.getStartTime(),
                                        b.getEndTime()
                                ).toMinutes()
                        )
                ));
    }
    public long getDailyBookingCount(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        return bookings.values().stream()
                .filter(b -> b.getStatus() == BookingStatus.ACTIVE)
                .filter(b -> b.getStartTime().toLocalDate().equals(date))
                .count();
    }
}
