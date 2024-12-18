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
    @PutMapping("/booking-slots/choose-booking-slot")
    @ResponseStatus(HttpStatus.OK)
    public BookingSlotDTO chooseBookingSlot(@RequestParam long userId, @RequestParam long bookingSlotId) {
        return bookingSlotService.chooseBookingSlot(userId, bookingSlotId);
    }
    @DeleteMapping("/booking-slots/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeByDefenceSessionId(@RequestParam long defenceSessionId) {
        bookingSlotService.removeBookingSlotByDefenceSessionId(defenceSessionId);
    }

    @GetMapping("/booking-slots")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingSlotDTO> getAllByDefenceSessionId(@RequestParam long defenceSessionId) {
        return bookingSlotService.getAllByDefenceSessionId(defenceSessionId);
    }

}
