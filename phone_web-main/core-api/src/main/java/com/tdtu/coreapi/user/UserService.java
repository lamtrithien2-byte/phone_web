package com.tdtu.coreapi.user;

import com.tdtu.coreapi.user.dto.CreateStaffRequest;
import com.tdtu.coreapi.user.dto.UpdateProfileRequest;
import com.tdtu.coreapi.user.dto.UserView;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserProcedureRepository userProcedureRepository;

    public UserService(UserProcedureRepository userProcedureRepository) {
        this.userProcedureRepository = userProcedureRepository;
    }

    public UserView getProfile(Long userId) {
        return userProcedureRepository.getProfile(userId);
    }

    public List<UserView> getStaffList() {
        return userProcedureRepository.getStaffList();
    }

    public UserView createStaff(CreateStaffRequest request) {
        Long userId = userProcedureRepository.createStaff(request);
        return userProcedureRepository.getProfile(userId);
    }

    public UserView updateProfile(UpdateProfileRequest request) {
        return userProcedureRepository.updateProfile(request);
    }

    public List<UserView> toggleStatus(Long userId) {
        userProcedureRepository.toggleStatus(userId);
        return userProcedureRepository.getStaffList();
    }
}
