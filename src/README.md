# Meeting Room Booking & Conflict Detection System

## Overview
A console-based Java application that manages meeting-room reservations, prevents scheduling conflicts, supports cancellation and rescheduling, and generates usage reports.

## Technology
- Java 17 or later (developed using Java 21)
- Collections Framework
- Stream API
- `java.time`
- Core Java OOP
- In-memory storage only
- No database, Spring Boot, web UI, or external libraries

## Features
- Add meeting rooms and create bookings.
- Validate booking IDs, room existence, date, time, working hours, and duration.
- Prevent overlapping active bookings in the same room.
- Allow back-to-back bookings.
- Search bookings by ID.
- Cancel bookings while retaining historical data.
- Reschedule bookings only when the new interval is valid.
- Display active room schedules in chronological order.
- Check room availability.
- Generate reports:
    - Active bookings per room
    - Total booked minutes per room
    - Busiest room
    - Active meeting minutes per organizer
    - Daily active booking count

## Data Structures
- `HashMap<Long, MeetingRoom>` stores rooms using room IDs as keys.
- `HashMap<Long, Booking>` stores bookings using booking IDs as keys and retains cancelled bookings.
- `TreeMap` provides predictable, sorted report output.
- Stream API operations calculate report totals from stored bookings.

## Booking Rules
1. A booking must reference an existing room and use a unique booking ID.
2. Start time must be earlier than end time.
3. Start and end must be on the same calendar date.
4. Bookings must fit entirely within 09:00–18:00.
5. Duration must be between 30 and 180 minutes, inclusive.
6. Active bookings in the same room must not overlap.
7. Back-to-back bookings are allowed.
8. Cancelled bookings remain stored but do not block availability or contribute to reports.
9. A failed reschedule must preserve the original booking times.
10. Invalid requests must produce meaningful errors.

## How to Run
1. Open the project in IntelliJ IDEA or another Java IDE.
2. Configure a Java 17 or later JDK.
3. Compile the Java source files.
4. Run `Main.java`.

From a terminal, assuming all source files are directly inside `src`:

```bash
javac -d out src/*.java
java -cp out Main
```

## Mandatory Sample Scenario
The application runs the assignment scenario for 26 October 2026.

Rooms:
- R1: Atlas, capacity 4
- R2: Orion, capacity 8

Operations:
- BK101: Asha, R1, 10:00–11:00 — accepted, then cancelled.
- BK102: Rahul, R1, 10:30–11:30 — rejected due to overlap.
- BK103: Priya, R1, 11:00–12:00 — accepted; conflicting reschedule rejected.
- BK104: Rahul, R2, 10:30–11:30 — accepted.
- BK105: Asha, R1, 09:30–10:00 — accepted.
- BK106: Rahul, R1, 10:00–11:00 — accepted after BK101 is cancelled.

## Expected Final Results
- Active bookings: BK103, BK104, BK105, BK106.
- R1 booked time: 150 minutes.
- R2 booked time: 60 minutes.
- Busiest room: R1 (Atlas).
- Organizer usage: Asha 30 minutes, Priya 60 minutes, Rahul 120 minutes.
- Daily active booking count on 26 October 2026: 4.
- BK101 remains stored with CANCELLED status.
- BK102 is not stored.
- BK103 remains ACTIVE at 11:00–12:00 after its rejected reschedule.

## Testing
See `TEST_CASES.md` for test inputs, expected results, and actual results.
