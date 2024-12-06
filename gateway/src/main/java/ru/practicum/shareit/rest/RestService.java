package ru.practicum.shareit.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
public abstract class RestService {
    private final RestTemplate restTemplate;

    public ResponseEntity<Object> executeQuery(RestQueryDto restQueryDto) {
        log.trace("try exchange " + restQueryDto);

        var headers = getHeaders(restQueryDto.getRequestHead());

        HttpEntity<Object> restQueryEntity = restQueryDto.getBody()
                .map(i -> new HttpEntity<>(i, headers))
                .orElse(new HttpEntity<>(headers));

        try {
            var response = restTemplate.exchange(
                    restQueryDto.getPath(),
                    restQueryDto.getHttpMethod(),
                    restQueryEntity,
                    Object.class);

            return prepareGatewayResponse(response);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
    }

    private HttpHeaders getHeaders(Optional<RestQueryHead> head) {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        head.ifPresent(i -> headers.set(i.name(), i.value()));

        return headers;
    }

    private ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
