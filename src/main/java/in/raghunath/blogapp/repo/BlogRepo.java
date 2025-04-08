package in.raghunath.blogapp.repo;

import in.raghunath.blogapp.model.Blog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepo extends MongoRepository<Blog,String> {
    List<Blog> findByIsPublishedTrue();
    Optional<Blog> findByIdAndIsPublishedTrue(String id);
    List<Blog> findByIsPublishedFalse();
    List<Blog> findByAuthorUsernameAndIsPublishedTrue(String username);
    List<Blog> findByAuthorUsernameAndIsPublishedFalse(String username);
    List<Blog> findByTopicIgnoreCaseAndIsPublishedTrue(String topic);

    @Query("""
        {
            'isPublished': true,
            '$or': [
                { 'title':    { '$regex': ?0, '$options': 'i' } },
                { 'subtitle': { '$regex': ?0, '$options': 'i' } },
                { 'topic':    { '$regex': ?0, '$options': 'i' } }
            ]
        }
    """)
    List<Blog> searchPublishedBlogs(String searchTerm);
}
