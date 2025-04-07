package in.raghunath.blogapp.controller;

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

    @GetMapping
    public ResponseEntity<List<Blog>> getAllPublishedBlogs() {
        return ResponseEntity.ok(blogService.getAllPublishedBlogs());
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
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @blogSecurityService.isOwner(#id, authentication.principal.username)")
    public ResponseEntity<Void> togglePublishStatus(@PathVariable String id){
        blogService.togglePublishStatus(id);
        return ResponseEntity.ok(null);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @blogSecurityService.isOwner(#id, authentication.principal.username)")
    public ResponseEntity<Void> deleteBlogById(@PathVariable String id) {
        blogService.deleteBlogById(id);
        return ResponseEntity.noContent().build(); // Return 204 No Content
    }


}
