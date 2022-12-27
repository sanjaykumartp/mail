package com.ebs.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ebs.entity.User;
import com.ebs.entity.VerificationToken;
import com.ebs.event.RegistrationCompleteEvent;
import com.ebs.model.PasswordModel;
import com.ebs.model.UserModel;
import com.ebs.service.UserService;
//import com.employeemanagement.entity.Employee;

import lombok.extern.slf4j.Slf4j;

/*
 * 29/11/2022
 * Admin acces is working fine
 * default pages also working fine
 *  NOTE-
 *  1- Register page not working
 *  2- User access is not working
 */
@Slf4j
@RestController
@RequestMapping("/ebs")
public class EBS_Controller {
	@Autowired
	private  UserService userService;

	@Autowired
	private ApplicationEventPublisher publisher;

	@GetMapping("/wc")
	public String ebs() {
		return "Welcome to EBS in EBS Controller";
	}
	@GetMapping("/home")
	public String home() {
		return "Welcome Home in EBS Controller";
	}
	@GetMapping("/user")
	public String user() {
		return "USER accesing this method in EBS Controller";
	}
	@GetMapping("/admin")
	public String admin() {
		return "Admin Method Calling";
	}

	/*
	 * to get the url we need http sevletrequest
	 */
	@PostMapping("/register")
	public ResponseEntity<?> register( @RequestBody UserModel userModel, final HttpServletRequest request) {
		System.out.println("Register Method Calling");
		User user = userService.register(userModel);
		//this event send token to the user and email to verify the link 
		publisher.publishEvent(new RegistrationCompleteEvent(
				user,
				// here we creating method to create the url
				applicationUrl(request)
				));

		return new ResponseEntity<User>(user, HttpStatus.CREATED);
	}
	
	


	/*
	 * it is mainly used to verify the link   
	 */
	@GetMapping("/verifyRegistration")
	public String verifyRegistration(@RequestParam("token") String token) {
		String result = userService.validateVerificationToken(token);
		if(result.equalsIgnoreCase("valid")) {
			return "User Verified Successfully";
		}
		return "Bad User";
	}
	/*
	 * Resend the verification link to the user
	 */
	@GetMapping("/resendVerifyToken")
	public String resendVerificationToken(@RequestParam("token") String oldToken,
			HttpServletRequest request) {
		VerificationToken verificationToken
		= userService.generateNewVerificationToken(oldToken);
		User user = verificationToken.getUser();
		resendVerificationTokenMail(user, applicationUrl(request), verificationToken);
		return "Verification Link Sent";
	}
	/*
	 * to reset the password 
	 * first we need create one passwordModel class 
	 */
	@PostMapping("/resetPassword")
	public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request) {
		User user = userService.findUserByEmail(passwordModel.getEmail());
		String url = "";
		if(user!=null) {
			String token = UUID.randomUUID().toString();
			userService.createPasswordResetTokenForUser(user,token);
			url = passwordResetTokenMail(user,applicationUrl(request), token);
		}
		return url;
	}

	@PostMapping("/savePassword")
	public String savePassword(@RequestParam("token") String token,
			@RequestBody PasswordModel passwordModel) {
		String result = userService.validatePasswordResetToken(token);
		if(!result.equalsIgnoreCase("valid")) {
			return "Invalid Token";
		}
		Optional<User> user = userService.getUserByPasswordResetToken(token);
		if(user.isPresent()) {
			userService.changePassword(user.get(), passwordModel.getNewPassword());
			return "Password Reset Successfully";
		} else {
			return "Invalid Token";
		}
	}

	@PostMapping("/changePassword")
	public String changePassword(@RequestBody PasswordModel passwordModel){
		User user = userService.findUserByEmail(passwordModel.getEmail());
		if(!userService.checkIfValidOldPassword(user,passwordModel.getOldPassword())) {
			return "Invalid Old Password";
		}
		//Save New Password
		userService.changePassword(user,passwordModel.getNewPassword());
		return "Password Changed Successfully";
	}

	private  String passwordResetTokenMail(User user, String applicationUrl, String token) {
		String url =
				applicationUrl
				+ "/savePassword?token="
				+ token;

		userService.sendSimpleEmail("sanjay.kumar@estuate.com", "Reset password", url);
		//sendVerificationEmail()
		log.info("Click the link to Reset your Password: {}",
				url);
		return url;
	}

/*
 * to resend the verification link to mail
 * 
 */
	private  void resendVerificationTokenMail(User user, String applicationUrl, VerificationToken verificationToken) {
		String url =
				applicationUrl
				+ "/verifyRegistration?token="
				+ verificationToken.getToken();

		userService.sendSimpleEmail("sanjay.kumar@estuate.com", "Resend Verification Mail", url);
		//sendVerificationEmail()
		log.info("Click the link to verify your account: {}",
				url);
	}

	/*
	 * here we can create the url to pass the url 
	 */
	private static String applicationUrl(HttpServletRequest request) {
		return "http://" +
				request.getServerName() +
				":" +
				request.getServerPort() +
				request.getContextPath();
	}

	@GetMapping("/user/{userName}")
	public ResponseEntity<?> getUserByUserName(@PathVariable("userName") String userName) {	
		User user = userService.getUserByUserName(userName);
		return new ResponseEntity<User>(user, HttpStatus.ACCEPTED);


	}


}
