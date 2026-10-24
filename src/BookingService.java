import java.util.HashMap;
import java.util.Map;

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
            if (hasConflict(booking)) {
                throw new IllegalArgumentException("Room is already booked during this time");
            }

            bookings.put(booking.getBookingId(), booking);
        }

        private boolean hasConflict(Booking newBooking) {
            for (Booking existing : bookings.values()){
                if (existing.getRoomId()!=newBooking.getRoomId()) {continue;}

                if (existing.getStatus() == BookingStatus.CANCELLED) {continue;}

                if (existing.getStartTime().isBefore(existing.getEndTime())&& newBooking.getEndTime().isAfter(existing.getStartTime())) {

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
}
