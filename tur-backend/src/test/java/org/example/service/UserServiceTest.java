package org.example.service;

import org.example.application.dto.UserRegistrationDTO;
import org.example.application.dto.UserResponseDTO;
import org.example.application.dto.UserUpdateDTO;
import org.example.application.exception.ResourceNotFoundException;
import org.example.application.mapper.UserMapper;
import org.example.model.Guide;
import org.example.model.User;
import org.example.model.UserRole;
import org.example.repository.GuideRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private GuideRepository guideRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User sampleUser;
    private UserResponseDTO sampleUserResponse;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(UserRole.ADMIN)
                .build();

        sampleUserResponse = new UserResponseDTO();
        sampleUserResponse.setId(1L);
        sampleUserResponse.setUsername("testuser");
        sampleUserResponse.setEmail("test@example.com");
        sampleUserResponse.setRole("ADMIN");
    }

    @Test
    @DisplayName("getAllUsers - positive: returns list of user responses")
    void getAllUsers_positive_returnsUserResponseList() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));
        when(userMapper.toResponseDto(sampleUser)).thenReturn(sampleUserResponse);

        List<UserResponseDTO> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser");
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("getAllUsers - negative: no users in database returns empty list")
    void getAllUsers_negative_noUsers_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponseDTO> result = userService.getAllUsers();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getUserById - positive: existing ID returns user response")
    void getUserById_positive_existingId_returnsUserResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userMapper.toResponseDto(sampleUser)).thenReturn(sampleUserResponse);

        UserResponseDTO result = userService.getUserById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("getUserById - negative: non-existing ID throws ResourceNotFoundException")
    void getUserById_negative_nonExistingId_throwsResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("deleteUser - positive: existing user is deleted successfully")
    void deleteUser_positive_existingId_deletesSuccessfully() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteUser - negative: non-existing ID throws ResourceNotFoundException")
    void deleteUser_negative_nonExistingId_throwsResourceNotFoundException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(userRepository, never()).deleteById(any());
    }
}