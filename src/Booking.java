import java.time.LocalDateTime;

public class Booking {
    private long bookingId;
    private long roomId;
    private String organizerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;

    public Booking(long bookingId, long roomId, String organizerName, LocalDateTime startTime, LocalDateTime endTime, BookingStatus status) {
       if(bookingId<=0) throw new IllegalArgumentException("bookingId can't be null");
        this.bookingId = bookingId;
        if(roomId<=0) throw new IllegalArgumentException("roomId can't be null");
        this.roomId = roomId;
        if(organizerName==null || organizerName.isBlank()) throw new IllegalArgumentException("organizerName can't be null OR blank");
        this.organizerName = organizerName;


        if(startTime==null) throw new NullPointerException("startTime can't be null");
        if(endTime==null) throw new NullPointerException("endTime can't be null");
        if (!startTime.isBefore(endTime)) throw new IllegalArgumentException("startTime can't be after endTime");
        if(!startTime.toLocalDate().equals(endTime.toLocalDate())) throw new IllegalArgumentException("Booking must be on the same day");
        long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
        if(minutes<30||minutes>180)throw new IllegalArgumentException("Booking must be between 30 minutes and 180 minutes");

        java.time.LocalTime openingTime = java.time.LocalTime.of(9,0);
        java.time.LocalTime closingTime = java.time.LocalTime.of(18,0);
        if ( startTime.toLocalTime().isBefore(openingTime)||endTime.toLocalTime().isAfter(closingTime) )
            throw new  IllegalArgumentException("Booking must be between start and end time");



        this.startTime = startTime;
        this.endTime = endTime;
        if(status==null) throw new NullPointerException("BookingStatus can't be null");
        this.status = status;
    }

    public long getBookingId() {
        return bookingId;
    }

    public long getRoomId() {
        return roomId;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }


    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}
