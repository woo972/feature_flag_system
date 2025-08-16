import com.featureflag.sdk.*;
import com.featureflag.sdk.api.*;
import jakarta.annotation.*;
import org.springframework.context.annotation.*;

@Configuration
public class SampleFeatureFlagConfig {
    @PostConstruct
    public void init() {
        featureFlagClient().initialize();
    }

    @Bean
    public FeatureFlagClient featureFlagClient() {
        return SimpleFeatureFlagClient.builder()
                .cache(new DefaultFeatureFlagLocalCache())
                .provider(FeatureFlagProvider.builder().build())
                .build();
    }


}
