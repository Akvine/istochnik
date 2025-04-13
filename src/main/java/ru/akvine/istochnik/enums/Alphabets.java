package ru.akvine.istochnik.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

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

    public final static Map<Character, String> TRANSLIT_MAP = Map.<Character, String>ofEntries(
            Map.entry('А', "A"), Map.entry('а', "a"),
            Map.entry('Б', "B"), Map.entry('б', "b"),
            Map.entry('В', "V"), Map.entry('в', "v"),
            Map.entry('Г', "G"), Map.entry('г', "g"),
            Map.entry('Д', "D"), Map.entry('д', "d"),
            Map.entry('Е', "E"), Map.entry('е', "e"),
            Map.entry('Ё', "Yo"), Map.entry('ё', "yo"),
            Map.entry('Ж', "Zh"), Map.entry('ж', "zh"),
            Map.entry('З', "Z"), Map.entry('з', "z"),
            Map.entry('И', "I"), Map.entry('и', "i"),
            Map.entry('Й', "Y"), Map.entry('й', "y"),
            Map.entry('К', "K"), Map.entry('к', "k"),
            Map.entry('Л', "L"), Map.entry('л', "l"),
            Map.entry('М', "M"), Map.entry('м', "m"),
            Map.entry('Н', "N"), Map.entry('н', "n"),
            Map.entry('О', "O"), Map.entry('о', "o"),
            Map.entry('П', "P"), Map.entry('п', "p"),
            Map.entry('Р', "R"), Map.entry('р', "r"),
            Map.entry('С', "S"), Map.entry('с', "s"),
            Map.entry('Т', "T"), Map.entry('т', "t"),
            Map.entry('У', "U"), Map.entry('у', "u"),
            Map.entry('Ф', "F"), Map.entry('ф', "f"),
            Map.entry('Х', "Kh"), Map.entry('х', "kh"),
            Map.entry('Ц', "Ts"), Map.entry('ц', "ts"),
            Map.entry('Ч', "Ch"), Map.entry('ч', "ch"),
            Map.entry('Ш', "Sh"), Map.entry('ш', "sh"),
            Map.entry('Щ', "Shch"), Map.entry('щ', "shch"),
            Map.entry('Ъ', ""), Map.entry('ъ', ""),
            Map.entry('Ы', "Y"), Map.entry('ы', "y"),
            Map.entry('Ь', ""), Map.entry('ь', ""),
            Map.entry('Э', "E"), Map.entry('э', "e"),
            Map.entry('Ю', "Yu"), Map.entry('ю', "yu"),
            Map.entry('Я', "Ya"), Map.entry('я', "ya")
    );

    private final String code;
    private final List<Character> chars;
}
