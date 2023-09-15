package com.example.ecommerceproj.controller;

import com.example.ecommerceproj.domain.User;
import com.example.ecommerceproj.interfaces.*;
import com.example.ecommerceproj.usecase.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/user")
public class RegisterUserController {
    private final UserUseCase userUseCase;
    private final UserToRegisterUserResponseDtoConverter userToRegisterUserResponseDtoConverter;

    private boolean isValidRegistrationData(RegisterUserResponseDto requestDto) {
        // Check if required fields are not empty
        if (requestDto.getEmail() == null || requestDto.getEmail().isEmpty() ||
                requestDto.getHashedPassword() == null || Arrays.toString(requestDto.getHashedPassword()).isEmpty() ||
                requestDto.getFirstName() == null || requestDto.getFirstName().isEmpty() ||
                requestDto.getLastName() == null || requestDto.getLastName().isEmpty() ||
                requestDto.getPhoneNumber() == null || requestDto.getPhoneNumber().isEmpty() ||
                requestDto.getAddress() == null || requestDto.getAddress().isEmpty()) {
            return false;
        }
        // Validate email format using a regular expression
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!requestDto.getEmail().matches(emailPattern)) {
            return false;
        }
        // Check if the email already exists in the database
        if (emailExists(requestDto.getEmail())) {
            return false;
        }
        return true;
    }
    private boolean emailExists(String email) {
        return userUseCase.existsByEmail(email); // Example method name, adjust as needed
    }
    @PostMapping("/register")
    public ResponseEntity<HttpStatus> registerUser(@RequestBody RegisterUserResponseDto requestDto) {
       if (!isValidRegistrationData(requestDto))
           return ResponseEntity.badRequest().body(HttpStatus.NOT_FOUND);
        // Convert requestDto to User;
        User user = RegisterUserDtoToUserConverter.convertToUser(requestDto);
        // Register the user
        User registeredUser = userUseCase.register(user);

        if (registeredUser != null) {
            return ResponseEntity.ok(HttpStatus.ACCEPTED);
        } else {
            return ResponseEntity.badRequest().body(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/sendOTP")
    public ResponseEntity<HttpStatus> sendOTP(@RequestParam Long userId){
    //send OTP by email
    UserDbo userDbo = userUseCase.findUserDboById(userId);
    userUseCase.sendOTPByEmail(userDbo);
    return ResponseEntity.ok(HttpStatus.CREATED);
    }
    @GetMapping("/confirm/OTP")
    public ResponseEntity<HttpStatus> confirmOTP(@RequestParam Long userId , Integer otp){
    //send OTP by email
    UserDbo userDbo = userUseCase.findUserDboById(userId);
    if (otp.equals(userDbo.getOtp())){
    userUseCase.sendConfirmationEmail(userDbo);
    return ResponseEntity.ok(HttpStatus.CREATED);}
    else return ResponseEntity.badRequest().body(HttpStatus.NOT_ACCEPTABLE);
    }
    @PostMapping("/login")
    public ResponseEntity<HttpStatus> login (@RequestBody RequestLoginDto requestLoginDto){
        if (!emailExists(requestLoginDto.getEmail()))
            return ResponseEntity.badRequest().body(HttpStatus.NOT_FOUND);

        UserDbo user =userUseCase.getByEmail(requestLoginDto.getEmail());

        // Validate the password
    if (! (Arrays.equals(requestLoginDto.getHashedPassword(), user.getHashedPassword())) )
        return ResponseEntity.badRequest().body(HttpStatus.NOT_ACCEPTABLE);

     return ResponseEntity.ok(HttpStatus.ACCEPTED);
}

   @PostMapping("/forgetPass")
   public ResponseEntity<HttpStatus> forgetPass(@RequestParam String email){
       //send OTP by email
       UserDbo userDbo = userUseCase.getByEmail(email);
       userUseCase.sendOTPByEmail(userDbo);
       return ResponseEntity.ok(HttpStatus.CREATED);
   }
    @PostMapping("/changePass")
    public ResponseEntity<HttpStatus> confirmOTP(@RequestParam Long userId , @RequestParam Integer otp , @RequestParam byte[] newPassword){
    //send OTP by email
    UserDbo userDbo = userUseCase.findUserDboById(userId);
    if (otp.equals(userDbo.getOtp())) {
    userUseCase.changePassword(userDbo,newPassword);
    return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
    else return ResponseEntity.badRequest().body(HttpStatus.NOT_ACCEPTABLE);
}}