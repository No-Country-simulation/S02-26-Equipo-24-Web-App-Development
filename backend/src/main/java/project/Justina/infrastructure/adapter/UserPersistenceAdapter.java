package project.Justina.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.Justina.domain.model.User;
import project.Justina.domain.repository.UserRepository;
import project.Justina.infrastructure.adapter.mapper.UserMapper;
import project.Justina.infrastructure.adapter.repository.JpaUserRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username)
                .map(userMapper::toDomain);
    }

    @Override
    public void save(User user) {
        jpaUserRepository.save(userMapper.toEntity(user));
    }
}
