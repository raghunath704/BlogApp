package in.raghunath.blogapp.controller;

import in.raghunath.blogapp.DTO.ApiResponse;
import in.raghunath.blogapp.model.Blog;
import in.raghunath.blogapp.model.User;
import in.raghunath.blogapp.repo.BlogRepo;
import in.raghunath.blogapp.service.BlogService;
import in.raghunath.blogapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {


    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Blog> createBlog(@RequestBody Blog blog) {
        Blog createdBlog = blogService.createBlog(blog);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBlog);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Blog>> getAllBlogs(){
        return ResponseEntity.ok(blogService.getAllBlogs());
    }
    @GetMapping("/unpublished/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Blog>> getAllUnpublishedBlogs() {
        List<Blog> unpublishedBlogs = blogService.getAllUnpublishedBlogsForAdmin();
        return ResponseEntity.ok(unpublishedBlogs);
    }

    @GetMapping("/unpublished/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Blog>> getMyUnpublishedBlogs() {
        List<Blog> myUnpublishedBlogs = blogService.getMyUnpublishedBlogs();
        return ResponseEntity.ok(myUnpublishedBlogs);
    }


    @GetMapping("/user/{username}")
    public ResponseEntity<List<Blog>> getAllPublishedBlogsByUsername(@PathVariable String username){ // Use @PathVariable
        List<Blog> blogs = blogService.findBlogsByUsername(username);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping
    public ResponseEntity<List<Blog>> getAllPublishedBlogs() {
        return ResponseEntity.ok(blogService.getAllPublishedBlogs());
    }
    @GetMapping("/topic/{topic}")
    public ResponseEntity<List<Blog>> getPublishedBlogsByTopic(@PathVariable String topic){
        List<Blog> blogs=blogService.findPublishedBlogsByTopic(topic);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping(params = "search")
    public ResponseEntity<List<Blog>> searchPublishedBlogs(@RequestParam("search") String searchQuery) {
        List<Blog> blogs = blogService.searchPublishedBlogs(searchQuery);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Blog> getBlogById(@PathVariable String id) {
        Blog blog = blogService.getBlogById(id);
        return ResponseEntity.ok(blog);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @blogSecurityService.isOwner(#id, authentication.principal.username)")
    public ResponseEntity<Blog> updateBlog(@PathVariable String id, @RequestBody Blog blogDetails) {
        Blog updatedBlog = blogService.updateBlog(id, blogDetails);
        return ResponseEntity.ok(updatedBlog);
    }
    @PutMapping("/toggleStatus/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @blogSecurityService.isOwner(#id, principal.username)")
    public ResponseEntity<ApiResponse> togglePublishStatus(@PathVariable String id) {
        blogService.togglePublishStatus(id);
        String finalStatus = blogService.isPublished(id) ? "PUBLISHED" : "UNPUBLISHED";
        String successMessage = "Blog status successfully toggled to " + finalStatus + ".";
        ApiResponse response = new ApiResponse(true, successMessage);

        return ResponseEntity.ok(response); // Return 200 OK with ApiResponse body
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @blogSecurityService.isOwner(#id, authentication.principal.username)")
    public ResponseEntity<Void> deleteBlogById(@PathVariable String id) {
        blogService.deleteBlogById(id);
        return ResponseEntity.noContent().build(); // Return 204 No Content
    }


}
