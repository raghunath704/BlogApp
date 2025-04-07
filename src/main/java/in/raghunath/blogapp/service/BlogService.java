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

    @Transactional
    public Blog createBlog(Blog blog){
        Authentication authentication= SecurityContextHolder
                .getContext()
                .getAuthentication();
        String currentPrincipalName = "anonymousUser"; // Default or throw if auth is null?
        if(authentication!=null && authentication.isAuthenticated()){
            if (authentication.getPrincipal() instanceof UserDetails) {
                currentPrincipalName =
                        ((UserDetails) authentication.getPrincipal())
                        .getUsername();
            } else currentPrincipalName = authentication.getName();
        }
        else {
            throw new IllegalStateException("User must be authenticated to create a blog.");
        }
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
