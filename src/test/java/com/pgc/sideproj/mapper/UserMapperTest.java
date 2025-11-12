package com.pgc.sideproj.mapper;

import com.pgc.sideproj.dto.db.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("UserMapper 테스트")
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    private UserDTO createTestUser(String email, String nickname){
        UserDTO user = new UserDTO();
        user.setEmail(email);
        user.setPassword("hashedpassword123");
        user.setNickname(nickname);
        user.setRole("USER");
        return user;
    }

    /**
     * 1. 회원가입(save) 및 PK 자동 생성 테스트
     * 트랜잭션(@MybatisTest에 의해 기본 적용) 덕분에 테스트 후 데이터는 롤백됩니다.
     */
    @Test
    @DisplayName("사용자 저장(save) 및 ID 자동 생성")
    void save_should_generate_id_and_persist_user() {
        // given
        UserDTO newUser = createTestUser("test@example.com", "테스터1");

        // when
        userMapper.save(newUser);

        // then
        // PK(id)가 자동으로 생성되었는지 확인
        assertThat(newUser.getId()).isNotNull();
        assertThat(newUser.getId()).isGreaterThan(0L);

        // 저장된 데이터가 findByEmail로 조회되는지 확인 (쿼리 검증)
        Optional<UserDTO> foundUser = userMapper.findByEmail("test@example.com");
        assertTrue(foundUser.isPresent());
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(foundUser.get().getNickname()).isEqualTo("테스터1");
    }

    /**
     * 2. 이메일로 사용자 조회(findByEmail) 테스트
     */
    @Test
    @DisplayName("이메일로 사용자 조회 성공")
    void findByEmail_should_return_user_when_exists() {
        // given: 사용자 저장
        userMapper.save(createTestUser("findme@test.com", "조회대상"));

        // when: 이메일로 조회
        Optional<UserDTO> foundUser = userMapper.findByEmail("findme@test.com");

        // then: 조회가 성공하고 데이터가 일치하는지 확인
        assertTrue(foundUser.isPresent());
        assertThat(foundUser.get().getEmail()).isEqualTo("findme@test.com");
    }

    /**
     * 3. 이메일로 사용자 조회 실패 테스트
     */
    @Test
    @DisplayName("존재하지 않는 이메일 조회 시 Optional.empty() 반환")
    void findByEmail_should_return_empty_when_not_exists() {
        // when: 존재하지 않는 이메일로 조회
        Optional<UserDTO> foundUser = userMapper.findByEmail("nonexistent@test.com");

        // then: Optional.empty()가 반환되는지 확인
        assertTrue(foundUser.isEmpty());
    }
}
