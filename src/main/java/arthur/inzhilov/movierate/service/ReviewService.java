package arthur.inzhilov.movierate.service;

import arthur.inzhilov.movierate.dto.ReviewDto;
import arthur.inzhilov.movierate.entity.ReviewEntity;
import arthur.inzhilov.movierate.exception.NotFoundException;
import arthur.inzhilov.movierate.dao.FilmRepository;
import arthur.inzhilov.movierate.dao.ReviewRepository;
import arthur.inzhilov.movierate.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static arthur.inzhilov.movierate.utility.Constants.REVIEW_POST_DATE_FORMATTER;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final UserRepository userRepository;

    private final FilmRepository filmRepository;

    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsByFilmId(Long filmId) {
        return reviewRepository.findByFilmIdOrderByPostDateDesc(filmId).stream()
                .map(this::mapReviewEntityToReviewDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addReview(ReviewDto reviewDto) {
        ReviewEntity reviewEntity = reviewRepository.findByFilmIdAndUserId(reviewDto.getFilmId(), reviewDto.getUserId());
        if (reviewEntity != null) {
            return;
        }
        reviewRepository.save(ReviewEntity.builder()
                .id(null)
                .film(filmRepository.findById(reviewDto.getFilmId()).
                        orElseThrow(() -> new NotFoundException("Фильм не найден")))
                .user(userRepository.findById(reviewDto.getUserId()).
                        orElseThrow(() -> new NotFoundException("Пользователь не найден")))
                .text(reviewDto.getText())
                .postDate(LocalDateTime.now())
                .build());
    }

    @Transactional
    public void updateReview(ReviewDto reviewDto) {
        reviewRepository.save(ReviewEntity.builder()
                .id(reviewRepository.findByFilmIdAndUserId(reviewDto.getFilmId(), reviewDto.getUserId()).getId())
                .film(filmRepository.findById(reviewDto.getFilmId()).
                        orElseThrow(() -> new NotFoundException("Фильм не найден")))
                .user(userRepository.findById(reviewDto.getUserId()).
                        orElseThrow(() -> new NotFoundException("Пользователь не найден")))
                .text(reviewDto.getText())
                .postDate(LocalDateTime.now())
                .build());
    }

    @Transactional(readOnly = true)
    public ReviewDto getReviewByFilmIdAndUserId(Long filmId, Long userId) {
        ReviewEntity reviewEntity = reviewRepository.findByFilmIdAndUserId(filmId, userId);
        if (reviewEntity == null) {
            return null;
        }
        return ReviewDto.builder()
                .id(reviewEntity.getId())
                .filmId(reviewEntity.getFilm().getId())
                .userId(reviewEntity.getFilm().getId())
                .text(reviewEntity.getText())
                .postDate(reviewEntity.getPostDate().format(REVIEW_POST_DATE_FORMATTER))
                .build();
    }

    @Transactional
    public void deleteReviewById(Long commentId) {
        reviewRepository.deleteById(commentId);
    }

    private ReviewDto mapReviewEntityToReviewDto(ReviewEntity reviewEntity) {
        return ReviewDto.builder()
                .id(reviewEntity.getId())
                .filmId(reviewEntity.getFilm().getId())
                .film(reviewEntity.getFilm())
                .userId(reviewEntity.getUser().getId())
                .user(reviewEntity.getUser())
                .text(reviewEntity.getText())
                .postDate(reviewEntity.getPostDate().format(REVIEW_POST_DATE_FORMATTER))
                .build();
    }
}
