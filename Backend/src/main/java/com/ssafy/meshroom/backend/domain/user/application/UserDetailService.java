package com.ssafy.meshroom.backend.domain.user.application;

import com.ssafy.meshroom.backend.domain.user.dao.UserRepository;
import com.ssafy.meshroom.backend.domain.user.domain.User;
import com.ssafy.meshroom.backend.domain.user.domain.UserRole;
import com.ssafy.meshroom.backend.global.common.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String id) {
        return userRepository.findById(id)
                .orElseThrow(()->new UsernameNotFoundException(id+" 사용자 없음"));
    }

    public String saveUser(String username, UserRole userRole){
        return userRepository.save(new User(username, userRole)).get_id();
    }

    public Response<?> changeUserName(String username) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User foundUser = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("유저 없음"));

        foundUser.setUsername(username);
        userRepository.save(foundUser);

        return new Response<>(true, 2000L, "유저 닉네임 정보가 성공적으로 수정되었습니다.", null);
    }
}
