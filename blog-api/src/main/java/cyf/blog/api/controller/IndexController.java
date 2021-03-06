package cyf.blog.api.controller;

import com.github.pagehelper.PageInfo;
import cyf.blog.api.service.ContentService;
import cyf.blog.api.service.MetaService;
import cyf.blog.api.service.SiteService;
import cyf.blog.base.common.Constants;
import cyf.blog.base.enums.db.ContentStatus;
import cyf.blog.base.enums.db.MetaType;
import cyf.blog.dao.model.Contents;
import cyf.blog.dao.model.Metas;
import cyf.blog.dao.model.bo.ArchiveBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * 前端
 */
@Controller
@Slf4j
public class IndexController extends BaseController {


    @Autowired
    private ContentService contentService;
    @Autowired
    private SiteService siteService;
    @Autowired
    private MetaService metaService;



    /**
     * 首页
     *
     * @return
     */
    @GetMapping(value = "/")
    public String index(HttpServletRequest request) {
        return index(request, 1, 12);
    }

    /**
     * 首页分页
     *
     * @param request   request
     * @param pageIndex 第几页
     * @param limit     每页大小
     * @return 主页
     */
    @GetMapping(value = "/page/{pageIndex}")
    public String index(HttpServletRequest request, @PathVariable int pageIndex, @RequestParam(value = "limit", defaultValue = "12") int limit) {

        pageIndex = pageIndex < 0 || pageIndex > Constants.MAX_PAGE ? 1 : pageIndex;
        PageInfo<Contents> articles = contentService.getContents(pageIndex, limit);
        request.setAttribute("articles", articles);
        if (pageIndex > 1) {
            title(request, "第" + pageIndex + "页");
        }
        return render("index");
    }

    /**
     * 文章页
     *
     * @param request 请求
     * @param cid     文章主键
     * @return
     */
    @GetMapping(value = {"article/{cid}", "article/{cid}.html"})
    public String getArticle(HttpServletRequest request, @PathVariable String cid) {
        Contents contents = contentService.getContentsById(Integer.valueOf(cid));
        if (null == contents || Objects.equals(ContentStatus.draft.getCode(), contents.getStatus())) {
            return this.render_404();
        }
        request.setAttribute("article", contents);
        request.setAttribute("is_post", true);
       /* completeArticle(request, contents);
        */
        return this.render("post");


    }

    /**
     * 归档
     *
     * @return
     */
    @GetMapping("archives")
    public String getFile(HttpServletRequest request) {
        List<ArchiveBo> archives = siteService.getArchives();
        request.setAttribute("archives", archives);
        return render("archives");
    }

    /**
     * 友链
     *
     * @return
     */
    @GetMapping("links")
    public String getLinks(HttpServletRequest request) {
        List<Metas> links = siteService.getLinks();
        request.setAttribute("links", links);
        return render("links");
    }

    /**
     * 自定义页面,如关于的页面
     */
    @GetMapping(value = "/{pageName}")
    public String page(@PathVariable String pageName, HttpServletRequest request) {
        Contents contents = contentService.getContentByPageName(pageName);
        if (null == contents) {
            return this.render_404();
        }
        /*if (contents.getAllowComment()) {
            String cp = request.getParameter("cp");
            if (StringUtils.isBlank(cp)) {
                cp = "1";
            }
            PageInfo<CommentBo> commentsPaginator = commentService.getComments(contents.getCid(), Integer.parseInt(cp), 6);
            request.setAttribute("comments", commentsPaginator);
        }*/
        request.setAttribute("article", contents);
        return this.render("page");
    }

    /**
     * 分类页
     *
     * @return
     */
    @GetMapping(value = "category/{keyword}")
    public String categories(HttpServletRequest request, @PathVariable String keyword, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return this.categories(request, keyword, 1, limit);
    }

    @GetMapping(value = "category/{keyword}/{pageIndex}")
    public String categories(HttpServletRequest request, @PathVariable String keyword,
                             @PathVariable int pageIndex, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        pageIndex = pageIndex < 0 || pageIndex > Constants.MAX_PAGE ? 1 : pageIndex;
        List<Integer> cids = metaService.getCidsByNameAndType(MetaType.category.getCode(), keyword);
        if (CollectionUtils.isEmpty(cids)) {
            return this.render_404();
        }

        PageInfo<Contents> contents= contentService.getContentsByCids( pageIndex, limit,cids);

        request.setAttribute("articles", contents);
        request.setAttribute("type", "分类");
        request.setAttribute("keyword", keyword);

        return this.render("page-category");
    }

    /**
     * 标签页
     *
     * @return
     */
    @GetMapping(value = "tag/{keyword}")
    public String tags(HttpServletRequest request, @PathVariable String keyword, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return this.tags(request, keyword, 1, limit);
    }

    @GetMapping(value = "tag/{keyword}/{pageIndex}")
    public String tags(HttpServletRequest request, @PathVariable String keyword,
                             @PathVariable int pageIndex, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        pageIndex = pageIndex < 0 || pageIndex > Constants.MAX_PAGE ? 1 : pageIndex;

        List<Integer> cids = metaService.getCidsByNameAndType(MetaType.tag.getCode(), keyword);
        if (CollectionUtils.isEmpty(cids)) {
            return this.render_404();
        }

        PageInfo<Contents> contents = contentService.getContentsByCids( pageIndex, limit,cids);

        request.setAttribute("articles", contents);
        request.setAttribute("type", "标签");
        request.setAttribute("keyword", keyword);

        return this.render("page-category");
    }

    /**
     * 测试 update时传入Map参数
     * @param cid
     * @return
     */
    @GetMapping(value = "updateMap/{cid}")
    public String updateMap(@PathVariable Integer cid) {
        contentService.updateMap(cid);
        return "";
    }

}
