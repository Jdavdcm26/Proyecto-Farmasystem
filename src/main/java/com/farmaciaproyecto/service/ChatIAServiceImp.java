package com.farmaciaproyecto.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmaciaproyecto.dto.response.ConsultaIAResponseDTO;
import com.farmaciaproyecto.dto.response.ProductoResponseDTO;
import com.farmaciaproyecto.model.ConsultaIA;
import com.farmaciaproyecto.model.User;
import com.farmaciaproyecto.repository.ConsultaIARepository;
import com.farmaciaproyecto.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatIAServiceImp implements ChatIAService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final ConsultaIARepository consultaIARepository;
    private final UserRepository userRepository;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String consultar(String sintomas, List<ProductoResponseDTO> productos) {
        String prompt = buildPrompt(sintomas, productos);
        String requestBody = buildRequestBody(prompt);

        log.debug("Llamando a Gemini API: {}", apiUrl);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "?key=" + apiKey))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .timeout(Duration.ofSeconds(45))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            log.debug("Gemini status: {}", response.statusCode());

            if (response.statusCode() != 200) {
                String detail = extractGeminiError(response.body());
                log.error("Gemini API error {} - {}", response.statusCode(), response.body());
                throw new RuntimeException("Error de la IA (" + response.statusCode() + "): " + detail);
            }

            return extractText(response.body());

        } catch (IOException | InterruptedException e) {
            log.error("Error de conexion con Gemini API", e);
            throw new RuntimeException("No se pudo conectar con la IA. Verifica tu conexion a internet.");
        }
    }

    @Override
    public void guardarConsulta(Long userId, String sintomas, String respuesta) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        ConsultaIA consulta = ConsultaIA.builder()
                .sintomas(sintomas)
                .respuesta(respuesta)
                .fecha(LocalDateTime.now())
                .user(user)
                .build();
        consultaIARepository.save(consulta);
    }

    @Override
    public List<ConsultaIAResponseDTO> obtenerHistorial(Long userId) {
        return consultaIARepository.findByUserIdOrderByFechaDesc(userId)
                .stream()
                .map(c -> ConsultaIAResponseDTO.builder()
                        .id(c.getId())
                        .sintomas(c.getSintomas())
                        .respuesta(c.getRespuesta())
                        .fecha(c.getFecha())
                        .build())
                .toList();
    }

    @Override
    public void eliminarConsulta(Long id, Long userId) {
        consultaIARepository.findById(id)
                .filter(c -> c.getUser().getId().equals(userId))
                .ifPresentOrElse(
                        consultaIARepository::delete,
                        () -> { throw new RuntimeException("Consulta no encontrada"); });
    }

    // ── Construcción del prompt ──────────────────────────────────────────────
    private String buildPrompt(String sintomas, List<ProductoResponseDTO> productos) {
        StringBuilder sb = new StringBuilder();
        sb.append("Eres un asistente farmacéutico de FarmaSystem. Tu trabajo es ayudar a los clientes ");
        sb.append("a encontrar productos disponibles en la farmacia según sus síntomas o necesidades.\n\n");
        sb.append("INSTRUCCIONES:\n");
        sb.append("1. Recomienda productos de nuestro catálogo que puedan ayudar al cliente.\n");
        sb.append("2. Si el producto ideal NO está en el catálogo, dilo claramente y ofrece la alternativa más cercana que sí tenemos.\n");
        sb.append("3. Si el stock de un producto es 0, advierte al cliente que no está disponible.\n");
        sb.append("4. No diagnostiques enfermedades; solo recomienda productos.\n");
        sb.append("5. Responde siempre en español, de forma concisa y profesional.\n\n");

        if (productos.isEmpty()) {
            sb.append("CATÁLOGO: La farmacia no tiene productos disponibles en este momento.\n\n");
        } else {
            sb.append("CATÁLOGO DE PRODUCTOS DISPONIBLES EN FARMACARE:\n");
            for (ProductoResponseDTO p : productos) {
                sb.append("- ").append(p.getNombre());
                if (p.getDescripcion() != null && !p.getDescripcion().isBlank()) {
                    sb.append(" (").append(p.getDescripcion()).append(")");
                }
                if (p.getCategoriaNombre() != null) {
                    sb.append(" | Categoría: ").append(p.getCategoriaNombre());
                }
                sb.append(" | Precio: $").append(p.getPrecio());
                sb.append(" | Stock: ").append(p.getStock()).append(" uds");
                sb.append("\n");
            }
            sb.append("\n");
        }

        sb.append("CONSULTA DEL CLIENTE:\n").append(sintomas);
        return sb.toString();
    }

    // ── Serialización JSON segura con Jackson ────────────────────────────────
    private String buildRequestBody(String prompt) {
        try {
            Map<String, Object> body = Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(
                        Map.of("text", prompt)
                    ))
                )
            );
            return mapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new RuntimeException("Error construyendo el cuerpo de la petición IA", e);
        }
    }

    // ── Extracción del texto de respuesta ────────────────────────────────────
    private String extractText(String responseBody) {
        try {
            JsonNode root = mapper.readTree(responseBody);
            JsonNode text = root
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text");

            if (text.isMissingNode() || text.isNull()) {
                log.error("Respuesta inesperada de Gemini: {}", responseBody);
                throw new RuntimeException("La IA no devolvió una respuesta válida.");
            }
            return text.asText();
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            log.error("Error parseando respuesta de Gemini: {}", responseBody, e);
            throw new RuntimeException("Error procesando la respuesta de la IA.");
        }
    }

    // ── Extrae el mensaje de error de Gemini ─────────────────────────────────
    private String extractGeminiError(String body) {
        try {
            JsonNode root = mapper.readTree(body);
            JsonNode msg = root.path("error").path("message");
            return msg.isMissingNode() ? "sin detalle" : msg.asText();
        } catch (Exception e) {
            return "sin detalle";
        }
    }
}
