package es.dawgroup17.nexgym.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import es.dawgroup17.nexgym.model.Image;
import es.dawgroup17.nexgym.repository.ImageRepository;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public Image getImage(long id) {
        return imageRepository.findById(id).orElseThrow();
    }

    // New method is been added for solving databaseinitializer issues. The problem
    // was an immage was saved two times
    public Image buildImage(InputStream inputStream) throws IOException {
        Image image = new Image();
        try {
            image.setImage(new SerialBlob(inputStream.readAllBytes()));
        } catch (Exception e) {
            throw new IOException("Failed to create image", e);
        }
        return image; // no save
    }

    public Image createImage(InputStream inputStream) throws IOException {

        Image image = new Image();

        try {
            image.setImage(new SerialBlob(inputStream.readAllBytes()));
        } catch (Exception e) {
            throw new IOException("Failed to create image", e);
        }

        imageRepository.save(image);

        return image;
    }

    public Resource getImageFile(long id) throws SQLException {

        Image image = imageRepository.findById(id).orElseThrow();

        if (image.getImage() != null) {
            return new InputStreamResource(image.getImage().getBinaryStream());
        } else {
            throw new RuntimeException("Image file not found");
        }
    }

    public Image replaceImageFile(long id, InputStream inputStream) throws IOException {

        Image image = imageRepository.findById(id).orElseThrow();

        try {
            image.setImage(new SerialBlob(inputStream.readAllBytes()));
        } catch (Exception e) {
            throw new IOException("Failed to create image", e);
        }

        imageRepository.save(image);

        return image;
    }

    public Image deleteImage(long id) {
        Image image = imageRepository.findById(id).orElseThrow();
        imageRepository.deleteById(id);
        return image;
    }

}
