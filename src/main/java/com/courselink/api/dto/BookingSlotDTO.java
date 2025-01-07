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

    @NotNull(message = "message.booking.slot.should.contains.start.time")
    private LocalTime startTime;

    @NotNull(message = "message.booking.slot.should.contains.end.time")
    private LocalTime endTime;

    private boolean isBooked;

    private Long userId;

    @NotNull(message = "message.booking.slot.should.contains.defence.session")
    private long defenceSessionId;

    public static BookingSlotDTO toBookingSlotDTO(BookingSlot bookingSlot) {
        return BookingSlotDTO.builder()
                .bookingSlotId(bookingSlot.getBookingSlotId())
                .startTime(bookingSlot.getStartTime())
                .endTime(bookingSlot.getEndTime())
                .userId(bookingSlot.getUser() != null ? bookingSlot.getUser().getUserId() : null)
                .isBooked(bookingSlot.isBooked())
                .defenceSessionId(bookingSlot.getDefenceSession().getDefenceSessionId())
                .build();
    }

}
