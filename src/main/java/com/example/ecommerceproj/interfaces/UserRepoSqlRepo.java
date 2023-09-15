package com.example.ecommerceproj.interfaces;

import com.example.ecommerceproj.domain.User;
import com.example.ecommerceproj.usecase.UserRepoInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Random;

@RequiredArgsConstructor
@Repository
public class UserRepoSqlRepo implements UserRepoInterface {

    private final UserJpaRepo userJpaRepo;
    private final UserDboToUserConverter userDboToUserConverter;
    private final UserToUserDboConverter userToUserDboConverter;
    private final JavaMailSender javaMailSender;
    private final EmailService emailService;

    @Override
    public User createUser(User user) {
        UserDbo userDbo = userToUserDboConverter.convert(user);
        userDbo = userJpaRepo.save(userDbo);
        return userDboToUserConverter.convert(userDbo);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepo.existsByEmail(email);
    }

    @Override
    public UserDbo findByEmail(String email) {
        return userJpaRepo.findByEmail(email);
    }

    @Override
    // Send OTP to the user's email
    public void sendOTPByEmail(UserDbo user) {
        Integer otp = generateOTP();
        user.setOtp(otp);
        user.setOtpExpiration(LocalDateTime.now().plusMinutes(5)); // OTP expires in 5 minutes
        userJpaRepo.save(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("OTP Verification");
        message.setText("Your OTP: " + otp);
        javaMailSender.send(message);
    }

    // Generate a random OTP
    public Integer generateOTP() {
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }

    //Send Confirmation Email
     public void sendConfirmationEmail(UserDbo user) {
        String to = user.getEmail();
        String subject = "Account Confirmation";
        String text = "Thank you for registering with our service!";
        emailService.sendEmail(to, subject, text);
    }

    public UserDbo findUserDboById (Long userId){
        return userJpaRepo.findUserDboById(userId);
    }

    @Override
    public void changePassword(UserDbo userDbo, byte[] newPassword) {
        userDbo.setHashedPassword(newPassword);
        userJpaRepo.save(userDbo);
    }


}
