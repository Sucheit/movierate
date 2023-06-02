package arthur.inzhilov.movierate.service;

import arthur.inzhilov.movierate.dao.FilmRatingRepository;
import arthur.inzhilov.movierate.dao.FilmRepository;
import arthur.inzhilov.movierate.dao.UserRepository;
import arthur.inzhilov.movierate.dto.FilmDto;
import arthur.inzhilov.movierate.entity.FilmEntity;
import arthur.inzhilov.movierate.entity.FilmRatingEntity;
import arthur.inzhilov.movierate.entity.UserEntity;
import arthur.inzhilov.movierate.exception.NotFoundException;
import arthur.inzhilov.movierate.utility.Film;
import arthur.inzhilov.movierate.utility.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

import static arthur.inzhilov.movierate.service.FilmService.mapFilmEntityToFilmDto;
import static arthur.inzhilov.movierate.utility.Constants.DECIMAL_FORMAT;
import static arthur.inzhilov.movierate.utility.Constants.NUMBER_OF_RECOMMENDED_FILMS;
import static arthur.inzhilov.movierate.utility.SlopeOne.slopeOne;
import static java.util.Map.Entry.comparingByValue;

@Service
@RequiredArgsConstructor
public class FilmRatingService {

    private final FilmRatingRepository filmRatingRepository;

    private final FilmRepository filmRepository;

    private final UserRepository userRepository;

    public String getFilmRatingByFilmId(Long filmId) {
        Double avgRating = filmRatingRepository.getAvgRating(filmId);
        String result;
        if (avgRating == null) {
            result = "0";
        } else {
            result = DECIMAL_FORMAT.format(avgRating);
        }
        return result;
    }

    public FilmRatingEntity findByFilmIdAndUserId(Long filmId, Long userId) {
        FilmEntity filmEntity = filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден."));
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден."));
        return filmRatingRepository.findByFilmEntityAndUserEntity(filmEntity, userEntity);
    }

    public FilmRatingEntity saveFilmRating(Long filmId, Long userId, Integer rating) {
        FilmEntity filmEntity = filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден."));
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        if (filmRatingRepository.existsByFilmEntityAndUserEntity(filmEntity, userEntity)) {
            FilmRatingEntity filmRatingEntity = filmRatingRepository.findByFilmEntityAndUserEntity(filmEntity, userEntity);
            filmRatingRepository.deleteById(filmRatingEntity.getId());
        }
        return filmRatingRepository.save(FilmRatingEntity.builder()
                .filmEntity(filmEntity)
                .userEntity(userEntity)
                .rating(rating)
                .build());
    }

    public List<FilmDto> getRecommendedFilms(Long userId) {
        List<FilmRatingEntity> filmRatingEntities = filmRatingRepository.findAll();
        Set<Long> filmIdsUserRated = filmRatingEntities.stream()
                .filter(filmRatingEntity -> filmRatingEntity.getUserEntity().getId().equals(userId))
                .map(filmRatingEntity -> filmRatingEntity.getFilmEntity().getId())
                .collect(Collectors.toSet());
        if (filmIdsUserRated.size() == 0) {
            return Collections.emptyList();
        }
        Map<User, HashMap<Film, Double>> inputData = initializeData(filmRatingEntities);
        System.out.println("Входные данные: ");
        printData(inputData);
        Set<Film> films = filmRatingEntities.stream()
                .map(f -> new Film(f.getFilmEntity().getId()))
                .collect(Collectors.toSet());
        Map<User, HashMap<Film, Double>> resultData = slopeOne(inputData, films);
        System.out.println("Выходные данные: ");
        printData(resultData);
        List<Film> recommendedFilms = new ArrayList<>();
        resultData.get(User.builder().userId(userId).build()).entrySet()
                .stream()
                .filter(entry -> !filmIdsUserRated.contains(entry.getKey().getFilmId()))
                .sorted(comparingByValue(Comparator.reverseOrder()))
                .limit(NUMBER_OF_RECOMMENDED_FILMS)
                .forEach(entry -> recommendedFilms.add(entry.getKey()));
        List<FilmDto> result = new ArrayList<>();
        recommendedFilms
                .forEach(film -> result.add(mapFilmEntityToFilmDto(filmRepository.findById(film.getFilmId())
                        .orElseThrow(() -> new NotFoundException("Фильм не найден!")))));
        return result;
    }

    public static Map<User, HashMap<Film, Double>> initializeData(List<FilmRatingEntity> filmRatingEntities) {
        Map<User, HashMap<Film, Double>> data = new HashMap<>();
        Set<User> users = filmRatingEntities.stream()
                .map(FilmRatingEntity::getUserEntity)
                .map(userEntity -> User.builder().userId(userEntity.getId()).build())
                .collect(Collectors.toSet());
        users.forEach(user -> {
            HashMap<Film, Double> newUser = new HashMap<>();
            filmRatingEntities.stream().filter(f -> f.getUserEntity().getId().equals(user.getUserId()))
                    .forEach(filmRatingEntity -> {
                        newUser.put(Film.builder().filmId(filmRatingEntity.getFilmEntity().getId()).build(),
                                (double) filmRatingEntity.getRating());
                    });
            data.put(user, newUser);
        });
        return data;
    }

    private static void print(HashMap<Film, Double> hashMap) {
        NumberFormat FORMAT = new DecimalFormat("#0.000");
        hashMap
                .entrySet()
                .stream()
                .sorted(comparingByValue())
                .forEach(entry -> System.out.println("Фильм:" + entry.getKey().getFilmId() + " --> " + FORMAT.format(entry.getValue())));
    }

    private static void printData(Map<User, HashMap<Film, Double>> data) {
        for (User user : data.keySet()) {
            System.out.println("Пользователь " + user.getUserId() + ":");
            print(data.get(user));
        }
    }
}
