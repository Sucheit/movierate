package arthur.inzhilov.movierate.service;

import arthur.inzhilov.movierate.dao.FilmRatingRepository;
import arthur.inzhilov.movierate.dao.FilmRepository;
import arthur.inzhilov.movierate.dao.UserRepository;
import arthur.inzhilov.movierate.dto.FilmDto;
import arthur.inzhilov.movierate.entity.FilmEntity;
import arthur.inzhilov.movierate.entity.FilmRatingEntity;
import arthur.inzhilov.movierate.entity.UserEntity;
import arthur.inzhilov.movierate.exception.NotFoundException;
import arthur.inzhilov.movierate.service.slopeone.Item;
import arthur.inzhilov.movierate.service.slopeone.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

import static arthur.inzhilov.movierate.service.FilmService.mapFilmEntityToFilmDto;
import static arthur.inzhilov.movierate.utility.Constants.DECIMAL_FORMAT;
import static arthur.inzhilov.movierate.utility.Constants.NUMBER_OF_RECOMMENDED_FILMS;
import static arthur.inzhilov.movierate.service.slopeone.SlopeOne.slopeOne;
import static java.util.Map.Entry.comparingByValue;

/**
 * Класс реализующий бизнес-логику оценок фильмов пользователями
 */
@Service
@RequiredArgsConstructor
public class FilmRatingService {

    private final FilmRatingRepository filmRatingRepository;

    private final FilmRepository filmRepository;

    private final UserRepository userRepository;

    /**
     * Нахождение среднего рейтинга фильма по id фильма
     * @param filmId идентификатор фильма
     * @return средний рейтинг фильма
     */
    @Transactional(readOnly = true)
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

    /**
     * Нахождение оценки фильма пользователем
     * @param filmId идентификатор фильма
     * @param userId идентификатор пользователя
     * @return сущность рейтинга фильма найденную в базе данных
     */
    @Transactional(readOnly = true)
    public FilmRatingEntity findByFilmIdAndUserId(Long filmId, Long userId) {
        FilmEntity filmEntity = filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден."));
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        return filmRatingRepository.findByFilmEntityAndUserEntity(filmEntity, userEntity);
    }

    /**
     * Сохранение и обновление рейтинга в базе данных
     * @param filmId идентификатор фильма
     * @param userId идентификатор пользователя
     * @param rating оценка
     * @return сущность рейтинга фильма сохраненную в базе данных
     */
    @Transactional
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

    /**
     * Получение списка рекомендованных фильмов
     * @param userId идентификатор пользователя в базе данных
     * @return список фильмов
     */
    @Transactional(readOnly = true)
    public List<FilmDto> getRecommendedFilms(Long userId) {
        List<FilmRatingEntity> filmRatingEntities = filmRatingRepository.findAll();
        Set<Long> filmIdsUserRated = filmRatingEntities.stream()
                .filter(filmRatingEntity -> filmRatingEntity.getUserEntity().getId().equals(userId))
                .map(filmRatingEntity -> filmRatingEntity.getFilmEntity().getId())
                .collect(Collectors.toSet());
        if (filmIdsUserRated.size() == 0) {
            return Collections.emptyList();
        }
        Map<User, HashMap<Item, Double>> inputData = initializeData(filmRatingEntities);
        System.out.println("initial data:");
        printData(inputData);
        Set<Item> items = filmRatingEntities.stream()
                .map(f -> new Item(f.getFilmEntity().getId()))
                .collect(Collectors.toSet());
        Map<User, HashMap<Item, Double>> resultData = slopeOne(inputData, items);
        System.out.println("result data:");
        printData(resultData);
        List<Item> recommendedFilms = new ArrayList<>();
        resultData.get(User.builder().userId(userId).build()).entrySet()
                .stream()
                .filter(entry -> !filmIdsUserRated.contains(entry.getKey().getItemId()))
                .sorted(comparingByValue(Comparator.reverseOrder()))
                .limit(NUMBER_OF_RECOMMENDED_FILMS)
                .forEach(entry -> recommendedFilms.add(entry.getKey()));
        List<FilmDto> result = new ArrayList<>();
        recommendedFilms
                .forEach(film -> result.add(mapFilmEntityToFilmDto(filmRepository.findById(film.getItemId())
                        .orElseThrow(() -> new NotFoundException("Фильм не найден!")))));
        return result;
    }

    /**
     * Преобразование данных полученных из БД для алгоритма рекомендованных фильмов
     * @param filmRatingEntities список сущностей рейтингов фильмов
     * @return таблицу данных пользователей, вещей с оценками
     */
    private Map<User, HashMap<Item, Double>> initializeData(List<FilmRatingEntity> filmRatingEntities) {
        Map<User, HashMap<Item, Double>> data = new HashMap<>();
        Set<User> users = filmRatingEntities.stream()
                .map(FilmRatingEntity::getUserEntity)
                .map(userEntity -> User.builder().userId(userEntity.getId()).build())
                .collect(Collectors.toSet());
        users.forEach(user -> {
            HashMap<Item, Double> newUser = new HashMap<>();
            filmRatingEntities.stream().filter(f -> f.getUserEntity().getId().equals(user.getUserId()))
                    .forEach(filmRatingEntity ->
                            newUser.put(Item.builder().itemId(filmRatingEntity.getFilmEntity().getId()).build(),
                                (double) filmRatingEntity.getRating()));
            data.put(user, newUser);
        });
        return data;
    }

    private static void print(HashMap<Item, Double> hashMap) {
        NumberFormat FORMAT = new DecimalFormat("#0.000");
        hashMap
                .entrySet()
                .stream()
                .sorted(comparingByValue())
                .forEach(entry -> System.out.println("Фильм:" + entry.getKey().getItemId() + " --> " + FORMAT.format(entry.getValue())));
    }

    private static void printData(Map<User, HashMap<Item, Double>> data) {
        data.forEach((key, value) -> {
            System.out.println("Пользователь " + key.getUserId() + ":");
            print(data.get(key));
        });
    }
}
