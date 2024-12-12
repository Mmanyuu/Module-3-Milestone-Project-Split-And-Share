package sg.edu.ntu.split_and_share.service;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
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

    @InjectMocks
    UserServiceImpl userService;

    @Test // Create User Test - Checked Correct
    public void testCreateUser() {
        User user = User.builder().username("Mmanyuu").password("123456789").name("Manyu").build();
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.createUser(user);
        assertEquals(user, savedUser, "The saved user should be the same as the new customer");

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testInvalidUserCreation() {
        // invalid can be 1. username, password and name blank, 2.password less than 8
        // char and 3. username taken
    }

    @Test
    public void testGetUser() {

        User user = User.builder().username("Mmanyuu").password("123456789").name("Manyu").build();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        User foundUser = userService.getUser(user.getUsername());

        assertTrue(foundUser.isPresent());
        assertEquals("Mmanyuu", foundUser.getUsername());
    }

    @Test
    public void testUserNotFound() {

    }
}
