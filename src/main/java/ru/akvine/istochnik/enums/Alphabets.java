package ru.akvine.istochnik.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum Alphabets {
    ENGLISH("en", List.of('A','B','C','D','E','F','G','H','I','J','K','L','M',
            'N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
            'a','b','c','d','e','f','g','h','i','j','k','l','m',
            'n','o','p','q','r','s','t','u','v','w','x','y','z')),
    RUSSIAN("ru", List.of('А','Б','В','Г','Д','Е','Ё','Ж','З','И','Й','К','Л','М',
            'Н','О','П','Р','С','Т','У','Ф','Х','Ц','Ч','Ш','Щ','Ъ',
            'Ы','Ь','Э','Ю','Я',
            'а','б','в','г','д','е','ё','ж','з','и','й','к','л','м',
            'н','о','п','р','с','т','у','ф','х','ц','ч','ш','щ','ъ',
            'ы','ь','э','ю','я'));

    private final String code;
    private final List<Character> chars;
}
