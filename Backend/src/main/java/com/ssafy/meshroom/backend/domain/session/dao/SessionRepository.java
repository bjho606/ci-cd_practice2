package com.ssafy.meshroom.backend.domain.session.dao;

import com.ssafy.meshroom.backend.domain.session.domain.Session;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends MongoRepository<Session, String> {
}
