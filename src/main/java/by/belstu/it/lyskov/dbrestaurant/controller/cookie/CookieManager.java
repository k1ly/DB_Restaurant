package by.belstu.it.lyskov.dbrestaurant.controller.cookie;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CookieManager {

    public Cookie create(String name, Object value, int expiration) {
        Cookie cookie = new Cookie(name, URLEncoder.encode(new Gson().toJson(value), StandardCharsets.UTF_8));
        cookie.setMaxAge(expiration);
        return cookie;
    }

    public Cookie create(String name, Object value) {
        return new Cookie(name, URLEncoder.encode(new Gson().toJson(value), StandardCharsets.UTF_8));
    }

    public <T> T parse(String value, Class<T> resultType) {
        return new Gson().fromJson(URLDecoder.decode(value, StandardCharsets.UTF_8), resultType);
    }

    public <T> List<T> parseList(String value, T[] resultArray) {
        List<T> list = new ArrayList<>();
        for (Object o : new Gson().fromJson(URLDecoder.decode(value, StandardCharsets.UTF_8), resultArray.getClass())) {
            list.add((T) o);
        }
        return list;
    }
}
