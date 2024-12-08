package com.courselink.api.dto;

import com.courselink.api.entity.BookingSlot;
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

    public static BookingSlotDTO bookingSlotDTO(BookingSlot bookingSlot) {
        return BookingSlotDTO.builder()
                .bookingSlotId(bookingSlot.getBookingSlotId())
                .startTime(bookingSlot.getStartTime())
                .endTime(bookingSlot.getEndTime())
                .isBooked(bookingSlot.isBooked())
                .build();
    }

}
