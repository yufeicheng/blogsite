package cyf.blog.dao.common;

import com.github.pagehelper.PageInfo;
import cyf.blog.base.common.Constants;
import cyf.blog.dao.model.Contents;
import cyf.blog.util.TextUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 前台通用配置
 *
 * @author Cheng Yufei
 * @create 2018-03-07 14:59
 **/
@Component
public class Common {

    public static String site_title() {
        return site_option("site_title");
    }

    /**
     * 返回文章链接地址
     *
     * @param contents
     * @return
     */
    public static String permalink(Contents contents) {
        return permalink(contents.getCid(), contents.getSlug());
    }

    public static String permalink(Integer cid, String slug) {
        return site_url("/article/" + (StringUtils.isNotBlank(slug) ? slug : cid.toString()));
    }

    /**
     * 返回网站链接下的全址
     *
     * @param sub 后面追加的地址
     * @return
     */
    public static String site_url(String sub) {
        return site_option("site_url") + sub;
    }

    /**
     * 网站配置项
     *
     * @param key
     * @return
     */
    public static String site_option(String key) {
        return site_option(key, "");
    }

    /**
     * 网站配置项
     *
     * @param key
     * @param defalutValue 默认值
     * @return
     */
    public static String site_option(String key, String defalutValue) {
        if (StringUtils.isBlank(key)) {
            return "";
        }
        //系统设置的map存储
//        String str = WebConst.initConfig.get(key);
        String str = "";
        return StringUtils.isNotBlank(str) ? str : defalutValue;
    }

    /**
     * 显示文章缩略图，顺序为：文章第一张图 -> 随机获取
     *
     * @return
     */
    public static String show_thumb(Contents contents) {
        int cid = contents.getCid();
        int size = cid % 20;
        size = size == 0 ? 1 : size;
        return "/user/img/rand/" + size + ".jpg";
    }

    /**
     * 显示文章图标
     *
     * @param cid
     * @return
     */
    public static String show_icon(int cid) {
        return Constants.ARTICLE_ICONS[cid % Constants.ARTICLE_ICONS.length];
    }

    /**
     * 显示分类
     *
     * @param categories
     * @return
     */
    public static String show_categories(String categories) throws UnsupportedEncodingException {
        if (StringUtils.isBlank(categories)) {
            return show_categories("默认分类");
        }
        String[] arr = categories.split(",");
        StringBuffer sbuf = new StringBuffer();
        for (String c : arr) {
            sbuf.append("<a href=\"/category/" + URLEncoder.encode(c, "UTF-8") + "\">" + c + "</a>");
        }
        return sbuf.toString();
    }

    /**
     * 显示文章内容，转换markdown为html
     *
     * @param value
     * @return
     */
    public static String article(String value) {
        if (StringUtils.isNotBlank(value)) {
            value = value.replace("<!--more-->", "\r\n");
            return TextUtil.mdToHtml(value);
        }
        return "";
    }

    /**
     * 文章页显示标签
     *
     * @param tags
     * @return
     */
    public static String show_tags(String tags) throws UnsupportedEncodingException {
        if (StringUtils.isBlank(tags)) {
            return "";
        }
        String[] arr = tags.split(",");
        StringBuffer sbuf = new StringBuffer();
        for (String c : arr) {
            sbuf.append("<a href=\"/tag/" + URLEncoder.encode(c, "UTF-8") + "\">" + c + "</a>");
        }
        return sbuf.toString();
    }

    /**
     * 判断分页中是否有数据
     *
     * @return
     */
    public static boolean is_empty(PageInfo pageInfo) {
        return pageInfo == null || (pageInfo.getList() == null) || (pageInfo.getList().size() == 0);
    }

    /**
     * 获取社交的链接地址
     *
     * @return
     */
    public static Map<String, String> social() {
        final String prefix = "social_";
        Map<String, String> map = new HashMap<>();
     /*   map.put("weibo", WebConst.initConfig.get(prefix + "weibo"));
        map.put("zhihu", WebConst.initConfig.get(prefix + "zhihu"));
        map.put("github", WebConst.initConfig.get(prefix + "github"));
        map.put("twitter", WebConst.initConfig.get(prefix + "twitter"));*/
        return map;
    }

    /**
     * 返回github头像地址
     *
     * @param email
     * @return
     */
    public static String gravatar(String email) {
        String avatarUrl = "https://github.com/identicons/";
        if (StringUtils.isBlank(email)) {
            email = "873401580@qq.com";
        }
        String hash = TextUtil.MD5encode(email.trim().toLowerCase());
        return avatarUrl + hash + ".png";
    }

    /**
     * 截取字符串
     *
     * @param str
     * @param len
     * @return
     */
    public static String substr(String str, int len) {
        if (str.length() > len) {
            return str.substring(0, len);
        }
        return str;
    }

}
