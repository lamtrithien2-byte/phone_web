package com.tdtu.coreapi.user;

import com.tdtu.coreapi.common.ApiResponse;
import com.tdtu.coreapi.user.dto.CreateStaffRequest;
import com.tdtu.coreapi.user.dto.UpdateProfileRequest;
import com.tdtu.coreapi.user.dto.UserView;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ApiResponse<UserView> getProfile(@RequestParam Long userId) {
        UserView user = userService.getProfile(userId);
        if (user == null) {
            return ApiResponse.error("User not found");
        }
        return ApiResponse.success(user);
    }

    @GetMapping("/staff")
    public ApiResponse<List<UserView>> getStaffList() {
        return ApiResponse.success(userService.getStaffList());
    }

    @PostMapping("/staff")
    public ApiResponse<UserView> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        return ApiResponse.success(userService.createStaff(request));
    }

    @PutMapping("/profile")
    public ApiResponse<UserView> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.success(userService.updateProfile(request));
    }

    @PostMapping("/{userId}/toggle-status")
    public ApiResponse<List<UserView>> toggleStatus(@PathVariable Long userId) {
        return ApiResponse.success(userService.toggleStatus(userId));
    }
}
