package com.pgc.sideproj.mapper;

import com.pgc.sideproj.dto.db.UserDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    /**
     * 회원가입: 새로운 사용자 정보를 DB에 저장합니다.
     * @param user 저장할 UserDTO 객체
     */
    void save(UserDTO user);

    /**
     * 이메일을 통해 사용자 정보를 조회합니다.
     * Optional<UserDTO>를 사용하여 조회 결과가 없을 경우 null 대신 Optional.empty()를 반환합니다. (중요!)
     * @param email 조회할 사용자의 이메일
     * @return UserDTO를 포함하는 Optional 객체
     */
    Optional<UserDTO> findByEmail(String email);

    List<UserDTO> findAll();
}
