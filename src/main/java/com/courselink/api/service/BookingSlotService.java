package com.courselink.api.service;

import com.courselink.api.dto.BookingSlotDTO;
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
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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

    private final MessageSource messageSource;

    public List<BookingSlotDTO> generateBookingSlots(long defenceSessionId, int bookingSlotsCount) {
        log.info("Generating booking slots for DefenceSession with ID: {}", defenceSessionId);

        if (bookingSlotsCount <= 0) {
            String errorMsg = messageSource.getMessage("message.illegal.booking.slot.count", null, LocaleContextHolder.getLocale());
            throw new IllegalArgumentException(errorMsg);
        }

        DefenceSession defenceSession = defenceSessionRepository.findById(defenceSessionId).orElseThrow(() -> {
           log.warn("Defence session with ID {} not found", defenceSessionId);
           String errorMsg = messageSource.getMessage("message.defence.session.not.found.with.id", new Object[]{defenceSessionId}, LocaleContextHolder.getLocale());
           throw new DefenceSessionNotFoundException(errorMsg);
        });

        if (bookingSlotRepository.existsByDefenceSession_DefenceSessionId(defenceSessionId)) {
            log.warn("Booking slots for DefenceSession with ID {} already exist!", defenceSessionId);
            String errorMsg = messageSource.getMessage("message.booking.slots.already.exist.with.defence.session.id", new Object[]{defenceSessionId}, LocaleContextHolder.getLocale());
            throw new DefenceSessionException(errorMsg);
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
                    String errorMsg = messageSource.getMessage("message.user.not.found.with.id", new Object[]{userId}, LocaleContextHolder.getLocale());
                    return new UserNotFoundException(errorMsg);
                });

        BookingSlot bookingSlot = bookingSlotRepository.findById(bookingSlotId)
                .orElseThrow(() -> {
                    log.warn("Booking slot with ID {} not found", bookingSlotId);
                    String errorMsg = messageSource.getMessage("message.booking.slot.not.found.with.id", new Object[]{bookingSlotId}, LocaleContextHolder.getLocale());
                    return new BookingSlotNotFoundException(errorMsg);
                });

        if(user.getRole() != Role.STUDENT && user.getRole() != Role.ADMIN_STUDENT) {
            log.warn("User with ID {} is not a student", userId);
            String errorMsg = messageSource.getMessage("message.user.not.student", new Object[]{userId}, LocaleContextHolder.getLocale());
            throw new BadCredentialsException(errorMsg);
        }

        if(bookingSlot.isBooked()) {
            log.warn("Booking slot with ID {} is already booked", bookingSlotId);
            String errorMsg = messageSource.getMessage("message.booking.slot.already.booked", new Object[]{bookingSlotId}, LocaleContextHolder.getLocale());
            throw new BadCredentialsException(errorMsg);
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
            String errorMsg = messageSource.getMessage("message.no.booking.slots.with.defence.session.id", new Object[]{defenceSessionId}, LocaleContextHolder.getLocale());
            throw new BookingSlotNotFoundException(errorMsg);
        }

        bookingSlotRepository.deleteByDefenceSession_DefenceSessionId(defenceSessionId);
        log.info("Removed booking slot with defence session ID: {}", defenceSessionId);

    }

    public List<BookingSlotDTO> getAllByDefenceSessionId(long defenceSessionId) {
        log.info("Fetching Booking slot with defence session ID: {}", defenceSessionId);

        List<BookingSlot> bookingSlots = bookingSlotRepository.findAllByDefenceSession_DefenceSessionId(defenceSessionId);

        if(bookingSlots.isEmpty()) {
            log.warn("Booking slots with defence session ID {} not found", defenceSessionId);
            String errorMsg = messageSource.getMessage("message.no.booking.slots.with.defence.session.id", new Object[]{defenceSessionId}, LocaleContextHolder.getLocale());
            throw new BookingSlotNotFoundException(errorMsg);
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
