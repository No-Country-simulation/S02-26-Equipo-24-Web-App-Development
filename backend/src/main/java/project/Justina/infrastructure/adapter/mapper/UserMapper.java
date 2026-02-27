package project.Justina.infrastructure.adapter.mapper;

import org.springframework.stereotype.Component;
import project.Justina.domain.model.User;
import project.Justina.infrastructure.adapter.entity.UserEntity;

@Component
public class UserMapper {

    public UserEntity toEntity(User domain) {
        if (domain == null) return null;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(domain.getId());
        userEntity.setUsername(domain.getUsername());
        userEntity.setPassword(domain.getPassword());
        userEntity.setRole(domain.getRole());

        return userEntity;
    }

    public User toDomain(UserEntity entity){
        if (entity == null) return null;

        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getRole()
        );
    }
}
