package com.ssafy.meshroom.backend.domain.topic.game.initialquiz.handler;

import com.ssafy.meshroom.backend.domain.topic.game.initialquiz.dto.IniQuizCorrectSignal;
import com.ssafy.meshroom.backend.domain.topic.game.initialquiz.dto.IniQuizWordSignal;
import com.ssafy.meshroom.backend.global.util.HangulToChosung;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "게임-진실혹은거짓 API", description = "진실 혹은 거짓 게임을 위한 API")
public class InitialQuizEventHandler {

    private final RedisTemplate<String, String> redisTemplate;
//    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/game/ini-quiz/word/{sessionId}")
    @SendTo("/subscribe/game/ini-quiz/word/{sessionId}")
    public IniQuizWordSignal handleSubmitIniQuiz(@DestinationVariable String sessionId, IniQuizWordSignal iniQuizWordSignal) {
        log.info("submit quiz signal received : " + sessionId + " - " + iniQuizWordSignal.toString());

        setQuizWordToRedis(sessionId, iniQuizWordSignal);

        String convertedWord = convertQuizWord(iniQuizWordSignal.getQuizWord());
        iniQuizWordSignal.setQuizWord(convertedWord);

        return iniQuizWordSignal;
    }

    @MessageMapping("/game/ini-quiz/guess/{sessionId}")
    @SendTo("/subscribe/game/ini-quiz/guess/{sessionId}")
    public IniQuizCorrectSignal handleAnswerIniQuiz(@DestinationVariable String sessionId, IniQuizWordSignal iniQuizWordSignal) {
        log.info("guess quiz signal received : " + sessionId + " - " + iniQuizWordSignal.toString());

        boolean guessResult = isGuessWordCorrect(sessionId, iniQuizWordSignal.getQuizWord());

        IniQuizCorrectSignal iniQuizCorrectSignal = new IniQuizCorrectSignal();
        iniQuizCorrectSignal.setOvToken(iniQuizWordSignal.getOvToken());
        iniQuizCorrectSignal.setSubmittedWord(iniQuizWordSignal.getQuizWord());
        iniQuizCorrectSignal.setResult(guessResult);

        return iniQuizCorrectSignal;
    }

    private boolean isGuessWordCorrect(String sessionId, String guessWord) {
        String redisKey = "ini-quiz:" + sessionId;
        String answerWord = redisTemplate.opsForValue().get(redisKey);

        if (answerWord == null || guessWord == null) return false;

        return answerWord.equals(guessWord);
    }

    private String convertQuizWord(String quizWord) {

        return HangulToChosung.convertToChosung(quizWord);
    }

    private void setQuizWordToRedis(String sessionId, IniQuizWordSignal iniQuizWordSignal) {
        String redisKey = "ini-quiz:" + sessionId;

        String curWord = redisTemplate.opsForValue().get(redisKey);

        redisTemplate.opsForValue().set(redisKey, iniQuizWordSignal.getQuizWord());
    }

}
