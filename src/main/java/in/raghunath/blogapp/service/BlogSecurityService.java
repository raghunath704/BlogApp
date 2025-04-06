package in.raghunath.blogapp.service;

import in.raghunath.blogapp.model.Blog;
import in.raghunath.blogapp.repo.BlogRepo;
import org.slf4j.Logger; // For logging
import org.slf4j.LoggerFactory; // For logging
import org.springframework.stereotype.Service;

@Service("blogSecurityService")
public class BlogSecurityService {

    private static final Logger log = LoggerFactory.getLogger(BlogSecurityService.class);
    private final BlogRepo blogRepository;
    public BlogSecurityService(BlogRepo blogRepository) {
        this.blogRepository = blogRepository;
    }

    /**
     * Checks if the authenticated user is the owner of the blog.
     * @param blogId The ID of the blog to check.
     * @param username The username of the currently authenticated user.
     * @return true if the user is the owner, false otherwise.
     * @throws BlogService.ResourceNotFoundException if the blog doesn't exist (reuse exception or create a specific one).
     */
    public boolean isOwner(String blogId, String username) {
        log.debug("Checking ownership for blogId: {}, user: {}", blogId, username);

        if (username == null || blogId == null) {
            log.warn("Username or BlogId is null during ownership check.");
            return false;
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> {
                    log.warn("Blog not found with id: {} for ownership check", blogId);
                    return new BlogService.ResourceNotFoundException("Blog not found with id: " + blogId + " for ownership check");
                });

        String ownerUsername = blog.getAuthorUsername();
        log.debug("Blog author username from DB: {}", ownerUsername);
        boolean isOwner = ownerUsername != null && ownerUsername.equalsIgnoreCase(username);
        log.debug("Ownership check result for blogId {}: {}", blogId, isOwner);
        return isOwner;
    }
}