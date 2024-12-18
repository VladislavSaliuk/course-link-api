package com.courselink.api.repository;

import com.courselink.api.entity.BookingSlot;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingSlotRepository extends JpaRepository<BookingSlot, Long> {
    boolean existsByDefenceSession_DefenceSessionId(long defenceSessionId);
    @Transactional
    void deleteByDefenceSession_DefenceSessionId(long defenceSessionId);
    List<BookingSlot> findAllByDefenceSession_DefenceSessionId(long defenceSessionId);
}