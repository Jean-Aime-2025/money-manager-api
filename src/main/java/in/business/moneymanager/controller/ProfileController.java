package in.business.moneymanager.controller;

import in.business.moneymanager.dto.AuthDTO;
import in.business.moneymanager.dto.ProfileDTO;
import in.business.moneymanager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO) {
        ProfileDTO registerProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token) {
        boolean isActivated = profileService.activateProfile(token);
        if (isActivated) {
            return ResponseEntity.ok("Profile activated successfully 🥳");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not activated ❌");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO) {
        try {
            if(!profileService.isAccountActive(authDTO.getEmail())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        Map.of(
                                "message","Please activate your account first!"
                        )
                );
            }
            Map<String,Object> response = profileService.authenticateAndGenerateToken(authDTO);
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/test")
    public String test(){
        return "Test successful";
    }


}
