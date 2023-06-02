package arthur.inzhilov.movierate.controller;

import arthur.inzhilov.movierate.dto.FilmDto;
import arthur.inzhilov.movierate.dto.SearchDto;
import arthur.inzhilov.movierate.dto.UserDto;
import arthur.inzhilov.movierate.entity.UserEntity;
import arthur.inzhilov.movierate.service.FilmService;
import arthur.inzhilov.movierate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import static arthur.inzhilov.movierate.utility.Utility.MultipartFileToImageString;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final FilmService filmService;

    private final UserService userService;

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/admin")
    public String adminHomePage(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("user", user);
        return "admin/admin.html";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/adminFilms")
    public String adminShowFilms(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("films", filmService.getFilms());
        model.addAttribute("filmDto", new FilmDto());
        return "admin/adminFilms.html";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/adminUpdateFilm")
    public String adminEditFilm(FilmDto filmDto,
                                @AuthenticationPrincipal UserDetails user,
                                Model model) {
        UserDto userDto = null;
        if (user != null) {
            userDto = userService.getUserDtoByUsername(user.getUsername());
            model.addAttribute("userEntity", userDto);
        }
        model.addAttribute("user", userDto);
        model.addAttribute("genres", filmService.getGenres());
        return "admin/adminEditFilm.html";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/adminDoUpdateFilm")
    public String adminEditFilmFinished(@RequestParam("file") MultipartFile file,
                                        @Valid FilmDto filmDto,
                                        BindingResult bindingResult,
                                        @AuthenticationPrincipal UserDetails user,
                                        Model model) {
        if (!file.isEmpty()) {
            filmDto.setFile(file);
            filmDto.setImage(MultipartFileToImageString(file));
        }
        model.addAttribute("genres", filmService.getGenres());
        if (bindingResult.hasErrors()) {
            return "admin/adminEditFilm.html";
        }
        filmService.updateFilm(filmDto);
        UserDto userDto = null;
        if (user != null) {
            userDto = userService.getUserDtoByUsername(user.getUsername());
            model.addAttribute("userEntity", userDto);
        }
        model.addAttribute("user", userDto);
        return "admin/adminOnEditFilm.html";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/adminDeleteFilm")
    public String adminDeleteFilm(FilmDto filmDto,
                                  @AuthenticationPrincipal UserDetails user,
                                  Model model) {
        filmService.deleteFilm(filmDto.getId());
        UserDto userDto = null;
        if (user != null) {
            userDto = userService.getUserDtoByUsername(user.getUsername());
            model.addAttribute("userEntity", userDto);
        }
        model.addAttribute("user", userDto);
        return "admin/adminOnDeleteFilm.html";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/adminSearch")
    public String searchForm(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("searchDto", new SearchDto());
        return "admin/adminSearch.html";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/adminSearchedFilms")
    public String searchedFilms(@Valid SearchDto searchDto,
                                @AuthenticationPrincipal UserDetails user,
                                Model model) {
        model.addAttribute("user", user);
        model.addAttribute("films", filmService.findFilmsBySearchTerm(searchDto.getSearchTerm()));
        model.addAttribute("filmDto", new FilmDto());
        return "admin/adminFilms.html";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/adminAddFilm")
    public String addFilm(@AuthenticationPrincipal UserDetails user,
                          Model model) {
        model.addAttribute("user", user);
        model.addAttribute("filmDto", new FilmDto());
        model.addAttribute("genres", filmService.getGenres());
        return "admin/adminAddFilm.html";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/adminAddFilm")
    public String addFilmFinished(@Valid FilmDto filmDto,
                                  BindingResult bindingResult,
                                  @RequestParam("file") MultipartFile file,
                                  @AuthenticationPrincipal UserDetails user,
                                  Model model) {
        if (!file.isEmpty()) {
            filmDto.setFile(file);
            filmDto.setImage(MultipartFileToImageString(file));
        }
        model.addAttribute("genres", filmService.getGenres());
        if (bindingResult.hasErrors()) {
            return "admin/adminAddFilm.html";
        }
        filmDto.setFile(file);
        filmService.addFilm(filmDto);
        model.addAttribute("user", user);
        return "admin/adminOnAddFilm.html";
    }
}
