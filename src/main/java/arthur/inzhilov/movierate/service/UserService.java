package arthur.inzhilov.movierate.service;

import arthur.inzhilov.movierate.dto.UserDto;
import arthur.inzhilov.movierate.dto.UserDtoRegistration;
import arthur.inzhilov.movierate.entity.UserEntity;
import arthur.inzhilov.movierate.exception.NotFoundException;
import arthur.inzhilov.movierate.dao.RoleRepository;
import arthur.inzhilov.movierate.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;


    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public UserDto getUserDtoByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::mapUserEntityToUserDto)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Пользователя '%s' не существует.", username)));
    }

    @Transactional
    public Long addUser(UserDtoRegistration userDto) {
        UserEntity userEntity = UserEntity.builder()
                .id(null)
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .email(userDto.getEmail())
                .roleEntities(List.of(roleRepository.findById(2L)
                        .orElseThrow(() -> new NotFoundException("Роль не найдена"))))
                .build();
        UserEntity addedUser = userRepository.save(userEntity);
        return addedUser.getId();
    }

    private UserDto mapUserEntityToUserDto(UserEntity userEntity) {
        return UserDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .build();
    }
}
