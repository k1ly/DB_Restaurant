package by.belstu.it.lyskov.dbrestaurant.controller.validation;

import by.belstu.it.lyskov.dbrestaurant.dto.newdto.NewUserDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class PasswordMatcherValidator implements ConstraintValidator<PasswordMatcher, NewUserDto> {

    @Override
    public boolean isValid(NewUserDto userDto, ConstraintValidatorContext constraintValidatorContext) {
        return Objects.equals(userDto.getPassword(), userDto.getMatchingPsw());
    }
}
