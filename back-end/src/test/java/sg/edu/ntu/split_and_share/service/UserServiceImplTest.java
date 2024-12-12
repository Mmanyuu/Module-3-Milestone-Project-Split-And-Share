package sg.edu.ntu.split_and_share.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @InjectMocks
    UserServiceImpl userService;

    @Test
    public void testCreateUser() {
        User user = User.builder().username("Manyu").password("123456789").name("Mmanyuu").build();

        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.createUser(user);

        assertEquals(user, savedUser, "The saved user should be the same as the new customer");

        verify(userRepository, times(1)).save(user);
    }
}
