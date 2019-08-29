package java_dz_4;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "no such doctor")
public class DoctorNotFoundException extends RuntimeException {
}
