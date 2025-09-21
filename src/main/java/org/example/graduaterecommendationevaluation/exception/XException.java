package org.example.graduaterecommendationevaluation.exception;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class XException extends RuntimeException{
    private Code code;
    private int number;
    private String message;
}
