package com.courselink.api.service;

import com.courselink.api.dto.DefenceSessionDTO;
import com.courselink.api.entity.DefenceSession;
import com.courselink.api.exception.DefenceSessionException;
import com.courselink.api.exception.DefenceSessionNotFoundException;
import com.courselink.api.repository.DefenceSessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefenceSessionService {

    private final DefenceSessionRepository defenceSessionRepository;

    private final MessageSource messageSource;

    public DefenceSessionDTO createDefenceSession(DefenceSessionDTO defenceSessionDTO) {
        log.info("Creating DefenceSession: {}", defenceSessionDTO);

        LocalDate defenceDate = defenceSessionDTO.getDefenseDate();
        LocalTime startTime = defenceSessionDTO.getStartTime();
        LocalTime endTime = defenceSessionDTO.getEndTime();

        List<DefenceSessionDTO> defenceSessionDTOList = getAll();

        if (startTime.compareTo(endTime) > 0) {
            log.error("Start time {} is greater than end time {}", startTime, endTime);
            String errorMsg = messageSource.getMessage("message.defence.session.start.time.greater.end.time", null, LocaleContextHolder.getLocale());
            throw new DefenceSessionException(errorMsg);
        }

        for (DefenceSessionDTO currentDefenceSessionDTO : defenceSessionDTOList) {
            if (defenceDate.compareTo(currentDefenceSessionDTO.getDefenseDate()) == 0) {
                if ((startTime.isBefore(currentDefenceSessionDTO.getEndTime()) && endTime.isAfter(currentDefenceSessionDTO.getStartTime()))) {
                    log.error("Time conflict detected: new session overlaps with an existing session on {}", defenceDate);
                    String errorMsg = messageSource.getMessage("message.defence.session.time.conflict", null, LocaleContextHolder.getLocale());
                    throw new DefenceSessionException(errorMsg);
                }
            }
        }

        DefenceSession defenceSession = defenceSessionRepository.save(DefenceSession.toDefenceSession(defenceSessionDTO));
        log.info("Created DefenceSession with ID: {}", defenceSession.getDefenceSessionId());

        return DefenceSessionDTO.toDefenceSessionDTO(defenceSession);
    }

    @Transactional
    public DefenceSessionDTO updateDefenceSession(DefenceSessionDTO defenceSessionDTO) {
        log.info("Updating DefenceSession with ID: {}", defenceSessionDTO.getDefenceSessionId());

        LocalDate defenceDate = defenceSessionDTO.getDefenseDate();
        LocalTime startTime = defenceSessionDTO.getStartTime();
        LocalTime endTime = defenceSessionDTO.getEndTime();

        List<DefenceSessionDTO> defenceSessionDTOList = getAll();

        DefenceSession updatedDefenceSession = defenceSessionRepository.findById(defenceSessionDTO.getDefenceSessionId())
            .orElseThrow(() -> {
            log.warn("Defence session with ID {} not found", defenceSessionDTO.getDefenceSessionId());
            String errorMsg = messageSource.getMessage("message.defence.session.not.found.with.id", new Object[]{defenceSessionDTO.getDefenceSessionId()}, LocaleContextHolder.getLocale());
            return new DefenceSessionNotFoundException(errorMsg);
        });

        if (startTime.compareTo(endTime) > 0) {
            log.error("Start time {} is greater than end time {}", startTime, endTime);
            String errorMsg = messageSource.getMessage("message.defence.session.start.time.greater.end.time", null, LocaleContextHolder.getLocale());
            throw new DefenceSessionException(errorMsg);
        }

        for (DefenceSessionDTO currentDefenceSessionDTO : defenceSessionDTOList) {
            if (defenceDate.compareTo(currentDefenceSessionDTO.getDefenseDate()) == 0) {
                if ((startTime.isBefore(currentDefenceSessionDTO.getEndTime()) && endTime.isAfter(currentDefenceSessionDTO.getStartTime()))) {
                    log.error("Time conflict detected: new session overlaps with an existing session on {}", defenceDate);
                    String errorMsg = messageSource.getMessage("message.defence.session.time.conflict", null, LocaleContextHolder.getLocale());
                    throw new DefenceSessionException(errorMsg);
                }
            }
        }

        updatedDefenceSession.setDescription(defenceSessionDTO.getDescription());
        updatedDefenceSession.setStartTime(defenceSessionDTO.getStartTime());
        updatedDefenceSession.setEndTime(defenceSessionDTO.getEndTime());
        updatedDefenceSession.setTaskCategory(defenceSessionDTO.getTaskCategory());

        log.info("Updated DefenceSession with ID: {}", updatedDefenceSession.getDefenceSessionId());

        return DefenceSessionDTO.toDefenceSessionDTO(updatedDefenceSession);
    }

    public List<DefenceSessionDTO> getAll() {
        log.info("Fetching all DefenceSessions");

        List<DefenceSessionDTO> defenceSessions = defenceSessionRepository.findAll()
                .stream().map(defenceSession -> DefenceSessionDTO.toDefenceSessionDTO(defenceSession))
                .collect(Collectors.toList());

        log.info("Fetched {} DefenceSessions", defenceSessions.size());
        return defenceSessions;
    }

    public DefenceSessionDTO getById(long defenceSessionId) {
        log.info("Fetching DefenceSession with ID: {}", defenceSessionId);

        DefenceSessionDTO session = defenceSessionRepository.findById(defenceSessionId)
                .map(defenceSession -> DefenceSessionDTO.toDefenceSessionDTO(defenceSession))
                .orElseThrow(() -> {
                    log.warn("Defence session with ID {} not found", defenceSessionId);
                    String errorMsg = messageSource.getMessage("message.defence.session.not.found.with.id", new Object[]{defenceSessionId}, LocaleContextHolder.getLocale());
                    return new DefenceSessionNotFoundException(errorMsg);
                });

        log.info("Found DefenceSession with ID: {}", session.getDefenceSessionId());
        return session;
    }

    public void removeById(long defenceSessionId) {
        log.info("Removing DefenceSession with ID: {}", defenceSessionId);

        if (!defenceSessionRepository.existsById(defenceSessionId)) {
            log.warn("Defence session with ID {} not found", defenceSessionId);
            String errorMsg = messageSource.getMessage("message.defence.session.not.found.with.id", new Object[]{defenceSessionId}, LocaleContextHolder.getLocale());
            throw new DefenceSessionNotFoundException(errorMsg);
        }

        defenceSessionRepository.deleteById(defenceSessionId);
        log.info("Removed DefenceSession with ID: {}", defenceSessionId);
    }


}