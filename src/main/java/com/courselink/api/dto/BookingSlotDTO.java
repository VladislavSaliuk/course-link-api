package com.courselink.api.dto;

import com.courselink.api.entity.BookingSlot;
import com.courselink.api.entity.DefenceSession;
import com.courselink.api.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;

@Data
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BookingSlotDTO {

    private long bookingSlotId;

    @NotNull(message = "Booking slot should contains start time!")
    private LocalTime startTime;

    @NotNull(message = "Booking slot should contains end time!")
    private LocalTime endTime;

    private boolean isBooked;

    private User user;

    @NotNull(message = "Defence session should contains end time!")
    private DefenceSession defenceSession;

    public static BookingSlotDTO toBookingSlotDTO(BookingSlot bookingSlot) {
        return BookingSlotDTO.builder()
                .bookingSlotId(bookingSlot.getBookingSlotId())
                .startTime(bookingSlot.getStartTime())
                .endTime(bookingSlot.getEndTime())
                .user(bookingSlot.getUser())
                .isBooked(bookingSlot.isBooked())
                .defenceSession(bookingSlot.getDefenceSession())
                .build();
    }

}
