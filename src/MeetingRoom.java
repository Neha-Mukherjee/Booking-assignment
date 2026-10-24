import java.util.Objects;

public class MeetingRoom {
    private long roomId;
    private String roomName;
    private long capacity;

    public MeetingRoom(long roomId, String roomName, long capacity) {
        if(roomId<=0) throw new NullPointerException("roomId can't be null");
        this.roomId = roomId;
        this.roomName = Objects.requireNonNull(roomName,"Room name cannot be null");
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }
        this.capacity = capacity;
    }

    public long getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public long getCapacity() {
        return capacity;
    }

    public String toString(){
        return roomId + "\t" + roomName + "\t" + capacity;
    }
}

