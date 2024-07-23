package com.ssafy.meshroom.backend.domain.session.api;

import com.ssafy.meshroom.backend.domain.session.application.SessionService;
import com.ssafy.meshroom.backend.domain.session.dto.*;
import com.ssafy.meshroom.backend.domain.user.domain.User;
import com.ssafy.meshroom.backend.global.common.dto.Response;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class SessionController {

    private final SessionService sessionService;

    /*
    * 세션 생성
    * */
    @PostMapping
    public ResponseEntity<Response<SessionCreateResponse>> createSessions(@RequestBody SessionCreateRequest request)
            throws OpenViduJavaClientException, OpenViduHttpException {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.createSession(request.getContentsOrder()));
    }

    /*
    * 하위 세션 생성
    * */
    @PostMapping("/{sessionId}")
    public ResponseEntity<Response<SubSessionCreateResponse>> createSubSessions(@PathVariable String sessionId)
            throws OpenViduJavaClientException, OpenViduHttpException {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.createSubSession(sessionId));
    }

    /*
    * 메인 세션 및 하위세션 정보 반환
    * */
     @GetMapping("/{sessionId}")
     public String test(@PathVariable String sessionId){
//         log.info(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
         return sessionId;
     }

    /*
    * 세션에 대한 커넥션 생성
    * */
    @PostMapping("/{sessionId}/connections")
    public ResponseEntity<Response<ConnectionCreateResponse>> createConnections(@PathVariable String sessionId, @RequestBody ConnectionCreateRequest req, HttpServletResponse response)
            throws OpenViduJavaClientException, OpenViduHttpException {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.createConnection(req.getUserName(), sessionId, response));
    }


}
