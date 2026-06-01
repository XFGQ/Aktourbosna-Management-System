package org.example.service;

import org.example.application.dto.guide.GuideCreateDTO;
import org.example.application.dto.guide.GuideResponseDTO;
import org.example.application.dto.guide.GuideSummaryDTO;
import org.example.application.dto.guide.GuideUpdateDTO;
import org.example.application.exception.ResourceNotFoundException;
import org.example.application.mapper.GuideMapper;
import org.example.model.Guide;
import org.example.model.User;
import org.example.model.UserRole;
import org.example.repository.GuideRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GuideServiceTest {

    @Mock GuideRepository guideRepository;
    @Mock GuideMapper guideMapper;
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks GuideService guideService;

    private Guide guide;
    private User user;
    private GuideResponseDTO responseDTO;
    private GuideSummaryDTO summaryDTO;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("guideuser");
        user.setEmail("guide@example.com");
        user.setPassword("encoded");
        user.setRole(UserRole.GUIDE);

        guide = new Guide();
        guide.setId(1L);
        guide.setUser(user);
        guide.setPhone("555-1234");
        guide.setBaseCity("Sarajevo");

        responseDTO = new GuideResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setUsername("guideuser");

        summaryDTO = new GuideSummaryDTO();
        summaryDTO.setId(1L);
        summaryDTO.setUsername("guideuser");

        setAdminSecurityContext();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void setAdminSecurityContext() {
        SecurityContextHolder.setContext(new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken(
                        "adminuser", null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
        ));
    }

    @Test
    void getAllGuides_returnsMappedList() {
        when(guideRepository.findAll()).thenReturn(List.of(guide));
        when(guideMapper.toResponse(guide)).thenReturn(responseDTO);

        List<GuideResponseDTO> result = guideService.getAllGuides();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("guideuser");
    }

    @Test
    void getAllGuidesSummary_returnsSummaryList() {
        when(guideRepository.findAll()).thenReturn(List.of(guide));
        when(guideMapper.toSummary(guide)).thenReturn(summaryDTO);

        List<GuideSummaryDTO> result = guideService.getAllGuidesSummary();

        assertThat(result).hasSize(1).containsExactly(summaryDTO);
    }

    @Test
    void getGuideById_found_returnsDTO() {
        when(guideRepository.findById(1L)).thenReturn(Optional.of(guide));
        when(guideMapper.toResponse(guide)).thenReturn(responseDTO);

        GuideResponseDTO result = guideService.getGuideById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getGuideById_notFound_throwsResourceNotFoundException() {
        when(guideRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> guideService.getGuideById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getMyProfile_existingUser_returnsDTO() {
        SecurityContextHolder.setContext(new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken("guideuser", null, List.of())
        ));

        when(guideRepository.findByUser_Username("guideuser")).thenReturn(Optional.of(guide));
        when(guideMapper.toResponse(guide)).thenReturn(responseDTO);

        GuideResponseDTO result = guideService.getMyProfile();

        assertThat(result.getUsername()).isEqualTo("guideuser");
    }

    @Test
    void getMyProfile_notFound_throwsResourceNotFoundException() {
        SecurityContextHolder.setContext(new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken("unknown", null, List.of())
        ));

        when(guideRepository.findByUser_Username("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> guideService.getMyProfile())
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createGuide_newUsername_createsUserAndGuide() {
        GuideCreateDTO dto = new GuideCreateDTO();
        dto.setUsername("newguide");
        dto.setPassword("pass123");
        dto.setEmail("newguide@example.com");
        dto.setBaseCity("Mostar");

        when(userRepository.existsByUsername("newguide")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(guideMapper.toEntity(dto)).thenReturn(guide);
        when(guideRepository.save(guide)).thenReturn(guide);
        when(guideMapper.toResponse(guide)).thenReturn(responseDTO);

        GuideResponseDTO result = guideService.createGuide(dto);

        assertThat(result.getUsername()).isEqualTo("guideuser");
        verify(userRepository).save(any(User.class));
        verify(guideRepository).save(guide);
    }

    @Test
    void createGuide_duplicateUsername_throwsIllegalArgumentException() {
        GuideCreateDTO dto = new GuideCreateDTO();
        dto.setUsername("guideuser");
        dto.setEmail("guide@example.com");
        dto.setPassword("pass123");

        when(userRepository.existsByUsername("guideuser")).thenReturn(true);

        assertThatThrownBy(() -> guideService.createGuide(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("guideuser");
    }

    @Test
    void updateMyProfile_existingGuide_updatesAndReturns() {
        SecurityContextHolder.setContext(new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken("guideuser", null, List.of())
        ));

        GuideUpdateDTO dto = new GuideUpdateDTO();
        dto.setBaseCity("Banja Luka");

        when(guideRepository.findByUser_Username("guideuser")).thenReturn(Optional.of(guide));
        when(guideRepository.save(guide)).thenReturn(guide);
        when(guideMapper.toResponse(guide)).thenReturn(responseDTO);

        GuideResponseDTO result = guideService.updateMyProfile(dto);

        assertThat(result).isEqualTo(responseDTO);
        verify(guideMapper).updateEntityFromDto(dto, guide);
    }

    @Test
    void deleteGuide_found_deletesGuideAndUser() {
        when(guideRepository.findById(1L)).thenReturn(Optional.of(guide));

        guideService.deleteGuide(1L);

        verify(guideRepository).delete(guide);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteGuide_notFound_throwsResourceNotFoundException() {
        when(guideRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> guideService.deleteGuide(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
