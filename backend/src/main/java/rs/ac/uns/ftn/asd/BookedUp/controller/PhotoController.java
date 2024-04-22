package rs.ac.uns.ftn.asd.BookedUp.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.uns.ftn.asd.BookedUp.domain.Photo;
import rs.ac.uns.ftn.asd.BookedUp.domain.User;
import rs.ac.uns.ftn.asd.BookedUp.dto.AccommodationDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.PhotoDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.UserDTO;
import rs.ac.uns.ftn.asd.BookedUp.mapper.PhotoMapper;
import rs.ac.uns.ftn.asd.BookedUp.mapper.UserMapper;
import rs.ac.uns.ftn.asd.BookedUp.service.PhotoService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/photo")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PhotoController {
    @Autowired
    private PhotoService photoService;
    private static final String UPLOAD_DIR = "images/";


    /* url: /api/photo/1 GET*/
    @GetMapping("/{id}/load")
    public ResponseEntity<byte[]> loadPhoto(@PathVariable Long id) throws IOException {
        Photo photo = photoService.getById(id);
        if (photo != null) {
            String[] splitPath = photo.getUrl().split("/");
            String imageName = splitPath[splitPath.length - 1];
            System.out.println(imageName);

            String fileExtension = imageName.substring(imageName.lastIndexOf(".") + 1);

            MediaType mediaType;
            if ("png".equalsIgnoreCase(fileExtension)) {
                mediaType = MediaType.IMAGE_PNG;
            } else if ("jpg".equalsIgnoreCase(fileExtension) || "jpeg".equalsIgnoreCase(fileExtension)) {
                mediaType = MediaType.IMAGE_JPEG;
            } else {
                // Ako format slike nije prepoznat, možete koristiti opšti tip
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }

//            Resource resource = new ClassPathResource(UPLOAD_DIR + imageName);
            Path imagePath = Paths.get("src/main/resources/images/", imageName);
            Resource resource = new PathResource(imagePath);
            byte[] imageBytes = Files.readAllBytes(resource.getFile().toPath());
            System.out.println(resource.getFile().toPath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);

            return ResponseEntity.ok().headers(headers).body(imageBytes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage (@RequestParam("image") MultipartFile imageFile) throws Exception {
        String uploadDirectory = "src/main/resources/images";
        String uniqueFileName = imageFile.getOriginalFilename();

        Path uploadPath = Path.of(uploadDirectory);
        Path filePath = uploadPath.resolve(uniqueFileName);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    /*url: /api/photo GET*/
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<PhotoDTO>> getPhotos() {
        Collection<Photo> photos = photoService.getAll();
        Collection<PhotoDTO> photosDTO = photos.stream()
                .map(PhotoMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(photosDTO, HttpStatus.OK);
    }

    /* url: /api/photo/1 GET*/
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PhotoDTO> getPhoto(@PathVariable("id") Long id) {
        Photo photo = photoService.getById(id);

        if (photo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(PhotoMapper.toDto(photo), HttpStatus.OK);
    }

    /*url: /api/photo POST*/
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PhotoDTO> createPhoto(@Valid @RequestBody PhotoDTO photoDTO) throws Exception {
        Photo createdPhoto = null;

        try {
            createdPhoto = photoService.create(PhotoMapper.toEntity(photoDTO));

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new PhotoDTO(),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(PhotoMapper.toDto(createdPhoto), HttpStatus.CREATED);
    }

    /* url: /api/photo/1 PUT*/
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PhotoDTO> updatePhoto(@RequestBody PhotoDTO photoDTO, @PathVariable Long id)
            throws Exception {
        Photo photoForUpdate = photoService.getById(id);

        if (photoForUpdate == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        photoForUpdate.setUrl(photoDTO.getUrl());
        photoForUpdate.setCaption(photoDTO.getCaption());

        photoForUpdate = photoService.save(photoForUpdate);

        return new ResponseEntity<>(PhotoMapper.toDto(photoForUpdate), HttpStatus.OK);
    }

    /** url: /api/photo/1 DELETE*/
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Photo> deletePhoto(@PathVariable("id") Long id) {
        try {
            photoService.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
