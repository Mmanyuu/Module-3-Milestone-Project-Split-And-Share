package sg.edu.ntu.split_and_share.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import sg.edu.ntu.split_and_share.entity.User;
import sg.edu.ntu.split_and_share.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    // As our data structure is storing information in repo and inject in service
    // file. Hence need to use mock and inject.

    @Mock
    private UserRepository userRepository;

    @Mock
    private DashboardRepository dashboardRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test // Create User Test - Checked Correct
    public void testCreateUser() {
        // Arrange
        User user = User.builder().username("Mmanyuu").password("123456789").name("Manyu").build();
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User savedUser = userService.createUser(user);

        // Assert - checking individual fields of savedUser
        assertNotNull(savedUser, "Saved user should not be null");
        assertEquals("Mmanyuu", savedUser.getUsername(), "Username should match.");
        assertEquals("123456789", savedUser.getPassword(), "Password should match.");
        assertEquals("Manyu", savedUser.getName(), "Name should match.");

        // Verify - ensures the process (interactions with dependencies) is correct
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testInvalidUserCreation() {
        // invalid can be 1. username, password and name blank, 2.password less than 8
        // char and 3. username taken
    }

    @Test
    public void testGetUser() {

        // User user =
        // User.builder().username("Mmanyuu").password("123456789").name("Manyu").build();
        // when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        // User foundUser = userService.getUser(user.getUsername());

        // assertTrue(foundUser.isPresent());
        // assertEquals("Mmanyuu", foundUser.getUsername());
    }

    @Test
    public void testUserNotFound() {

    }
}
