package com.ssafy.meshroom.backend.domain.OVToken.dao;

import com.ssafy.meshroom.backend.domain.OVToken.domain.OVToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OVTokenRepository extends MongoRepository<OVToken, String> {
    List<OVToken> findAllBySessionSid(String sessionSid);

<<<<<<< HEAD
    List<OVToken> findAllByUserSid(String userSid);
=======
    List<OVToken> findAllByUserSid(String uesrSid);
>>>>>>> a76be45b45b028351e20ba8df5734d3378267173

    void deleteAllBySessionSid(String sessionSid);

    void deleteBySessionSidAndUserSid(String sessionSid, String userSid);
<<<<<<< HEAD

=======
>>>>>>> a76be45b45b028351e20ba8df5734d3378267173
}