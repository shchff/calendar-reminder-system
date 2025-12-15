package ru.grigorii.calendar_reminder_system.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.grigorii.calendar_reminder_system.dto.UserDto;
import ru.grigorii.calendar_reminder_system.model.Role;
import ru.grigorii.calendar_reminder_system.model.User;
import ru.grigorii.calendar_reminder_system.repository.UserRepository;
import ru.grigorii.calendar_reminder_system.service.mvc.UserMvcService;
import ru.grigorii.calendar_reminder_system.service.exception.UserAlreadyExistsException;
import ru.grigorii.calendar_reminder_system.service.exception.UserNotFoundException;
import ru.grigorii.calendar_reminder_system.service.rest.UserRestService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Реализация сервисов пользователя + реализация UserDetailsService из Spring Security
 */
@Service
@Transactional
public class UserServiceImplementation
        implements UserMvcService, UserRestService, UserDetailsService
{

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserServiceImplementation(UserRepository userRepository,
                                     PasswordEncoder encoder)
    {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    /**
     * Security: загружает информацию о пользователе по его юзернейму
     * @param email email пользователя (используется как username)
     * @return информация о пользователе
     * @throws UsernameNotFoundException пользоп
     */
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException
    {

        User user = findEntityByEmail(email);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }

    /**
     * Ищет пользователя по email
     * @param email email
     * @return пользователь
     */
    private User findEntityByEmail(String email)
    {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User with email " + email + " not found"));
    }

    /**
     * WEB: регистрирует пользователя
     * @param dto информация о пользователе для регистрации
     * @return созданный пользователь
     */
    @Override
    public UserDto register(UserDto dto)
    {

        if (userRepository.existsByEmail(dto.email()))
        {
            throw new UserAlreadyExistsException(dto.email());
        }

        User user = new User();
        user.setName(dto.name());
        user.setSurname(dto.surname());
        user.setEmail(dto.email());
        user.setRole(Role.valueOf(dto.role()));
        user.setPasswordHash(encoder.encode(dto.password()));

        user.setCreatedAt(LocalDateTime.now());

        return UserDto.fromEntity(userRepository.save(user));
    }

    /**
     * WEB: Возвращает dto пользователя по его email
     * @param email email пользователя
     * @return пользователь
     */
    @Override
    public UserDto getByEmail(String email)
    {
        return UserDto.fromEntity(findEntityByEmail(email));
    }

    /**
     * WEB: Обновляет информацию о пользователе и проверяет допустимость почт
     * @param currentEmail текущая почта
     * @param dto обновлённый пользователь
     */
    @Override
    public void updateProfile(String currentEmail, UserDto dto)
    {
        User user = findEntityByEmail(currentEmail);

        if (!currentEmail.equals(dto.email())
                && userRepository.existsByEmail(dto.email()))
        {
            throw new UserAlreadyExistsException(dto.email());
        }

        user.setName(dto.name());
        user.setSurname(dto.surname());
        user.setEmail(dto.email());
    }

    /**
     * WEB: изменяет пароль пользователя
     * @param email email пользователя
     * @param oldPassword старый пароль
     * @param newPassword новый пароль
     */
    @Override
    public void changePassword(String email,
                               String oldPassword,
                               String newPassword)
    {

        User user = findEntityByEmail(email);

        if (!encoder.matches(oldPassword, user.getPasswordHash()))
        {
            throw new IllegalArgumentException("Wrong password");
        }

        user.setPasswordHash(encoder.encode(newPassword));
    }

    /**
     * WEB: удаляет аккаунт пользователя с проверкой пароля
     * @param email email пользователя
     * @param password пароль пользователя
     */
    @Override
    public void deleteAccount(String email, String password)
    {

        User user = findEntityByEmail(email);

        if (!encoder.matches(password, user.getPasswordHash()))
        {
            throw new IllegalArgumentException("Wrong password");
        }

        userRepository.delete(user);
    }

    /**
     * REST API: возвращает список пользователей
     *
     * @return список пользователей
     */
    @Override
    public List<UserDto> findAll()
    {
        return userRepository.findAll().stream()
                .map(UserDto::fromEntity)
                .toList();
    }

    /**
     * REST API: возвращает пользователя по его id
     *
     * @param id id пользователя
     * @return пользователь
     */
    @Override
    public UserDto findById(Long id)
    {
        return UserDto.fromEntity(
                userRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException(id))
        );
    }

    /**
     * REST API: создаёт пользователя
     * @param dto данные для создания пользователя
     * @return созданный пользователь
     */
    public UserDto create(UserDto dto)
    {
        User user = new User();
        applyDto(user, dto);
        user.setCreatedAt(LocalDateTime.now());
        return UserDto.fromEntity(userRepository.save(user));
    }

    /**
     * REST API: изменение пользователя
     * @param id id пользователя
     * @param dto данные с изменением
     * @return изменённый пользователь
     */
    public UserDto update(Long id, UserDto dto)
    {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        applyDto(user, dto);
        return UserDto.fromEntity(user);
    }

    /**
     * REST API: удаляет пользователя
     * @param id id пользователя
     */
    public void delete(Long id)
    {
        if (!userRepository.existsById(id))
        {
            throw new EntityNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    /**
     * Маппер
     */
    private void applyDto(User user, UserDto dto)
    {
        user.setName(dto.name());
        user.setSurname(dto.surname());
        user.setEmail(dto.email());
        user.setPasswordHash(encoder.encode(dto.password()));
        user.setRole(Role.valueOf(dto.role()));
    }
}
