package in.raghunath.blogapp.controller;

import in.raghunath.blogapp.model.Blog;
import in.raghunath.blogapp.model.User;
import in.raghunath.blogapp.service.BlogService;
import in.raghunath.blogapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Blog> createBlog(@RequestBody Blog blog) {
        blog.setCreatedAt(new Date()); // Automatically set the current date and time
        Blog createdBlog = blogService.createBlog(blog);
        return ResponseEntity.ok(createdBlog);
    }


    @GetMapping
    public ResponseEntity<List<Blog>> getAllBlogs() {
        return ResponseEntity.ok(blogService.getAllBlogs());
    }


    @DeleteMapping("/{Id}")
    public ResponseEntity<Void> deleteBlogById(@PathVariable String Id) {
        return ResponseEntity.ok(blogService.deleteBlogById(Id));
    }


}
