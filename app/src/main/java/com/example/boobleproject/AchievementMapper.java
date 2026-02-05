package com.example.boobleproject;

import java.util.ArrayList;
import java.util.List;

public class AchievementMapper {

    public static List<Achievement> fromApi(
            AchievementsResponse.CompletedTasks tasks,
            CountStatsResponse stats) {

        List<Achievement> list = new ArrayList<>();

        // --- Лайки ---
        list.add(create("firstLike", tasks.firstLike, "Первый лайк",
                "Поставить первый лайк", R.drawable.likeor,
                percent(stats.totalLikesGiven, 1)));

        list.add(create("tenLikes", tasks.tenLikes, "10 лайков",
                "Поставить 10 лайков", R.drawable.likeor,
                percent(stats.totalLikesGiven, 10)));

        list.add(create("oneHundredLikes", tasks.oneHundredLikes, "100 лайков",
                "Поставить 100 лайков", R.drawable.likeor,
                percent(stats.totalLikesGiven, 100)));

        // --- Дни ---
        list.add(create("firstDayOnAccount", tasks.firstDayOnAccount, "Первый день",
                "Зайти в приложение", R.drawable.cabin,
                percent(stats.daysSinceRegistration, 1)));

        list.add(create("tenDaysOnAccount", tasks.tenDaysOnAccount, "10 дней",
                "10 дней в приложении", R.drawable.cabin,
                percent(stats.daysSinceRegistration, 10)));

        list.add(create("oneHundredDaysOnAccount", tasks.oneHundredDaysOnAccount, "100 дней",
                "100 дней в приложении", R.drawable.cabin,
                percent(stats.daysSinceRegistration, 100)));

        // --- Матчи ---
        list.add(create("firstMatch", tasks.firstMatch, "Первый матч",
                "Получить первый матч", R.drawable.messages,
                percent(stats.totalMatches, 1)));

        list.add(create("fiveMatches", tasks.fiveMatches, "5 матчей",
                "Получить 5 матчей", R.drawable.messages,
                percent(stats.totalMatches, 5)));

        list.add(create("datingGuru", tasks.datingGuru, "Гуру знакомств",
                "Получить 50 матчей", R.drawable.messages,
                percent(stats.totalMatches, 50)));

        return list;
    }

    private static Achievement create(String id, boolean completed,
                                      String title, String desc,
                                      int icon, int percent) {
        return new Achievement(title, desc, icon, completed, percent);
    }

    private static int percent(int current, int goal) {
        int p = (int) ((current / (float) goal) * 100f);
        return Math.min(p, 100);
    }

}
