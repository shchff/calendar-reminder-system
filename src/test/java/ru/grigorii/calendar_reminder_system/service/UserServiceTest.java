package ru.grigorii.calendar_reminder_system.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.grigorii.calendar_reminder_system.dto.UserDto;
import ru.grigorii.calendar_reminder_system.model.Role;
import ru.grigorii.calendar_reminder_system.model.User;
import ru.grigorii.calendar_reminder_system.repository.UserRepository;
import ru.grigorii.calendar_reminder_system.service.exception.UserAlreadyExistsException;
import ru.grigorii.calendar_reminder_system.service.exception.UserNotFoundException;
import ru.grigorii.calendar_reminder_system.service.impl.UserServiceImplementation;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest
{

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserServiceImplementation service;

    private User user;

    @BeforeEach
    void setUp()
    {
        user = new User();
        user.setId(1L);
        user.setName("Ivan");
        user.setSurname("Ivanov");
        user.setEmail("ivan@test.com");
        user.setRole(Role.USER);
        user.setPasswordHash("hash");
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails()
    {
        when(userRepository.findByEmail("ivan@test.com"))
                .thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("ivan@test.com");

        assertEquals("ivan@test.com", details.getUsername());
        assertEquals("hash", details.getPassword());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_shouldThrow_whenNotFound()
    {
        when(userRepository.findByEmail("no@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("no@test.com"));
    }

    @Test
    void register_shouldCreateUser()
    {
        UserDto dto = UserDto.forCreate(
                "Ivan", "Ivanov", "ivan@test.com", "123456", "USER"
        );

        when(userRepository.existsByEmail(dto.email()))
                .thenReturn(false);
        when(encoder.encode("123456"))
                .thenReturn("encoded");
        when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        UserDto result = service.register(dto);

        assertEquals("ivan@test.com", result.email());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrow_whenEmailExists()
    {
        UserDto dto = UserDto.forCreate(
                "Ivan", "Ivanov", "ivan@test.com", "123456", "USER"
        );

        when(userRepository.existsByEmail(dto.email()))
                .thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> service.register(dto));
    }

    @Test
    void updateProfile_shouldUpdateUser()
    {
        UserDto dto = UserDto.forUpdate(
                1L, "New", "Name", "new@test.com"
        );

        when(userRepository.findByEmail("ivan@test.com"))
                .thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@test.com"))
                .thenReturn(false);

        service.updateProfile("ivan@test.com", dto);

        assertEquals("new@test.com", user.getEmail());
    }

    @Test
    void updateProfile_shouldThrow_whenEmailTaken()
    {
        UserDto dto = UserDto.forUpdate(
                1L, "New", "Name", "taken@test.com"
        );

        when(userRepository.findByEmail("ivan@test.com"))
                .thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("taken@test.com"))
                .thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> service.updateProfile("ivan@test.com", dto));
    }

    @Test
    void changePassword_shouldChange()
    {
        when(userRepository.findByEmail("ivan@test.com"))
                .thenReturn(Optional.of(user));
        when(encoder.matches("old", "hash"))
                .thenReturn(true);
        when(encoder.encode("new"))
                .thenReturn("newHash");

        service.changePassword("ivan@test.com", "old", "new");

        assertEquals("newHash", user.getPasswordHash());
    }

    @Test
    void changePassword_shouldThrow_whenWrongPassword()
    {
        when(userRepository.findByEmail("ivan@test.com"))
                .thenReturn(Optional.of(user));
        when(encoder.matches("wrong", "hash"))
                .thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> service.changePassword("ivan@test.com", "wrong", "new"));
    }

    @Test
    void deleteAccount_shouldDelete()
    {
        when(userRepository.findByEmail("ivan@test.com"))
                .thenReturn(Optional.of(user));
        when(encoder.matches("pass", "hash"))
                .thenReturn(true);

        service.deleteAccount("ivan@test.com", "pass");

        verify(userRepository).delete(user);
    }

    @Test
    void deleteAccount_shouldThrow_whenWrongPassword()
    {
        when(userRepository.findByEmail("ivan@test.com"))
                .thenReturn(Optional.of(user));
        when(encoder.matches("wrong", "hash"))
                .thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> service.deleteAccount("ivan@test.com", "wrong"));
    }

    @Test
    void findAll_shouldReturnUsers()
    {
        when(userRepository.findAll())
                .thenReturn(List.of(user));

        List<UserDto> result = service.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void findById_shouldReturnUser()
    {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserDto dto = service.findById(1L);

        assertEquals("ivan@test.com", dto.email());
    }

    @Test
    void findById_shouldThrow_whenNotFound()
    {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.findById(1L));
    }

    @Test
    void delete_shouldDelete()
    {
        when(userRepository.existsById(1L))
                .thenReturn(true);

        service.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_shouldThrow_whenNotExists()
    {
        when(userRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> service.delete(1L));
    }
}
