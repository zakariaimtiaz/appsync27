package ngo.friendship.syncapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class AppConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private AppInterceptor appInterceptor;

    @Autowired
    private BearerTokenInterceptor bearerTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // Session based interceptor
        registry.addInterceptor(appInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/resources/**",
                        "/login",
                        "/try-login",
                        "/logout",
                        "/sync/**"
                );

        // Token based interceptor
        registry.addInterceptor(bearerTokenInterceptor)
                .addPathPatterns("/sync/**");
    }
}
