package wenle.github.com.helloworldweb;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CookieUtil {    /**
 * 日志记录器
 */
private static Logger logger = LogManager.getLogger();

    /**
     * 只在Chrome浏览器中增加SameSite头字段
     */
    public static final String USE_SAME_SITE_ONLY_AFTER_CHROME_80 = "useChrome80";

    /**
     * 根Path
     */
    public static final String ROOT_PATH = "/";

    /**
     * US locale - all HTTP dates are in english
     */
    public final static Locale LOCALE_US = Locale.US;

    /**
     * Pattern used for old cookies
     */
    public final static String OLD_COOKIE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss z";

    //
    // from RFC 2068, token special case characters
    //
    private static final String COOKIE_SPECIAL_CHARS = "()<>@,;:\\\"/[]?={} \t";

    private static boolean characterCheckFlag[] = new boolean[127];

    static {
        for (int i = 0; i < COOKIE_SPECIAL_CHARS.length(); i++) {
            characterCheckFlag[COOKIE_SPECIAL_CHARS.charAt(i)] = true;
        }
    }

    /**
     * 获取 Cookie值
     *
     * @param key     cookie 名称
     * @param request 请求对象
     * @return cookie 值
     */
    public static String getCookieValue(String key, HttpServletRequest request) {
        Cookie cookie = getCookie(key, request);
        if (cookie == null) {
            return null;
        }
        return cookie.getValue();
    }


    /**
     * 获取 Cookie
     *
     * @param key     cookie 名称
     * @param request 请求对象
     * @return cookie 对象
     */
    public static Cookie getCookie(String key, HttpServletRequest request) {
        if (request == null || StringUtils.isBlank(key)) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        Cookie value = null;

        for (Cookie c : cookies) {
            if (key.equals(c.getName())) {
                value = c;
                break;
            }
        }
        return value;
    }

    /**
     * 增加 cookie 键值对
     *
     * @param key      cookie 名
     * @param value    cookie 值
     * @param response 响应
     */
    public static void addCookie(String key, String value, HttpServletResponse response) {
        setCookie(key, value, -1, null, null, response);
    }

    /**
     * 增加 cookie 键值对
     *
     * @param key      cookie 名
     * @param value    cookie 值
     * @param httpOnly 是否仅http，禁止脚本访问Cookie
     * @param response 响应
     */
    public static void addCookie(String key, String value, final boolean httpOnly, HttpServletResponse response) {
        setCookie(key, value, -1, null, null, httpOnly, response);
    }

    /**
     * 增加 cookie 键值对
     *
     * @param key      cookie 名
     * @param value    cookie 值
     * @param httpOnly 是否仅http，禁止脚本访问Cookie
     * @param response 响应
     */
    public static void addCookie(String key, String value, final boolean httpOnly, final boolean secure, HttpServletResponse response) {
        setCookie(key, value, -1, null, null, httpOnly, secure, response);
    }

    /**
     * 增加 cookie 键值对
     *
     * @param key      cookie 名
     * @param value    cookie 值
     * @param maxAge   最大存活时间
     * @param response 响应
     */
    public static void addCookie(String key, String value, int maxAge, HttpServletResponse response) {
        setCookie(key, value, maxAge, null, null, response);
    }

    /**
     * 增加 cookie 键值对
     *
     * @param key      cookie 名
     * @param value    cookie 值
     * @param maxAge   最大存活时间
     * @param httpOnly 是否仅http，禁止脚本访问Cookie
     * @param response 响应
     */
    public static void addCookie(String key, String value, int maxAge, final boolean httpOnly, HttpServletResponse response) {
        setCookie(key, value, maxAge, null, null, httpOnly, response);
    }

    /**
     * 增加 cookie 键值对
     *
     * @param key      cookie 名
     * @param value    cookie 值
     * @param maxAge   最大存活时间
     * @param httpOnly 是否仅http，禁止脚本访问Cookie
     * @param secure   是否是SSL/HTTPS
     * @param response 响应
     */
    public static void addCookie(String key, String value, int maxAge, final boolean httpOnly, final boolean secure, HttpServletResponse response) {
        setCookie(key, value, maxAge, null, null, httpOnly, secure, response);
    }

    /**
     * 增加 cookie 键值对
     *
     * @param key                 cookie 名
     * @param value               cookie 值
     * @param maxAge              最大存活时间
     * @param httpOnly            是否仅http，禁止脚本访问Cookie
     * @param secure              是否是SSL/HTTPS
     * @param response            响应
     * @param request             请求
     * @param cookieSameSiteValue Cookie的同站属性值
     * @param checkSameSiteRegex  检测是否同站的正则表达式
     */
    @Deprecated
    public static void addCookie(String key, String value, int maxAge, final boolean httpOnly, final boolean secure,
        HttpServletResponse response, HttpServletRequest request, String cookieSameSiteValue, String checkSameSiteRegex) {
        setCookie(key, value, maxAge, null, null, httpOnly, secure, request, response, cookieSameSiteValue, checkSameSiteRegex, null);
    }

    /**
     * 增加 cookie 键值对
     *
     * @param key      cookie 名
     * @param value    cookie 值
     * @param maxAge   最大存活时间
     * @param httpOnly 是否仅http，禁止脚本访问Cookie
     * @param secure   是否是SSL/HTTPS
     * @param priority 优先级
     * @param response 响应
     */
    public static void addCookie(String key, String value, int maxAge, final boolean httpOnly, final boolean secure, final String priority, HttpServletResponse response) {
        setCookie(key, value, maxAge, null, null, httpOnly, secure, priority, response);
    }

    /**
     * 增加 cookie 键值对
     *
     * @param key                 cookie 名
     * @param value               cookie 值
     * @param maxAge              最大存活时间
     * @param httpOnly            是否仅http，禁止脚本访问Cookie
     * @param secure              是否是SSL/HTTPS
     * @param response            响应
     * @param request             请求
     * @param cookieSameSiteValue Cookie的SameSite同站属性值
     * @param checkSameSiteRegex  检测是否同站的正则表达式
     */
    @Deprecated
    public static void addCookie(String key, String value, int maxAge, final boolean httpOnly, final boolean secure, final String priority,
        HttpServletResponse response, HttpServletRequest request, String cookieSameSiteValue, String checkSameSiteRegex) {
        setCookie(key, value, maxAge, null, null, httpOnly, secure, priority, response, request, cookieSameSiteValue, checkSameSiteRegex, null);
    }

    /**
     * 增加 cookie 键值对
     *
     * @param key        cookie 名
     * @param value      cookie 值
     * @param maxAge     最大存活时间
     * @param path       路径
     * @param domainName 域名
     * @param response   响应
     */
    public static void addCookie(String key, String value, int maxAge, String path, String domainName, HttpServletResponse response) {
        setCookie(key, value, maxAge, path, domainName, response);
    }

    /**
     * 增加 cookie 键值对
     *
     * @param key        cookie 名
     * @param value      cookie 值
     * @param maxAge     最大存活时间
     * @param path       路径
     * @param domainName 域名
     * @param httpOnly   是否仅http，禁止脚本访问Cookie
     * @param response   响应
     */
    public static void addCookie(String key, String value, int maxAge, String path, String domainName, final boolean httpOnly, HttpServletResponse response) {
        setCookie(key, value, maxAge, path, domainName, httpOnly, response);
    }

    /**
     * 增加 cookie 键值对
     *
     * @param key        cookie 名
     * @param value      cookie 值
     * @param maxAge     最大存活时间
     * @param path       路径
     * @param domainName 域名
     * @param httpOnly   是否仅http，禁止脚本访问Cookie
     * @param secure     SSL/HTTPS
     * @param response   响应
     */
    public static void addCookie(String key, String value, int maxAge, String path, String domainName, final boolean httpOnly, final boolean secure, HttpServletResponse response) {
        setCookie(key, value, maxAge, path, domainName, httpOnly, secure, response);
    }

    /**
     * 增加 cookie 键值对
     *
     * @param key                 cookie 名
     * @param value               cookie 值
     * @param maxAge              最大存活时间
     * @param httpOnly            是否仅http，禁止脚本访问Cookie
     * @param secure              是否是SSL/HTTPS
     * @param response            响应
     * @param cookieSameSiteValue Cookie的SameSite同站属性值
     * @param checkSameSiteRegex  检测是否同站的正则表达式
     */
    @Deprecated
    public static void addCookie(String key, String value, int maxAge, String path, String domainName, final boolean httpOnly,
        final boolean secure, HttpServletRequest request, HttpServletResponse response, String cookieSameSiteValue, String checkSameSiteRegex) {
        setCookie(key, value, maxAge, path, domainName, httpOnly, secure, request, response, cookieSameSiteValue, checkSameSiteRegex, null);
    }

    /**
     * 增加 cookie 键值对
     *
     * @param key                 cookie 名
     * @param value               cookie 值
     * @param maxAgeSeconds       最大存活时间毫秒数
     * @param httpOnly            是否仅http，禁止脚本访问Cookie
     * @param secure              是否是SSL/HTTPS
     * @param response            响应
     * @param cookieSameSiteValue Cookie的同站属性值
     * @param checkSameSiteRegex  检测是否同站的正则表达式
     */
    public static void addCookie(
        String key,
        String value,
        long maxAgeSeconds,
        String path,
        String domainName,
        final boolean httpOnly,
        final boolean secure,
        HttpServletRequest request,
        HttpServletResponse response,
        String cookieSameSiteValue,
        String checkSameSiteRegex,
        String checkUnSameSiteRegex
    ) {
        setCookie(
            key,
            value,
            maxAgeSeconds,
            path,
            domainName,
            httpOnly,
            secure,
            request,
            response,
            cookieSameSiteValue,
            checkSameSiteRegex,
            checkUnSameSiteRegex
        );
    }

    /**
     * 移除 Cookie
     *
     * @param request  请求
     * @param response 响应
     * @param key      cookie 名
     */
    public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String key) {
        removeCookie(request, response, key, null);
    }

    /**
     * 移除 Cookie
     *
     * @param request  请求
     * @param response 响应
     * @param key      cookie 名
     * @param domain   域名
     */
    public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String key, String domain) {
        removeCookie(request, response, key, null, domain);
    }

    /**
     * 移除 Cookie
     *
     * @param key        cookie 名
     * @param path       路径
     * @param domainName 域名
     * @param response   响应
     */
    public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String key, String path, String domainName) {
        setCookie(key, StringUtils.EMPTY, 0, path, domainName, true, false, response);
    }

    /**
     * 移除Cookie
     *
     * @param request              请求
     * @param response             响应
     * @param key                  Cookie名
     * @param path                 Cookie路径
     * @param domainName           域名
     * @param cookieSameSiteValue  Cookie的SameSite值
     * @param checkSameSiteRegex   检查是否是SameSite的正则表达式
     * @param secure               是否是https secure
     * @param checkUnSameSiteRegex 检查是否不是SameSite的正则表达式
     */
    public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String key, String path, String domainName, String cookieSameSiteValue,
        String checkSameSiteRegex, boolean secure, String checkUnSameSiteRegex) {
        setCookie(key, StringUtils.EMPTY, 0, path, domainName, false, secure, request, response, cookieSameSiteValue, checkSameSiteRegex, checkUnSameSiteRegex);
    }

    /**
     * 设置Cookie
     *
     * @param key        cookie 名
     * @param value      cookie 值
     * @param maxAge     最大存活时间
     * @param path       路径
     * @param domainName 域名
     * @param response   响应
     */
    private static void setCookie(String key, String value, int maxAge, String path, String domainName, HttpServletResponse response) {
        setCookie(key, value, maxAge, path, domainName, false, true, response);
    }

    /**
     * 设置Cookie
     *
     * @param key        cookie 名
     * @param value      cookie 值
     * @param maxAge     最大存活时间
     * @param path       路径
     * @param domainName 域名
     * @param httpOnly   是否仅http，禁止脚本访问Cookie
     * @param response   响应
     */
    private static void setCookie(String key, String value, int maxAge, String path, String domainName, final boolean httpOnly, HttpServletResponse response) {
        setCookie(key, value, maxAge, path, domainName, httpOnly, false, response);
    }

    /**
     * 设置Cookie
     *
     * @param key        cookie 名
     * @param value      cookie 值
     * @param maxAge     最大存活时间
     * @param path       路径
     * @param domainName 域名
     * @param httpOnly   是否仅http，禁止脚本访问Cookie
     * @param secure     SSL/HTTPS
     * @param response   响应
     */
    private static void setCookie(String key, String value, int maxAge, String path, String domainName, final boolean httpOnly, final boolean secure, HttpServletResponse response) {
        setCookie(key, value, maxAge, path, domainName, httpOnly, secure, null, response);
    }

    /**
     * 增加 cookie 键值对
     *
     * @param key           cookie 名
     * @param value         cookie 值
     * @param maxAgeSeconds 最大存活时间秒数
     * @param path          路径
     * @param domainName    域名
     * @param httpOnly      是否仅http，禁止脚本访问Cookie
     * @param secure        是否是SSL/HTTPS
     * @param response      响应
     */
    private static void setCookie(
        String key, String value,
        long maxAgeSeconds,
        String path,
        String domainName,
        final boolean httpOnly,
        final boolean secure,
        HttpServletRequest request,
        HttpServletResponse response,
        String cookieSameSiteValue,
        String checkSameSiteRegex,
        String checkUnSameSiteRegex
    ) {
        setCookie(
            key,
            value,
            maxAgeSeconds,
            path,
            domainName,
            httpOnly,
            secure,
            null,
            response,
            request,
            cookieSameSiteValue,
            checkSameSiteRegex,
            checkUnSameSiteRegex
        );
    }

    /**
     * 增加 cookie 键值对
     *
     * @param key        cookie 名
     * @param value      cookie 值
     * @param maxAge     最大存活时间
     * @param path       路径
     * @param domainName 域名
     * @param httpOnly   是否仅http，禁止脚本访问Cookie
     * @param secure     是否是SSL/HTTPS
     * @param priority   优先级
     * @param response   响应
     */
    private static void setCookie(String key, String value, int maxAge, String path, String domainName,
        final boolean httpOnly, final boolean secure, final String priority,
        HttpServletResponse response) {
        setCookie(key, value, maxAge, path, domainName, httpOnly, secure, priority, response, null, "None", null, null);
    }


    /**
     * 增加 cookie 键值对
     *
     * @param key                  cookie 名
     * @param value                cookie 值
     * @param maxAgeSeconds        最大存活时间秒数
     * @param path                 路径
     * @param domainName           域名
     * @param httpOnly             是否仅http，禁止脚本访问Cookie
     * @param secure               是否是SSL/HTTPS
     * @param priority             优先级
     * @param response             响应
     * @param request              请求
     * @param cookieSameSiteValue  同站点值
     * @param checkSameSiteRegex   检查同站点的正则表达式
     * @param checkUnSameSiteRegex 检查不同站点的正则表达式
     */
    private static void setCookie(
        String key,
        String value,
        long maxAgeSeconds,
        String path,
        String domainName,
        final boolean httpOnly,
        boolean secure,
        final String priority,
        HttpServletResponse response,
        HttpServletRequest request,
        String cookieSameSiteValue,
        String checkSameSiteRegex,
        String checkUnSameSiteRegex
    ) {
        if (response != null) {
            Cookie cookie = new Cookie(key, value);
            cookie.setMaxAge((int) maxAgeSeconds);
            if (StringUtils.isNotBlank(path)) {
                cookie.setPath(path);
            } else {
                cookie.setPath(ROOT_PATH);
            }
            if (StringUtils.isNotBlank(domainName)) {
                cookie.setDomain(domainName);
            }

            cookie.setVersion(0);
            cookie.setSecure(secure);
            if (httpOnly || StringUtils.isNotBlank(priority) || secure) {
                final StringBuffer buf = new StringBuffer();
                getCookieHeaderValue(cookie, buf, httpOnly, priority);
                String cookieValue = buf.toString();
                try {
                    cookieValue = appendSameSite(request, cookieValue, cookieSameSiteValue, checkSameSiteRegex, checkUnSameSiteRegex);
                } catch (Exception e) {
                    logger.error("appendSameSite error", e);
                }
                response.addHeader(getCookieHeaderName(cookie), cookieValue);
            } else {
                response.addCookie(cookie);
            }
        }
    }



    /**
     * 判断是否需要添加 SameSite=None
     *
     * @param cookieHeader 示例：USER_COOKIE=DE719787; Path=/; Secure; HttpOnly
     */
    public static String appendSameSite(HttpServletRequest request, String cookieHeader, String cookieSameSiteValue, String checkSameSiteRegex, String checkUnSameSiteRegex) {
        if (request == null || StringUtils.isBlank(cookieHeader)) {
            logger.debug("request={},cookieHeader={}", request, cookieHeader);
            cookieHeader = String.format("%s=%s","SameSite", "None");
            return cookieHeader;
        }

        // 如果已经存在SameSite属性，不添加
        if (containsSameSite(cookieHeader)) {
            logger.debug("exist sameSite {}", cookieHeader);
            return cookieHeader;
        }
            cookieHeader = String.format("%s; %s=%s", cookieHeader, "SameSite", "None");

        return cookieHeader;
    }
    private static Pattern isIosVersionPattern = Pattern.compile("\\(iP.+; CPU .*OS (\\d+)[_\\d]*.*\\) AppleWebKit/");
    private static Pattern isMacosxVersionPattern = Pattern.compile("\\(Macintosh;.*Mac OS X (\\d+)_(\\d+)[_\\d]*.*\\) AppleWebKit/");
    private static Pattern isSafariPattern = Pattern.compile("Version/.* Safari/");
    private static Pattern isMacEmbeddedBrowserPattern = Pattern.compile("^Mozilla/[.\\d]+ \\(Macintosh;.*Mac OS X [_\\d]+\\) AppleWebKit/[.\\d]+ \\(KHTML, like Gecko\\)$");
    private static Pattern isChromiumBasedPattern = Pattern.compile("Chrom(e|ium)");
    private static Pattern isChromiumVersionAtLeastPattern = Pattern.compile("Chrom[^ /]+/(\\d+)[.\\d]* ");
    private static Pattern isUcBrowserVersionAtLeastPattern = Pattern.compile("UBrowser/(\\d+)\\.(\\d+)\\.(\\d+)[.\\d]* ");

    public static boolean shouldSendSameSiteNone(String useragent) {
        if (StringUtils.isBlank(useragent)) {
            return false;
        } else {
            return !isSameSiteNoneIncompatible(useragent);
        }
    }

    public static boolean isSameSiteNoneIncompatible(String useragent) {
        if (StringUtils.isBlank(useragent)) {
            return false;
        } else {
            return hasWebKitSameSiteBug(useragent) || dropsUnrecognizedSameSiteCookies(useragent);
        }
    }

    public static boolean hasWebKitSameSiteBug(String useragent) {
        if (StringUtils.isBlank(useragent)) {
            return false;
        } else {
            return isIosVersion(12, useragent) || isMacosxVersion(10, 14, useragent) && (isSafari(useragent) || isMacEmbeddedBrowser(useragent));
        }
    }

    public static boolean dropsUnrecognizedSameSiteCookies(String useragent) {
        if (isUcBrowser(useragent)) {
            return !isUcBrowserVersionAtLeast(12, 13, 2, useragent);
        } else {
            return isChromiumBased(useragent) && isChromiumVersionAtLeast(51, useragent) && !isChromiumVersionAtLeast(67, useragent);
        }
    }

    public static boolean isIosVersion(int major, String useragent) {
        if (StringUtils.isBlank(useragent)) {
            return false;
        } else {
            Matcher m = isIosVersionPattern.matcher(useragent);
            return !m.find() ? false : String.valueOf(major).equals(m.group(1));
        }
    }

    public static boolean isMacosxVersion(int major, int minor, String useragent) {
        if (StringUtils.isBlank(useragent)) {
            return false;
        } else {
            Matcher m = isMacosxVersionPattern.matcher(useragent);
            if (!m.find()) {
                return false;
            } else {
                return String.valueOf(major).equals(m.group(1)) && String.valueOf(minor).equals(m.group(2));
            }
        }
    }

    public static boolean isSafari(String useragent) {
        if (StringUtils.isBlank(useragent)) {
            return false;
        } else {
            Matcher m = isSafariPattern.matcher(useragent);
            return m.find();
        }
    }

    public static boolean isMacEmbeddedBrowser(String useragent) {
        if (StringUtils.isBlank(useragent)) {
            return false;
        } else {
            Matcher m = isMacEmbeddedBrowserPattern.matcher(useragent);
            return m.find();
        }
    }

    public static boolean isChromiumBased(String useragent) {
        if (StringUtils.isBlank(useragent)) {
            return false;
        } else {
            Matcher m = isChromiumBasedPattern.matcher(useragent);
            return m.find();
        }
    }

    public static boolean isUcBrowser(String useragent) {
        if (StringUtils.isBlank(useragent)) {
            return false;
        } else {
            String regex = "UBrowser/";
            return useragent.contains(regex);
        }
    }

    public static boolean isUcBrowserVersionAtLeast(int major, int minor, int build, String useragent) {
        if (StringUtils.isBlank(useragent)) {
            return false;
        } else {
            Matcher m = isUcBrowserVersionAtLeastPattern.matcher(useragent);
            if (!m.find()) {
                return false;
            } else {
                int major_version = Integer.valueOf(m.group(1));
                int minor_version = Integer.valueOf(m.group(2));
                int build_version = Integer.valueOf(m.group(3));
                if (major_version != major) {
                    return major_version > major;
                } else if (minor_version != minor) {
                    return minor_version > minor;
                } else {
                    return build_version >= build;
                }
            }
        }
    }

    public static boolean isSameSiteByRegex(String userAgent, String regex) {
        if (!StringUtils.isBlank(userAgent) && !StringUtils.isBlank(regex)) {
            Pattern r = Pattern.compile(regex);
            Matcher m = r.matcher(userAgent);
            return m.find();
        } else {
            return false;
        }
    }

    public static boolean isChromium80AtLeast(String useragent) {
        return isChromiumVersionAtLeast(80, useragent);
    }

    public static boolean isChromiumVersionAtLeast(int major, String useragent) {
        if (StringUtils.isBlank(useragent)) {
            return false;
        } else {
            Matcher m = isChromiumVersionAtLeastPattern.matcher(useragent);
            if (!m.find()) {
                return false;
            } else {
                int version = Integer.valueOf(m.group(1));
                return version >= major;
            }
        }
    }

    /**
     * 是否包含Secure
     *
     * @param cookieHeader 示例：USER_COOKIE=DE719787; Path=/; Secure; HttpOnly
     * @return true or false
     */
    public static boolean containsSecure(String cookieHeader) {
        return containsAttribute(cookieHeader, "secure");
    }

    /**
     * 是否包含SameSite
     *
     * @param cookieHeader 示例：USER_COOKIE=DE719787; Path=/; Secure; HttpOnly
     * @return true or false
     */
    public static boolean containsSameSite(String cookieHeader) {
        return containsAttribute(cookieHeader, "samesite");
    }

    /**
     * 判断 cookie header 是否包含某个属性
     *
     * @param cookieHeader 示例：USER_COOKIE=DE719787; Path=/; Secure; HttpOnly
     * @param attribute    Cookie 属性，必须全部小写。例如：secure、samesite
     * @return true or false
     */
    private static boolean containsAttribute(String cookieHeader, String attribute) {
        int index = cookieHeader.indexOf(";");
        if (index < 0) {
            return false;
        }
        cookieHeader = cookieHeader.substring(index).toLowerCase();
        return cookieHeader.contains(attribute);
    }

    /**
     * 获取 Cookie Http 响应头的名字
     *
     * @param cookie
     * @return
     */
    private static String getCookieHeaderName(final Cookie cookie) {
        final int version = cookie.getVersion();
        if (version == 1) {
            return "Set-Cookie2";
        } else {
            return "Set-Cookie";
        }
    }

    /**
     * 获取CookieHeader字符串
     *
     * @param cookie Cookie对象
     * @return CookieHeader字符串
     */
    public static String getCookieHeaderValue(final Cookie cookie) {
        StringBuffer buffer = new StringBuffer();
        getCookieHeaderValue(cookie, buffer, cookie.isHttpOnly(), null);
        return buffer.toString();
    }

    /**
     * 将 Cookie 对象写到 StringBuffer 中。
     *
     * @param cookie   Cookie对象
     * @param buf      容器
     * @param httpOnly 是否仅页面可以使用，禁止脚本使用
     * @param priority 优先级
     */
    private static void getCookieHeaderValue(final Cookie cookie, final StringBuffer buf,
        final boolean httpOnly, final String priority) {
        final int version = cookie.getVersion();

        // this part is the same for all cookies

        String name = cookie.getName(); // Avoid NPE on malformed cookies
        if (name == null) {
            name = "";
        }
        String value = cookie.getValue();
        if (value == null) {
            value = "";
        }

        buf.append(name);
        buf.append("=");

        maybeQuote(version, buf, value);

        // add version 1 specific information
        if (version == 1) {
            // Version=1 ... required
            buf.append("; Version=1");

            // Comment=comment
            if (cookie.getComment() != null) {
                buf.append("; Comment=");
                maybeQuote(version, buf, cookie.getComment());
            }
        }

        // add domain information, if present

        if (cookie.getDomain() != null) {
            buf.append("; Domain=");
            maybeQuote(version, buf, cookie.getDomain());
        }

        // Max-Age=secs/Discard ... or use old "Expires" format
        if (cookie.getMaxAge() >= 0) {
            if (version == 0) {
                buf.append("; Expires=");
                SimpleDateFormat dateFormat = new SimpleDateFormat(OLD_COOKIE_PATTERN, LOCALE_US);
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT")); //必须使用GMT模式
                if (cookie.getMaxAge() == 0) {
                    dateFormat.format(new Date(10_000),
                        buf, new FieldPosition(0));
                } else {
                    dateFormat.format(new Date(System
                        .currentTimeMillis()
                        + cookie.getMaxAge() * 1000L), buf, new FieldPosition(0));
                }
            } else {
                buf.append("; Max-Age=");
                buf.append(cookie.getMaxAge());
            }
        } else if (version == 1) {
            buf.append("; Discard");
        }

        // Path=path
        if (cookie.getPath() != null) {
            buf.append("; Path=");
            maybeQuote(version, buf, cookie.getPath());
        }

        // Secure
        if (cookie.getSecure()) {
            buf.append("; Secure");
        }

        // HttpOnly
        if (httpOnly) {
            buf.append("; HttpOnly");
        }

        // Priority
        if (StringUtils.isNotBlank(priority)) {
            buf.append("; Priority=" + priority);
        }
    }

    /**
     * 引用一个Cookie的值
     *
     * @param value
     * @return
     */
    public static String quote(String value) {
        if (value == null) {
            return value;
        }

        boolean quote = false;
        for (char ch : value.toCharArray()) {
            if (ch < 127 && characterCheckFlag[ch]) {
                quote = true;
                break;
            }
        }

        if (quote) {
            return '"' + value + '"';
        }

        return value;

    }

    /**
     * 值是否需要引号引起来
     *
     * @param version 版本
     * @param buf     字符容器对象
     * @param value   值
     */
    private static void maybeQuote(final int version, final StringBuffer buf, final String value) {
        if (version == 0 || isToken(value)) {
            buf.append(value);
        } else {
            buf.append('"');
            buf.append(value);
            buf.append('"');
        }
    }

    /**
     * 字符串是否是一个ASCII token
     * Return true if the string counts as an HTTP/1.1 "token".
     */
    private static boolean isToken(final String value) {
        final int len = value.length();
        char c;
        final char[] charArray = value.toCharArray();
        for (int i = 0; i < len; i++) {
            c = charArray[i];
            if (c < 0x20 || c >= 0x7f) {
                return false;
            } else {
                if (characterCheckFlag[c]) {
                    return false;
                }
            }
        }
        return true;
    }


}
