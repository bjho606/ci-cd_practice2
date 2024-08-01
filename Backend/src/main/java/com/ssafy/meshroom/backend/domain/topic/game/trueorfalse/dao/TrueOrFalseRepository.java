package com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.dao;

import com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.domain.TFInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrueOrFalseRepository extends MongoRepository<TFInfo, String> {
    Optional<TFInfo> findByOvTokenAndSessionId(String ovToken, String sessionId);
}
