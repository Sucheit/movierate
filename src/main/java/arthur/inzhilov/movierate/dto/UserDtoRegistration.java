package arthur.inzhilov.movierate.dto;

import arthur.inzhilov.movierate.validation.EmailValitation;
import arthur.inzhilov.movierate.validation.UsernameValidation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDtoRegistration {

    @UsernameValidation(message = "Имя пользователя занято.")
    @NotBlank(message = "Имя пользователя не может быть пустым.")
    @Size(min = 3, max = 20, message = "Имя пользователя должно иметь размер от 3 до 20 символов.")
    private String username;

    @NotBlank(message = "Пароль не может быть пустым.")
    @Size(min = 6, max = 20, message = "Пароль должен иметь размер от 6 до 20 символов.")
    private String password;

    @EmailValitation(message = "Почта занята.")
    @NotBlank(message = "Почта не может быть пустой.")
    @Email(message = "Почта должна иметь правильный формат.")
    private String email;
}
