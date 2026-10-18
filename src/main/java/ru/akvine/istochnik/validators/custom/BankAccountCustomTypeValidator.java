package ru.akvine.istochnik.validators.custom;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.utils.RequisiteUtils;

@Component
public class BankAccountCustomTypeValidator implements CustomTypeValidator {

    @Override
    public List<String> validate(ColumnDto column, int rowsCount) {
        ConfigDto config = column.getConfig();
        List<String> errorMessages = new ArrayList<>();

        // БИК банка, под который рассчитывается контрольный ключ, передаётся в поле start
        String bik = StringUtils.trimToNull(config.getStart());
        if (bik == null) {
            if (config.isValid()) {
                errorMessages.add(ErrorMessages.BIK_IS_REQUIRED_ERROR);
            }
        } else if (!RequisiteUtils.isDigits(bik, RequisiteUtils.BIK_LENGTH)) {
            errorMessages.add(ErrorMessages.BIK_IS_INVALID_ERROR);
        }

        return errorMessages;
    }

    @Override
    public CustomType getCustomType() {
        return CustomType.BANK_ACCOUNT;
    }

    interface ErrorMessages {
        String BIK_IS_REQUIRED_ERROR =
                "field [start] must contain BIK (9 digits) of the bank when field [valid] is true";
        String BIK_IS_INVALID_ERROR = "field [start] is not a BIK. Expected 9 digits, for example: 044525225";
    }
}
