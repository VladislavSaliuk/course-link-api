package com.courselink.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "booking_slots")
public class BookingSlot implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_slot_id_generator")
    @SequenceGenerator(name = "booking_slot_id_generator", initialValue = 1, allocationSize = 1, sequenceName = "booking_slot_id_seq")
    @Column(name = "booking_slot_id", nullable = false)
    private long bookingSlotId;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "is_booked", nullable = false)
    private boolean isBooked;

    @JoinColumn(name = "defence_session_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private DefenceSession defenceSession;

}