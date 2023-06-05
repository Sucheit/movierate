package arthur.inzhilov.movierate.service;

import arthur.inzhilov.movierate.dao.FilmRepository;
import arthur.inzhilov.movierate.dao.GenreRepository;
import arthur.inzhilov.movierate.dto.FilmDto;
import arthur.inzhilov.movierate.entity.FilmEntity;
import arthur.inzhilov.movierate.entity.GenreEntity;
import arthur.inzhilov.movierate.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static arthur.inzhilov.movierate.utility.Constants.FILM_RELEASE_DATE_FORMATTER;
import static arthur.inzhilov.movierate.utility.Utility.MultipartFileToImageString;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmRepository filmRepository;

    private final GenreRepository genreRepository;

    @Transactional(readOnly = true)
    public List<FilmDto> getFilms() {
        return filmRepository.findAll().stream()
                .map(FilmService::mapFilmEntityToFilmDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FilmDto getFilmById(Long filmId) {
        FilmEntity filmEntity = filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException(String.format("Фильма '%s' не существует.", filmId)));
        return mapFilmEntityToFilmDto(filmEntity);
    }

    @Transactional
    public void addFilm(FilmDto filmDto) {
        FilmEntity filmEntity = FilmEntity.builder()
                .id(null)
                .name(filmDto.getName())
                .description(filmDto.getDescription())
                .duration(filmDto.getDuration())
                .genre(genreRepository.findById(filmDto.getGenreId())
                        .orElseThrow(() -> new NotFoundException("Жанр не найден.")))
                .releaseDate(LocalDate.parse(filmDto.getReleaseDate(), FILM_RELEASE_DATE_FORMATTER))
                .build();
        if (filmDto.getImage() != null) {
            filmEntity.setImage(filmDto.getImage());
        } else if (!filmDto.getFile().isEmpty()) {
            filmEntity.setImage(MultipartFileToImageString(filmDto.getFile()));
        }
        filmRepository.save(filmEntity);
    }

    @Transactional
    public void updateFilm(FilmDto filmDto) {
        Optional<FilmEntity> optionalFilmEntity = filmRepository.findById(filmDto.getId());
        if (optionalFilmEntity.isEmpty()) {
            throw new NotFoundException("Фильм не найден.");
        }
        FilmEntity filmEntityNew = FilmEntity.builder()
                .id(filmDto.getId())
                .name(filmDto.getName())
                .description(filmDto.getDescription())
                .duration(filmDto.getDuration())
                .genre(genreRepository.findById(filmDto.getGenreId())
                        .orElseThrow(() -> new NotFoundException("Жанр не найден.")))
                .releaseDate(LocalDate.parse(filmDto.getReleaseDate(), FILM_RELEASE_DATE_FORMATTER))
                .build();
        if (filmDto.getImage() != null) {
            filmEntityNew.setImage(filmDto.getImage());
        } else if (!filmDto.getFile().isEmpty()) {
            filmEntityNew.setImage(MultipartFileToImageString(filmDto.getFile()));
        } else {
            filmEntityNew.setImage(optionalFilmEntity.get().getImage());
        }
        filmRepository.save(filmEntityNew);
    }

    @Transactional
    public void deleteFilm(Long filmId) {
        filmRepository.deleteById(filmId);
    }

    @Transactional
    public List<GenreEntity> getGenres() {
        return genreRepository.findAll().stream()
                .sorted(Comparator.comparing(GenreEntity::getId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FilmDto> findFilmsBySearchTerm(String searchTerm) {
        return filmRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, searchTerm)
                .stream()
                .map(FilmService::mapFilmEntityToFilmDto)
                .collect(Collectors.toList());
    }

    public static FilmDto mapFilmEntityToFilmDto(FilmEntity filmEntity) {
        return FilmDto.builder()
                .id(filmEntity.getId())
                .name(filmEntity.getName())
                .description(filmEntity.getDescription())
                .releaseDate(filmEntity.getReleaseDate().format(FILM_RELEASE_DATE_FORMATTER))
                .duration(filmEntity.getDuration())
                .image(filmEntity.getImage())
                .genreId(filmEntity.getGenre().getId())
                .genre(filmEntity.getGenre())
                .build();
    }
}
