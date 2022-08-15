package com.exossystem.task.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayLoadData {
    private String name;
    private String id;
    private String validated;
}
