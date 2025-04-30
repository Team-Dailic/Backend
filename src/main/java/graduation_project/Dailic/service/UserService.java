package graduation_project.Dailic.service;

import graduation_project.Dailic.domain.User;
import graduation_project.Dailic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    //유저 저장
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    //전체 유저 조회
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //id로 조회
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()
                ->new IllegalArgumentException("User not found:" + id));
    }

    //삭제
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
