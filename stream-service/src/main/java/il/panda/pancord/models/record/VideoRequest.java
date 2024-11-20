package il.panda.pancord.models.record;

import il.panda.pancord.validation.UUID;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

public record VideoRequest(@Parameter(in = ParameterIn.PATH)
                           @UUID(message = "invalid video id !")
                           String id) {
}
