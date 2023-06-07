package arthur.inzhilov.movierate.controller;

import arthur.inzhilov.movierate.dto.*;
import arthur.inzhilov.movierate.entity.FilmRatingEntity;
import arthur.inzhilov.movierate.service.FilmRatingService;
import arthur.inzhilov.movierate.service.FilmService;
import arthur.inzhilov.movierate.service.ReviewService;
import arthur.inzhilov.movierate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

import static arthur.inzhilov.movierate.utility.Constants.RATINGS;

@Controller
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    private final ReviewService reviewService;

    private final FilmRatingService filmRatingService;

    private final UserService userService;

    @GetMapping(value = "/films")
    public String showFilms(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("films", filmService.getFilms());
        model.addAttribute("filmDto", new FilmDto());
        return "films/films.html";
    }

    @GetMapping(value = "/getFilm")
    public String getFilm(FilmDto filmDto,
                          @AuthenticationPrincipal UserDetails user,
                          Model model) {
        FilmDto film = filmService.getFilmById(filmDto.getId());
        FilmRatingEntity filmRatingEntity = null;
        ReviewDto reviewDto = null;
        UserDto userDto = null;
        if (user != null) {
            userDto = userService.getUserDtoByUsername(user.getUsername());
            filmRatingEntity = filmRatingService.findByFilmIdAndUserId(film.getId(), userDto.getId());
            reviewDto = reviewService.getReviewByFilmIdAndUserId(film.getId(), userDto.getId());
            model.addAttribute("userEntity", userDto);
        }
        model.addAttribute("filmEntityCurr", film);
        model.addAttribute("user", userDto);
        model.addAttribute("filmRatingDto", new FilmRatingDto());
        model.addAttribute("ratings", RATINGS);
        model.addAttribute("filmRatingEntity", filmRatingEntity);
        model.addAttribute("reviewDto", reviewDto);
        model.addAttribute("reviewDtoNew", new ReviewDto());
        model.addAttribute("comments", reviewService.getReviewsByFilmId(film.getId()));
        model.addAttribute("rating", filmRatingService.getFilmRatingByFilmId(film.getId()));
        return "films/film.html";
    }

    @Secured("ROLE_USER")
    @PostMapping(value = "/updateRating")
    public String filmUpdateRating(FilmRatingDto filmRatingDto,
                                   @AuthenticationPrincipal UserDetails user,
                                   Model model) {
        FilmDto film = filmService.getFilmById(filmRatingDto.getFilmId());
        FilmRatingEntity filmRatingEntity = null;
        ReviewDto reviewDto = null;
        UserDto userDto = null;
        if (user != null) {
            userDto = userService.getUserDtoByUsername(user.getUsername());
            filmRatingEntity = filmRatingService.saveFilmRating(film.getId(), userDto.getId(), filmRatingDto.getRating());
            reviewDto = reviewService.getReviewByFilmIdAndUserId(film.getId(), userDto.getId());
            model.addAttribute("userEntity", userDto);
        }
        model.addAttribute("user", userDto);
        model.addAttribute("filmEntityCurr", film);
        model.addAttribute("filmRatingDto", new FilmRatingDto());
        model.addAttribute("ratings", RATINGS);
        model.addAttribute("filmRatingEntity", filmRatingEntity);
        model.addAttribute("reviewDto", reviewDto);
        model.addAttribute("reviewDtoNew", new ReviewDto());
        model.addAttribute("comments", reviewService.getReviewsByFilmId(film.getId()));
        model.addAttribute("rating", filmRatingService.getFilmRatingByFilmId(film.getId()));
        return "films/film.html";
    }

    @Secured("ROLE_USER")
    @PostMapping(value = "/addReview")
    public String addReview(ReviewDto reviewDto,
                            @AuthenticationPrincipal UserDetails user,
                            Model model) {
        if (!reviewDto.getText().equals("") || new StringBuilder(reviewDto.getText()).length() < 255) {
            reviewService.addReview(reviewDto);
        }
        FilmDto film = filmService.getFilmById(reviewDto.getFilmId());
        FilmRatingEntity filmRatingEntity = null;
        UserDto userDto = null;
        if (user != null) {
            userDto = userService.getUserDtoByUsername(user.getUsername());
            filmRatingEntity = filmRatingService.findByFilmIdAndUserId(film.getId(), userDto.getId());
            reviewDto = reviewService.getReviewByFilmIdAndUserId(film.getId(), userDto.getId());
            model.addAttribute("userEntity", userDto);
        }
        model.addAttribute("filmEntityCurr", film);
        model.addAttribute("user", userDto);
        model.addAttribute("filmRatingDto", new FilmRatingDto());
        model.addAttribute("ratings", RATINGS);
        model.addAttribute("filmRatingEntity", filmRatingEntity);
        model.addAttribute("reviewDto", reviewDto);
        model.addAttribute("reviewDtoNew", new ReviewDto());
        model.addAttribute("comments", reviewService.getReviewsByFilmId(film.getId()));
        model.addAttribute("rating", filmRatingService.getFilmRatingByFilmId(film.getId()));
        return "films/film.html";
    }

    @Secured("ROLE_USER")
    @PostMapping(value = "/updateReview")
    public String updateReview(ReviewDto reviewDto,
                               @AuthenticationPrincipal UserDetails user,
                               Model model) {
        if (!reviewDto.getText().equals("") || new StringBuilder(reviewDto.getText()).length() < 255) {
            reviewService.updateReview(reviewDto);
        }
        FilmDto filmDto = filmService.getFilmById(reviewDto.getFilmId());
        FilmRatingEntity filmRatingEntity = null;
        UserDto userDto = null;
        if (user != null) {
            userDto = userService.getUserDtoByUsername(user.getUsername());
            filmRatingEntity = filmRatingService.findByFilmIdAndUserId(filmDto.getId(), userDto.getId());
            reviewDto = reviewService.getReviewByFilmIdAndUserId(filmDto.getId(), userDto.getId());
            model.addAttribute("userEntity", userDto);
        }
        model.addAttribute("filmEntityCurr", filmDto);
        model.addAttribute("user", userDto);
        model.addAttribute("filmRatingDto", new FilmRatingDto());
        model.addAttribute("ratings", RATINGS);
        model.addAttribute("filmRatingEntity", filmRatingEntity);
        model.addAttribute("reviewDto", reviewDto);
        model.addAttribute("reviewDtoNew", new ReviewDto());
        model.addAttribute("comments", reviewService.getReviewsByFilmId(filmDto.getId()));
        model.addAttribute("rating", filmRatingService.getFilmRatingByFilmId(filmDto.getId()));
        return "films/film.html";
    }

    @Secured("ROLE_USER")
    @PostMapping(value = "/deleteReview")
    public String deleteReview(ReviewDto reviewDto,
                               @AuthenticationPrincipal UserDetails user,
                               Model model) {
        reviewService.deleteReviewById(reviewDto.getId());
        FilmDto film = filmService.getFilmById(reviewDto.getFilmId());
        FilmRatingEntity filmRatingEntity = null;
        UserDto userDto = null;
        if (user != null) {
            userDto = userService.getUserDtoByUsername(user.getUsername());
            filmRatingEntity = filmRatingService.findByFilmIdAndUserId(film.getId(), userDto.getId());
            reviewDto = reviewService.getReviewByFilmIdAndUserId(film.getId(), userDto.getId());
            model.addAttribute("userEntity", userDto);
        }
        model.addAttribute("filmEntityCurr", film);
        model.addAttribute("user", userDto);
        model.addAttribute("filmRatingDto", new FilmRatingDto());
        model.addAttribute("ratings", RATINGS);
        model.addAttribute("filmRatingEntity", filmRatingEntity);
        model.addAttribute("reviewDto", reviewDto);
        model.addAttribute("reviewDtoNew", new ReviewDto());
        model.addAttribute("comments", reviewService.getReviewsByFilmId(film.getId()));
        model.addAttribute("rating", filmRatingService.getFilmRatingByFilmId(film.getId()));
        return "films/film.html";
    }

    @GetMapping(value = "search")
    public String searchForm(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("searchDto", new SearchDto());
        return "films/search.html";
    }

    @PostMapping(value = "searchedFilms")
    public String searchedFilms(@Valid SearchDto searchDto,
                                @AuthenticationPrincipal UserDetails user,
                                Model model) {
        model.addAttribute("user", user);
        model.addAttribute("films", filmService.findFilmsBySearchTerm(searchDto.getSearchTerm()));
        model.addAttribute("searchDto", searchDto);
        model.addAttribute("filmDto", new FilmDto());
        return "films/searched.html";
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "recommended")
    public String recommendedFilms(
            @AuthenticationPrincipal UserDetails user,
            Model model) {
        model.addAttribute("user", user);
        model.addAttribute("films",
                filmRatingService.getRecommendedFilms(userService.getUserDtoByUsername(user.getUsername()).getId()));
        model.addAttribute("filmDto", new FilmDto());
        return "films/recommended.html";
    }
}
