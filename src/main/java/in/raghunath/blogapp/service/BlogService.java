package in.raghunath.blogapp.service;


import com.mongodb.lang.Nullable;
import in.raghunath.blogapp.model.Blog;
import in.raghunath.blogapp.model.User;
import in.raghunath.blogapp.repo.BlogRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogService {

    @Autowired
    BlogRepo blogRepo;

    public Blog createBlog(Blog blog){
        return blogRepo.save(blog);
    }

    public List<Blog> getAllBlogs() {
        return blogRepo.findAll();
    }

    public Void deleteBlogById(String id) {
        blogRepo.deleteById(id);
        return null;
    }

}
