package com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.dao;

import com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.domain.TFInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD
import java.util.List;
=======
>>>>>>> a76be45b45b028351e20ba8df5734d3378267173
import java.util.Optional;

@Repository
public interface TrueOrFalseRepository extends MongoRepository<TFInfo, String> {
    boolean existsByOvTokenAndSessionId(String ovToken, String sessionId);
    Optional<TFInfo> findByOvTokenAndSessionId(String ovToken, String sessionId);
<<<<<<< HEAD
    Optional<List<TFInfo>> findAllBySessionId(String sessionId);
=======
>>>>>>> a76be45b45b028351e20ba8df5734d3378267173
}
