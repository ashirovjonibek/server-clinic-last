package uz.napa.clinic.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForbiddenException extends Throwable {
    private String message;

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
