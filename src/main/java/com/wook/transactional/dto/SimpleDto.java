package com.wook.transactional.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Table(name = "simple_table")
@Entity(name = "SimpleDto")
@AllArgsConstructor
@NoArgsConstructor
public class SimpleDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer simpleData;
}
