package com.zalo.Spring_Zalo.DTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {
    private int id;
    private String name;
    private String banner;
    private LocalDate timeStartEvent;
    private LocalDate timeEndEvent;
    private int point;
    private boolean visible;
    private String description;
    private String company;
}


