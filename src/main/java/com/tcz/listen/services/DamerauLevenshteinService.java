package com.tcz.listen.services;

import org.springframework.stereotype.Service;

@Service
public class DamerauLevenshteinService {
    public int calculateDistance(CharSequence source, CharSequence target) {
        if (source == null || target == null) {
            return -1;
        }
        int sourceLength = source.length();
        int targetLength = target.length();
        if (sourceLength == 0) return targetLength;
        if (targetLength == 0) return sourceLength;
        int[][] dist = new int[sourceLength + 1][targetLength + 1];
        for (int i = 0; i < sourceLength + 1; i++) {
            dist[i][0] = i;
        }
        for (int j = 0; j < targetLength + 1; j++) {
            dist[0][j] = j;
        }
        for (int i = 1; i < sourceLength + 1; i++) {
            for (int j = 1; j < targetLength + 1; j++) {
                int cost = source.charAt(i - 1) == target.charAt(j - 1) ? 0 : 1;
                dist[i][j] = Math.min(Math.min(dist[i - 1][j] + 1, dist[i][j - 1] + 1), dist[i - 1][j - 1] + cost);
                if (i > 1 &&
                        j > 1 &&
                        source.charAt(i - 1) == target.charAt(j - 2) &&
                        source.charAt(i - 2) == target.charAt(j - 1)) {
                    dist[i][j] = Math.min(dist[i][j], dist[i - 2][j - 2] + cost);
                }
            }
        }
        return dist[sourceLength][targetLength];
    }

    // Cравниваем посимвольно две строки (идя по самой короткой)
    // а так же учитываем максимальную длину таких совпадений
    // чтобы откинуть варианты где буквы будут просто перемешаны
    public static int[] compare(String a, String b, int[] mem) {
        int till = Math.min(a.length(), b.length()); // меньший цикл
        int max_len = 0;       // максимальная длина совпадения
        int pattern_size = 0;  // длина совпадения
        int[] ret = new int[a.length()];
        for (int i = 0; i < till; i++) {
            if (a.charAt(i) == b.charAt(i) && a.charAt(i) != ' ') {
                ret[i] = 1 + mem[i]; // накапливаем
                pattern_size += 1;
            } else if (a.charAt(i) != b.charAt(i) && a.charAt(i) != ' ') {
                ret[i] = mem[i]; // переносим предыдущий опыт
            } else {
                if (max_len < pattern_size) {
                    max_len = pattern_size;
                }
                pattern_size = 0;
            }
        }
        if (max_len < 3) return mem;
        return ret;
    }

    // Функция считает количество не нулевых элементов массива
    // Нулевыми в данном случае будут либо `пробелы`,
    // либо не совпадающие буквы.
    public int calc(int[] mem) {
        int sum = 0;
        for (int i : mem) if (i != 0) sum += 1;
        return sum;
    }

    // Функция сравнивает 2 строки `a` и `b`
    // строка `b` циклически прокручивается
    // чтобы найти максимальные совпадения со строкой `a`
    public double test(String a, String b) {
        int[] mem = new int[a.length()]; // тут накапливаем сравнения
        a += ' ';
        b += ' ';
        for (int i = 0; i < b.length(); i++) {
            String n = b.substring(i) + b.substring(0, i);
            //System.out.println(n);
            mem = compare(a, n, mem);
        }
        double letters = (double)a.replace(" ", "").length();
        double result = calc(mem) / letters;
        return 1 - result;
    }
}
