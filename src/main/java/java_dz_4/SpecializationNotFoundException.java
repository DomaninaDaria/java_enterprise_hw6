package java_dz_4;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class SpecializationNotFoundException extends RuntimeException {
    public SpecializationNotFoundException(String message) {
        super("no such specialization, list of specializations: " + message);
    }
}
