package rs.ac.uns.ftn.asd.BookedUp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.BookedUp.domain.Photo;
import rs.ac.uns.ftn.asd.BookedUp.repository.IPhotoRepository;
import rs.ac.uns.ftn.asd.BookedUp.service.interfaces.ServiceInterface;

import java.util.Collection;

@Service
public class PhotoService implements ServiceInterface<Photo> {
    @Autowired
    private IPhotoRepository repository;
    @Override
    public Collection<Photo> getAll() {
        return repository.findAll();
    }

    @Override
    public Photo getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Photo create(Photo photo) throws Exception {
        if (photo.getId() != null) {
            throw new Exception("Id mora biti null prilikom perzistencije novog entiteta.");
        }
        photo.setActive(true);
        photo.setWidth(0);
        photo.setHeight(0);
        return repository.save(photo);
    }

    @Override
    public Photo save(Photo photo) throws Exception {
        return repository.save(photo);
    }


//    @Override
//    public PhotoDTO update(PhotoDTO photoDTO) throws Exception {
//        Photo photo = photoMapper.toEntity(photoDTO);
//        Photo photoToUpdate = repository.findById(photo.getId()).orElse(null);
//        if (photoToUpdate == null) {
//            throw new Exception("Trazeni entitet nije pronadjen.");
//        }
//        photoToUpdate.setUrl(photo.getUrl());
//        photoToUpdate.setCaption(photo.getCaption());
//        photoToUpdate.setWidth(photo.getWidth());
//        photoToUpdate.setHeight(photo.getHeight());
//
//        Photo updatedPhoto = repository.save(photoToUpdate);
//        return photoMapper.toDto(updatedPhoto);
//    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
