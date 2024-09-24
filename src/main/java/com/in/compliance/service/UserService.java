package com.in.compliance.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.in.compliance.dto.UidDto;
import com.in.compliance.dto.UserSignInDto;
import com.in.compliance.exception.InvalidUidException;
import com.in.compliance.exception.UnauthorisedSignInException;
import com.in.compliance.models.ERole;
import com.in.compliance.models.User;
import com.in.compliance.repository.UserRepository;

@Service
public class UserService {

    @Autowired(required = true)
    UserRepository userRepo;


    public UserSignInDto signIn(UidDto uidDto) throws InvalidUidException,  UnauthorisedSignInException {
    	 Optional<User> findUser = userRepo.findById(uidDto.getUid());
        if(findUser.isEmpty()) {
            throw new InvalidUidException("UID in credential is not valid");
        }

        if(!findUser.get().getRoles().contains(ERole.ROLE_USER)) {
            throw new UnauthorisedSignInException("You are not authorised to access!");
        }

        User user = findUser.get();
     //  user.setTokenIsActive(true);
        userRepo.save(user);

		/*
		 * Optional<Volunteer> volunteerInfo = volunteerRepo.findById(user.getId());
		 * if(volunteerInfo.isEmpty()) { throw new
		 * NoVolunteerFoundExceptions("No volunteer info found"); }
		 */

        return new UserSignInDto(user);
    }


    public void signout(UidDto uidDto) throws InvalidUidException {
        Optional<User> findUser = userRepo.findById(uidDto.getUid());
        if(findUser.isEmpty()) {
            throw new InvalidUidException("UID in credential is not valid");
        }

        User user = findUser.get();
       // user.setTokenIsActive(false);
        userRepo.save(user);
    }

    public User administratorSignIn(UidDto uidDto) throws InvalidUidException, UnauthorisedSignInException {
    	 Optional<User> findUser = userRepo.findById(uidDto.getUid());
        if(findUser.isEmpty()) {
            throw new InvalidUidException("UID in credential is not valid");
        }

        if(!findUser.get().getRoles().contains(ERole.ROLE_ADMIN)) {
            throw new UnauthorisedSignInException("You are not authorised to access!");
        }

        User user = findUser.get();
       // user.setTokenIsActive(true);
        return userRepo.save(user);
    }

}
