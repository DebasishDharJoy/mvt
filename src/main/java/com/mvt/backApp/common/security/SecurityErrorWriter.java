package com.mvt.backApp.common.security;
import com.mvt.backApp.common.dto.ApiErrorResponse;
import com.mvt.backApp.common.service.MessageService;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
@Component @RequiredArgsConstructor
public class SecurityErrorWriter {
    private final ObjectMapper mapper; private final MessageService messages;
    public void write(HttpServletRequest req,HttpServletResponse res,HttpStatus status,
                      String code,String key)throws IOException {
        MessageService.ResolvedMessage m=messages.resolve(key);
        ApiErrorResponse body=ApiErrorResponse.builder().timestamp(Instant.now().toString())
                .status(status.value()).error(status.getReasonPhrase()).code(code).messageKey(key)
                .defaultMessage(m.defaultMessage()).localizedMessage(m.localizedMessage())
                .requestedLanguage(m.requestedLanguage()).path(req.getRequestURI()).build();
        res.setStatus(status.value());res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");mapper.writeValue(res.getOutputStream(),body);
    }
}
