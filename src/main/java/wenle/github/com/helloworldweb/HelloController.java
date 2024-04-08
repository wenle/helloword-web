package wenle.github.com.helloworldweb;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    public String printResponseHeaders(HttpServletResponse response) {
        // 获取所有的Header名称
        Collection<String> headerNames = response.getHeaderNames();

        // 打印每个Header的名称和对应的值
        StringBuilder sb = new StringBuilder();
        for (String headerName : headerNames) {
            String headerValue = response.getHeader(headerName);
            sb.append(headerName + ": " + headerValue);
            sb.append("\n");
        }
        return sb.toString();
    }

    @GetMapping("/")
    public void index(HttpServletResponse response) {
        CookieUtil.addCookie("key", "value", response);
        response.setContentType("text/plain");               // 设置内容类型为纯文本
        response.setCharacterEncoding("UTF-8");             // 设置字符编码

        String responseContent = String.format("headers:\n%s", printResponseHeaders(response));
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