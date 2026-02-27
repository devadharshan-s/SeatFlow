package org.example.bookmyshowbookingservice.booking.client;

import org.example.bookmyshowbookingservice.common.dto.ApiResponse;
import org.example.bookmyshowbookingservice.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "userClient", url = "${services.user.base-url}", configuration = FeignClientConfig.class)
public interface UserClient {

    @GetMapping("/getUser")
    ApiResponse<Long> getUserByEmail(@RequestParam("email") String email);
}
