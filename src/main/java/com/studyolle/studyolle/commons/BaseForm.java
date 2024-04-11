package com.studyolle.studyolle.commons;

import com.studyolle.studyolle.modules.account.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class BaseForm {
    protected Account account;
}
