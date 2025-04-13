package in.raghunath.blogapp.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class ImageUploadService {

    private static final Logger logger = LoggerFactory.getLogger(ImageUploadService.class);

    private final Cloudinary cloudinary;

    public ImageUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Uploads an image file to Cloudinary.
     *
     * @param file The MultipartFile representing the image.
     * @param folderName Optional: The folder name in Cloudinary (e.g., "profile_photos", "blog_images").
     * @return A Map containing upload details (e.g., "url", "public_id").
     * @throws IOException If an error occurs during upload.
     */
    public Map upload(MultipartFile file, String folderName) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Invalid file type. Only images are allowed.");
        }

        // Generate a unique public ID or use the original filename (carefully!)
        // Using UUID is safer to avoid overwrites and naming conflicts
        String publicId = (folderName != null ? folderName + "/" : "") + UUID.randomUUID().toString();

        logger.info("Uploading file '{}' to Cloudinary with public_id '{}'", file.getOriginalFilename(), publicId);

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId, // Explicitly set public_id
                            "overwrite", true // Or false, depending on your needs
                            // Add more options like transformations, tags etc. here
                            // "folder", folderName // Alternative way to set folder
                    ));

            logger.info("Successfully uploaded file '{}'. URL: {}", file.getOriginalFilename(), uploadResult.get("url"));
            // You typically need 'secure_url' for HTTPS and 'public_id' to manage the asset later (e.g., deletion)
            return uploadResult;

        } catch (IOException e) {
            logger.error("Failed to upload file '{}' to Cloudinary", file.getOriginalFilename(), e);
            throw new IOException("Failed to upload image to Cloudinary.", e);
        }
    }

    /**
     * Deletes an image from Cloudinary using its public ID.
     *
     * @param publicId The public ID of the image to delete.
     * @return A Map containing the deletion result.
     * @throws IOException If an error occurs during deletion.
     */
    public Map delete(String publicId) throws IOException {
        logger.info("Deleting image from Cloudinary with public_id '{}'", publicId);
        try {
            Map result = this.cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            logger.info("Successfully deleted image with public_id '{}'. Result: {}", publicId, result);
            return result;
        } catch (IOException e) {
            logger.error("Failed to delete image with public_id '{}' from Cloudinary", publicId, e);
            throw new IOException("Failed to delete image from Cloudinary.", e);
        }

    }
}