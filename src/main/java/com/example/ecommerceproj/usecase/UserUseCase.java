package com.example.ecommerceproj.usecase;
import com.example.ecommerceproj.domain.User;
import com.example.ecommerceproj.interfaces.UserDbo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserUseCase {
    private final UserRepoInterface userRepoInterface;

    @Autowired
    public UserUseCase(UserRepoInterface userRepoInterface) {
        this.userRepoInterface = userRepoInterface;
    }
    public User register(User user) {
        return userRepoInterface.createUser(user);
    }

    public boolean existsByEmail(String email) {
        return userRepoInterface.existsByEmail(email);
    }

    public UserDbo getByEmail(String email) {
        return userRepoInterface.findByEmail(email);
    }

    public UserDbo findUserDboById(Long userId){
        return userRepoInterface.findUserDboById(userId);

    }

    /* public void forgetPassword (ForgetPasswordRequestDto forgetPasswordRequestDto) throws IllegalAccessException{
        userRepoInterface.getByEmail(forgetPasswordRequestDto.getEmail());
    }*/

    public void sendOTPByEmail(UserDbo user){
         userRepoInterface.sendOTPByEmail(user);
    }

    public void sendConfirmationEmail(UserDbo user){
        userRepoInterface.sendConfirmationEmail(user);
    }

    public void changePassword(UserDbo userDbo, byte[] newPassword) {
        userRepoInterface.changePassword(userDbo, newPassword);
    }
}