package com.ssafy.meshroom.backend.domain.contents.api;

import com.ssafy.meshroom.backend.domain.contents.application.ContentsOrderService;
import com.ssafy.meshroom.backend.domain.contents.application.ContentsService;
import com.ssafy.meshroom.backend.domain.contents.dto.ContentsListResponse;
import com.ssafy.meshroom.backend.domain.contents.dto.ContentsOrderSubscribe;
import com.ssafy.meshroom.backend.global.common.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contents")
@RequiredArgsConstructor
@Slf4j
public class ContentsController {

    private final ContentsService contentsService;
    private final ContentsOrderService contentsOrderService;

    @GetMapping
    public ResponseEntity<Response<ContentsListResponse>> getContents(){
        return ResponseEntity.status(HttpStatus.OK).body(contentsService.getContents());
    }

    @GetMapping("/next")
    public ResponseEntity<Response<ContentsOrderSubscribe>> nextContents(){
        return ResponseEntity.status(HttpStatus.OK).body(contentsOrderService.nextContents());
    }

}
