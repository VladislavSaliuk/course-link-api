package com.courselink.api.entity;

import com.courselink.api.dto.DefenceSessionDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "defence_sessions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DefenceSession implements Serializable {

    @Id
    @Column(name = "defence_session_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "defence_session_id_generator")
    @SequenceGenerator(name = "defence_session_id_generator", initialValue = 1, allocationSize = 1, sequenceName = "defence_session_id_seq")
    private long defenceSessionId;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "defense_date", nullable = false)
    private LocalDate defenseDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_category_id", nullable = false)
    private TaskCategory taskCategory;

    public static DefenceSession toDefenceSession(DefenceSessionDTO defenceSessionDTO) {
        return DefenceSession.builder()
                .defenceSessionId(defenceSessionDTO.getDefenceSessionId())
                .description(defenceSessionDTO.getDescription())
                .defenseDate(defenceSessionDTO.getDefenseDate())
                .startTime(defenceSessionDTO.getStartTime())
                .endTime(defenceSessionDTO.getEndTime())
                .taskCategory(defenceSessionDTO.getTaskCategory())
                .build();
    }

}