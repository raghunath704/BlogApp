package in.raghunath.blogapp.service;


import com.mongodb.lang.Nullable;
import in.raghunath.blogapp.model.Blog;
import in.raghunath.blogapp.model.User;
import in.raghunath.blogapp.repo.BlogRepo;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BlogService {

    @Autowired
    BlogRepo blogRepo;

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("User must be authenticated for this operation.");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    @Transactional
    public Blog createBlog(Blog blog){
        String currentPrincipalName = getCurrentUsername();
        blog.setAuthorUsername(currentPrincipalName);
        blog.setCreatedAt(new Date());
        blog.setUpdatedAt(new Date());
        blog.setIsPublished(true);
        return blogRepo.save(blog);

    }

    public List<Blog> getAllBlogs() {
        return blogRepo.findAll();
    }

    public List<Blog> getAllPublishedBlogs(){
        return blogRepo.findByIsPublishedTrue();
    }

    public List<Blog> findBlogsByUsername(String username){
        return blogRepo.findByAuthorUsernameAndIsPublishedTrue(username);
    }

    public Blog getBlogById(String id) {
        return blogRepo.findById(id)
                .orElseThrow(() -> new
                        ResourceNotFoundException("Blog not found with id: " + id)); // Use a custom exception
    }

    public Blog updateBlog(String id, Blog blogDetails){
        Blog existingBlog=getBlogById(id);
        existingBlog.setTitle(blogDetails.getTitle());
        existingBlog.setSubtitle(blogDetails.getSubtitle());
        existingBlog.setContent(blogDetails.getContent());
        existingBlog.setTopic(blogDetails.getTopic());
        existingBlog.setUpdatedAt(new Date());
        return blogRepo.save(existingBlog);
    }

    public void togglePublishStatus(String id){
        Blog existingBlog=getBlogById(id);
        existingBlog.setIsPublished(!existingBlog.getIsPublished());
        blogRepo.save(existingBlog);
    }

    public boolean isPublished(String id){
        Blog blog=getBlogById(id);
        return blog.getIsPublished();
    }
    public List<Blog> getMyUnpublishedBlogs() {
        String currentUsername = getCurrentUsername();
        return blogRepo.findByAuthorUsernameAndIsPublishedFalse(currentUsername);
    }
    public List<Blog> getAllUnpublishedBlogsForAdmin() {
        // Security check (role) should ideally be done at Controller level with @PreAuthorize
        return blogRepo.findByIsPublishedFalse();
    }

    public void deleteBlogById(String id) {
        if(!blogRepo.existsById(id)){
            throw new ResourceNotFoundException("Blog not found with id: "+ id);
        }
        blogRepo.deleteById(id);
    }










    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

}
