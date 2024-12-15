package com.courselink.api.service;

import com.courselink.api.dto.BookingSlotDTO;
import com.courselink.api.entity.BookingSlot;
import com.courselink.api.entity.DefenceSession;
import com.courselink.api.exception.DefenceSessionException;
import com.courselink.api.exception.DefenceSessionNotFoundException;
import com.courselink.api.repository.BookingSlotRepository;
import com.courselink.api.repository.DefenceSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingSlotService {

    private final BookingSlotRepository bookingSlotRepository;
    private final DefenceSessionRepository defenceSessionRepository;

    public List<BookingSlotDTO> generateBookingSlots(long defenceSessionId, int bookingSlotsCount) {
        log.info("Generating booking slots for DefenceSession with ID: {}", defenceSessionId);

        if (bookingSlotsCount <= 0) {
            throw new IllegalArgumentException("Booking slots count must be greater than 0!");
        }

        DefenceSession defenceSession = defenceSessionRepository.findById(defenceSessionId).orElseThrow(() -> {
           log.warn("Defence session with ID {} not found", defenceSessionId);
           throw new DefenceSessionNotFoundException("Defence session with ID " + defenceSessionId + " doesn't exist!");
        });

        if (bookingSlotRepository.existsByDefenceSession_DefenceSessionId(defenceSessionId)) {
            log.warn("Booking slots for DefenceSession with ID {} already exist!", defenceSessionId);
            throw new DefenceSessionException("Booking slots for DefenceSession with ID " + defenceSessionId + " already exist!");
        }

        List<BookingSlot> bookingSlots = createBookingSlots(defenceSession, bookingSlotsCount);
        bookingSlotRepository.saveAll(bookingSlots);

        log.info("Generated {} booking slots.", bookingSlots.size());
        return bookingSlots.stream()
                .map(BookingSlotDTO::toBookingSlotDTO)
                .collect(Collectors.toList());
    }

    private List<BookingSlot> createBookingSlots(DefenceSession defenceSession, int bookingSlotsCount) {
        LocalTime startTime = defenceSession.getStartTime();
        LocalTime endTime = defenceSession.getEndTime();

        log.info("Start time: {}, End time: {}", startTime, endTime);

        Duration sessionDuration = Duration.between(startTime, endTime);
        long slotDurationInNanos = sessionDuration.toNanos() / bookingSlotsCount;

        log.info("Slot duration: {} minutes", Duration.ofNanos(slotDurationInNanos).toMinutes());

        return IntStream.range(0, bookingSlotsCount)
                .mapToObj(i -> {
                    LocalTime slotStartTime = startTime.plusNanos(slotDurationInNanos * i);
                    LocalTime slotEndTime = slotStartTime.plusNanos(slotDurationInNanos);

                    BookingSlot bookingSlot = new BookingSlot();
                    bookingSlot.setStartTime(slotStartTime);
                    bookingSlot.setEndTime(slotEndTime);
                    bookingSlot.setBooked(false);
                    bookingSlot.setDefenceSession(defenceSession);

                    return bookingSlot;
                })
                .collect(Collectors.toList());
    }
}
