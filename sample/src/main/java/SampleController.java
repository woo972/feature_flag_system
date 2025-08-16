import com.featureflag.sdk.api.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sample")
public class SampleController {
    private final FeatureFlagClient featureFlagClient;

    @GetMapping("/hello")
    public void hello(
            @RequestParam("name") String name
    ) {
        featureFlagClient.initialize();
    }

    public void flagTest(){
        var isEnabled = featureFlagClient.evaluate("feature-1", Map.of("name", "test"));
        if(isEnabled){
            log.info("feature-1 is enabled");
        }else{
            log.info("feature-1 is disabled");
        }
    }
}
