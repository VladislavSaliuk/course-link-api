package com.courselink.api.controller;

import com.courselink.api.dto.BookingSlotDTO;
import com.courselink.api.service.BookingSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookingSlotsRestController {

    private final BookingSlotService bookingSlotService;
    @PostMapping("/booking-slots/generate-booking-slots")
    @ResponseStatus(HttpStatus.CREATED)
    public List<BookingSlotDTO> getGeneratedBookingSlots(@RequestParam long defenceSessionId, @RequestParam int bookingSlotsCount) {
        return bookingSlotService.generateBookingSlots(defenceSessionId, bookingSlotsCount);
    }


}
