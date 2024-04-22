package com.example.certificatesbackend.repository;

import com.example.certificatesbackend.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPhotoRepository extends JpaRepository<Photo, Long> {


}
