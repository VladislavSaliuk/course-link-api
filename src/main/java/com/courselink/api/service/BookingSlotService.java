package com.courselink.api.service;

import com.courselink.api.dto.BookingSlotDTO;
import com.courselink.api.dto.DefenceSessionDTO;
import com.courselink.api.entity.BookingSlot;
import com.courselink.api.entity.DefenceSession;
import com.courselink.api.entity.Role;
import com.courselink.api.entity.User;
import com.courselink.api.exception.BookingSlotNotFoundException;
import com.courselink.api.exception.DefenceSessionException;
import com.courselink.api.exception.DefenceSessionNotFoundException;
import com.courselink.api.exception.UserNotFoundException;
import com.courselink.api.repository.BookingSlotRepository;
import com.courselink.api.repository.DefenceSessionRepository;
import com.courselink.api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
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

    private final UserRepository userRepository;

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


    @Transactional
    public BookingSlotDTO chooseBookingSlot(long userId, long bookingSlotId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User with ID {} not found", userId);
                    return new UserNotFoundException("User with " + userId + " Id doesn't exist!");
                });

        BookingSlot bookingSlot = bookingSlotRepository.findById(bookingSlotId)
                .orElseThrow(() -> {
                    log.warn("Booking slot with ID {} not found", userId);
                    return new BookingSlotNotFoundException("Booking slot with " + userId + " Id doesn't exist!");
                });

        if(user.getRole() != Role.STUDENT && user.getRole() != Role.ADMIN_STUDENT) {
            log.warn("User with ID {} is not a student", userId);
            throw new BadCredentialsException("User with " + userId + " Id is not a student!");
        }

        if(bookingSlot.isBooked()) {
            log.warn("Booking slot with ID {} is already booked", userId);
            throw new BadCredentialsException("Booking slot with " + userId + " Id is already booked!");
        }

        bookingSlot.setUser(user);
        bookingSlot.setBooked(true);

        log.info("User with ID {} successfully booked on booking slot with ID {}", userId, bookingSlot);

        return BookingSlotDTO.toBookingSlotDTO(bookingSlot);
    }

    public void removeBookingSlotByDefenceSessionId(long defenceSessionId) {

        log.info("Removing booking slot with defence session ID: {}", defenceSessionId);

        if (!bookingSlotRepository.existsByDefenceSession_DefenceSessionId(defenceSessionId)) {
            log.warn("Booking slot with defence session ID {} not found", defenceSessionId);
            throw new BookingSlotNotFoundException("Booking slot with defence session " + defenceSessionId + " Id doesn't exist!");
        }

        bookingSlotRepository.deleteByDefenceSession_DefenceSessionId(defenceSessionId);
        log.info("Removed booking slot with defence session ID: {}", defenceSessionId);

    }

    public List<BookingSlotDTO> getAllByDefenceSessionId(long defenceSessionId) {
        log.info("Fetching Booking slot with defence session ID: {}", defenceSessionId);

        List<BookingSlot> bookingSlots = bookingSlotRepository.findAllByDefenceSession_DefenceSessionId(defenceSessionId);

        if(bookingSlots.isEmpty()) {
            log.warn("Booking slots with defence session ID {} not found", defenceSessionId);
            throw new BookingSlotNotFoundException("Booking slots with " + defenceSessionId + " defence session Id doesn't exist!");
        }

        log.info("Found booking slots with defence session ID: {}", defenceSessionId);

        return bookingSlots.stream()
                .map(bookingSlot -> BookingSlotDTO.toBookingSlotDTO(bookingSlot))
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
