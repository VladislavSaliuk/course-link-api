package com.courselink.api.service;

import com.courselink.api.dto.BookingSlotDTO;
import com.courselink.api.dto.DefenceSessionDTO;
import com.courselink.api.entity.BookingSlot;
import com.courselink.api.entity.DefenceSession;
import com.courselink.api.exception.DefenceSessionException;
import com.courselink.api.exception.DefenceSessionNotFoundException;
import com.courselink.api.repository.DefenceSessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefenceSessionService {

    private final DefenceSessionRepository defenceSessionRepository;


    public DefenceSessionDTO createDefenceSession(DefenceSessionDTO defenceSessionDTO) {
        log.info("Creating DefenceSession: {}", defenceSessionDTO);

        LocalTime startTime = defenceSessionDTO.getStartTime();
        LocalTime endTime = defenceSessionDTO.getEndTime();

        if (startTime.compareTo(endTime) > 0) {
            log.error("Start time {} is greater than end time {}", startTime, endTime);
            throw new DefenceSessionException("Start time can not be greater than end time!");
        }

        DefenceSession defenceSession = defenceSessionRepository.save(DefenceSession.toDefenceSession(defenceSessionDTO));
        log.info("Created DefenceSession with ID: {}", defenceSession.getDefenceSessionId());

        return DefenceSessionDTO.toDefenceSessionDTO(defenceSession);
    }

    @Transactional
    public DefenceSessionDTO updateDefenceSession(DefenceSessionDTO defenceSessionDTO) {
        log.info("Updating DefenceSession with ID: {}", defenceSessionDTO.getDefenceSessionId());

        LocalTime startTime = defenceSessionDTO.getStartTime();
        LocalTime endTime = defenceSessionDTO.getEndTime();

        DefenceSession updatedDefenceSession = defenceSessionRepository.findById(defenceSessionDTO.getDefenceSessionId())
                .orElseThrow(() -> new DefenceSessionNotFoundException("Defence session with " + defenceSessionDTO.getDefenceSessionId() + " Id doesn't exist!"));

        if (startTime.compareTo(endTime) > 0) {
            log.error("Start time {} is greater than end time {}", startTime, endTime);
            throw new DefenceSessionException("Start time can not be greater than end time!");
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
                    return new DefenceSessionNotFoundException("Defence session with " + defenceSessionId + " Id doesn't exist!");
                });

        log.info("Found DefenceSession with ID: {}", session.getDefenceSessionId());
        return session;
    }

    public void removeById(long defenceSessionId) {
        log.info("Removing DefenceSession with ID: {}", defenceSessionId);

        if (!defenceSessionRepository.existsById(defenceSessionId)) {
            log.warn("Defence session with ID {} not found", defenceSessionId);
            throw new DefenceSessionNotFoundException("Defence session with " + defenceSessionId + " Id doesn't exist!");
        }

        defenceSessionRepository.deleteById(defenceSessionId);
        log.info("Removed DefenceSession with ID: {}", defenceSessionId);
    }



}