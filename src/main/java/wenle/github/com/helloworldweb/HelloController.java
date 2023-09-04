package wenle.github.com.helloworldweb;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Value("${env}")
    private String env;

    @GetMapping("/")
    public String index() {
        return String.format("Hello world! Current env: %s </br> Current host: %s", env, getHostName());
    }

    public String getHostName() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostName();
        } catch (UnknownHostException e) {
        }
        return "Unknown";
    }

}