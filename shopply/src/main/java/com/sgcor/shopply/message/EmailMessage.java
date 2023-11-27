package com.sgcor.shopply.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessage implements Serializable {
    private String to;
    private String Subject;
    private String body;
}
