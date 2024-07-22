package com.ssafy.meshroom.backend.domain.session.api;

import com.ssafy.meshroom.backend.domain.session.application.SessionService;
import com.ssafy.meshroom.backend.domain.session.dto.Response;
import com.ssafy.meshroom.backend.domain.session.dto.SessionCreateRequest;
import com.ssafy.meshroom.backend.domain.session.dto.SessionCreateResponse;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
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

    /*
    * 메인 세션 및 하위세션 정보 반환
    * */
    // @GetMapping("/{sessionId}")


}
