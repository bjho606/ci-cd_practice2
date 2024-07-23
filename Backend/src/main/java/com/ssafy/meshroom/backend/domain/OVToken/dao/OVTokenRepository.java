package com.ssafy.meshroom.backend.domain.OVToken.dao;

import com.ssafy.meshroom.backend.domain.OVToken.domain.OVToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OVTokenRepository extends MongoRepository<OVToken, String> {
    List<OVToken> findAllBySessionSid(String sessionSid);
}
