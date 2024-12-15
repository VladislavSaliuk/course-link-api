package com.courselink.api.repository;

import com.courselink.api.entity.BookingSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingSlotRepository extends JpaRepository<BookingSlot, Long> {

    boolean existsByDefenceSession_DefenceSessionId(long defenceSessionId);

}