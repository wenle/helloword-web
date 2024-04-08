package wenle.github.com.helloworldweb;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public void index(HttpServletResponse response) {
        CookieUtil.addCookie("key", "value", response);
        String header = response.getHeader("Set-Cookie2");
        String responseContent = String.format("Set-Cookie2 header: %s", header);
        response.setContentType("text/plain");               // 设置内容类型为纯文本
        response.setCharacterEncoding("UTF-8");             // 设置字符编码

        try (PrintWriter writer = response.getWriter()) {
            writer.write(responseContent);                  // 写入响应内容
            writer.flush();                                 // 清空缓冲，完成响应内容的发送
        } catch (IOException e) {
            // 异常处理逻辑
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Test success!";
    }

}