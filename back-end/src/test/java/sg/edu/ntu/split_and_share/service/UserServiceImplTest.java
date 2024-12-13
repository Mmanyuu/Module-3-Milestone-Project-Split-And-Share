package sg.edu.ntu.split_and_share.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import sg.edu.ntu.split_and_share.entity.Dashboard;
import sg.edu.ntu.split_and_share.entity.User;
import sg.edu.ntu.split_and_share.exception.UserNotFoundException;
import sg.edu.ntu.split_and_share.exception.UsernameIsTakenException;
import sg.edu.ntu.split_and_share.repository.DashboardRepository;
import sg.edu.ntu.split_and_share.repository.UserRepository;

@ExtendWith(MockitoExtension.class) // this extension automatically initializes @mock and @injectmocks annotations
public class UserServiceImplTest {

    // As our data structure is storing information in repo and inject in service
    // file. Hence need to use mock and inject.

    @Mock
    private UserRepository userRepository;

    @Mock
    private DashboardRepository dashboardRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test // Create User Test for Success Creation - Checked correct
    public void testCreateUser_SuccessCreation() {
        // Arrange
        when(userRepository.findByUsername("Mmanyuu")).thenReturn(Optional.empty()); // this is when findbyusername() is called, the username is not occupied.

        User newUser = User.builder().username("Mmanyuu").password("123456789").name("Manyu").build(); // Proceed to create new user and its dashboard

        Dashboard newUserDashboard = new Dashboard();
        newUserDashboard.setName("Manyu's Dashboard");

        // simlulating saving the new user and uses thenAnswer instead of thenReturn is
        // to have more flexibility in determining how the method should response if
        // called.
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0); // Access the first argument of save() which is the entity User (everything within the entity)
            savedUser.setId(1L); // Setting the ID within the enity after access the entity from the code before this
            return savedUser; // return modified User
        });

        // Act - executing the method
        User createdUser = userService.createUser(newUser);

        // Assert - checking individual fields of savedUser
        assertNotNull(createdUser, "Newly created user should not be null");
        assertNotNull(newUserDashboard, "Dashboard should be created together with user creation and not Null");
        assertEquals(newUser.getUsername(), createdUser.getUsername(), "Username should match.");
        assertEquals(newUser.getPassword(), createdUser.getPassword(), "Password should match.");
        assertEquals(newUser.getName(), createdUser.getName(), "Name should match.");

        // Verify
        verify(userRepository, times(1)).findByUsername("Mmanyuu");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testCreateUser_UsernameTaken() {

        // Creating a user with username that is already taken and simulate the behavior
        // of findByUsername(). It will return a optional containing existing user
        User existingUser = User.builder().username("Mmanyuu").name("Manyu").build();
        when(userRepository.findByUsername("Mmanyuu")).thenReturn(Optional.of(existingUser));

        // Attempt to create another user with the same username
        User newUser = User.builder().username("Mmanyuu").name("NewUser").build();

        // Act & Assert - assert that the exception is suppose to be thrown when same username is detected
        Exception exception = assertThrows(UsernameIsTakenException.class, () -> userService.createUser(newUser),
                "Expected createUser to throw an exception, but it didn't");
        assertEquals("Username already exists", exception.getMessage());

        // Verify
        verify(userRepository, times(1)).findByUsername("Mmanyuu"); //Verify that the findByUsername is called only once
        verify(userRepository, never()).save(any(User.class)); //Verify that if a detection of same username, it will not be saved or user is not created
    }

        @Test
    public void testGetUser_Success() {

        // Arrange - setup beginning information
        User user = User.builder().username("Mmanyuu").password("123456789").name("Manyu").build();
        when(userRepository.findByUsername("Mmanyuu")).thenReturn(Optional.of(user));

        //Act - execute the test method
        User foundUser = userService.getUser("Mmanyuu");

        //Assert - compare result
        assertNotNull(foundUser);
        assertEquals("Mmanyuu", foundUser.getUsername(),"Username input should be the same as in the data");
    }

    @Test
    public void testGetUser_UserNotFound() {
        //Mock repo behavior for non-existing username
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        //Act and Assert
        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.getUser("nonExistentUser"),
        "Expected getUser to throw an exception, but it didn't");
        assertEquals("User not found.", exception.getMessage()); //this should align with UserNotFoundException file

    }

}
