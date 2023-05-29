package com.timetable.timetablebe.services;

import com.timetable.timetablebe.components.ApplicationStartup;
import com.timetable.timetablebe.components.ApplicationStartupBase;
import com.timetable.timetablebe.dtos.TimetableFileDto;
import com.timetable.timetablebe.entities.AssignedEventEntity;
import com.timetable.timetablebe.entities.TimetableFileEntity;
import com.timetable.timetablebe.exceptions.TimetableFileNotFoundException;
import com.timetable.timetablebe.exceptions.UnableToReadTimetableFileException;
import com.timetable.timetablebe.repos.AssignedEventRepository;
import com.timetable.timetablebe.repos.TimetableFileRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

@Service
public class TimetableFileService {
    @Autowired
    private TimetableFileRepository timetableFileRepo;
    @Autowired
    private ApplicationStartupBase applicationStartup;

    @Autowired
    private ModelMapper mapper;

    public TimetableFileDto saveTimetableFile(MultipartFile multipartFile) {

        String fileName;
        if (multipartFile.getOriginalFilename() == null) {
            throw new UnableToReadTimetableFileException("Unable to read timetable file");
        }
        fileName = multipartFile.getOriginalFilename();
        File file = new File(fileName);

        try (OutputStream os = new FileOutputStream(file)) {
            os.write(multipartFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Optional<TimetableFileEntity> timetableFileEntityOpt = timetableFileRepo.findByName(fileName);
        if (timetableFileEntityOpt.isPresent()) {
            timetableFileRepo.delete(timetableFileEntityOpt.get());
        }

        TimetableFileEntity timetableFileEntity = new TimetableFileEntity();
        timetableFileEntity.setFile(file);
        timetableFileEntity.setName(fileName);
        timetableFileEntity.setTimestampAdded(System.currentTimeMillis());

        timetableFileEntity = timetableFileRepo.save(timetableFileEntity);
        return mapper.map(timetableFileEntity, TimetableFileDto.class);
    }

    public Long getTimetableFileIdByName(String name) {
        Optional<TimetableFileEntity> timetableFileEntity = timetableFileRepo.findByName(name);

        if (timetableFileEntity.isEmpty()) {
            throw new TimetableFileNotFoundException("Timetable file with name " + name + " not found");
        }

        return timetableFileEntity.get().getId();
    }

    public void deleteTimetableFile(Long id) {
        Optional<TimetableFileEntity> timetableFileEntity = timetableFileRepo.findById(id);

        if (timetableFileEntity.isEmpty()) {
            throw new TimetableFileNotFoundException("Timetable file with id " + id + " not found");
        }

        timetableFileRepo.delete(timetableFileEntity.get());
    }

    public TimetableFileDto setTimetableFile(String name) {
        Optional<TimetableFileEntity> timetableFileEntity = timetableFileRepo.findByName(name);

        if (timetableFileEntity.isEmpty()) {
            throw new TimetableFileNotFoundException("Timetable file with name " + name + " not found");
        }

        ApplicationStartup.XML_FILE = timetableFileEntity.get().getFile();
        AssignedEventService.cachedAlgorithmOption = "";
        try {
            applicationStartup.initializeDatabase();
        } catch (IOException e) {
            throw new UnableToReadTimetableFileException("Unable to read timetable file " + name);
        }

        return mapper.map(timetableFileEntity.get(), TimetableFileDto.class);
    }

    public List<String> getAllTimetableFileNames() {
        List<TimetableFileEntity> timetableFileEntities = timetableFileRepo.findAll();
        return timetableFileEntities.stream()
                .map(TimetableFileEntity::getName)
                .toList();
    }
}
