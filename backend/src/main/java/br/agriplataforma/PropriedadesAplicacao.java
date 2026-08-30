package br.agriplataforma;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "agriplataforma")
public record PropriedadesAplicacao(@NotNull UUID idTenantSemeado) {
}
