package com.yogpc.auto_version;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

class VersionObtainer {
    private static final char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final char[] decimal = "0123456789".toCharArray();

    private final int versionCode;
    private final String versionNameDebug;

    // 26進数で表記したときの桁数を返す
    private static int getLen(int i) {
        long j = i & 0xFFFFFFFFL;
        if (j < 26) {
            return 1;
        } else if (j < 676) {
            return 2;
        } else if (j < 17576) {
            return 3;
        } else if (j < 456976) {
            return 4;
        } else if (j < 11881376) {
            return 5;
        } else if (j < 308915776) {
            return 6;
        } else {
            return 7;
        }
    }

    private static String format(Calendar date, int count, boolean longFormat) {
        int l = getLen(count);
        char[] output = new char[(longFormat ? 11 : 5) + l];
        int year = date.getWeekYear();
        output[0] = decimal[(year / 10) % 10];
        output[1] = decimal[year % 10];
        output[2] = 'w';
        int week = date.get(Calendar.WEEK_OF_YEAR);
        output[3] = decimal[week / 10];
        output[4] = decimal[week % 10];
        for (int i = l; i > 0; i--) {
            output[4 + i] = alphabet[count % 26];
            count /= 26;
        }
        if (longFormat) {
            output[5 + l] = decimal[date.get(Calendar.DAY_OF_WEEK) - 1];
            output[6 + l] = '-';
            int hour = date.get(Calendar.HOUR_OF_DAY);
            output[7 + l] = decimal[hour / 10];
            output[8 + l] = decimal[hour % 10];
            int minute = date.get(Calendar.MINUTE);
            output[9 + l] = decimal[minute / 10];
            output[10 + l] = decimal[minute % 10];
        }
        return new String(output);
    }

    VersionObtainer(File dir) {
        String buildNumber = System.getenv("BUILD_NUMBER");
        Calendar now = Calendar.getInstance();
        Calendar fromC = (Calendar) now.clone(); // 週の開始(inclusive)
        fromC.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        fromC.set(Calendar.HOUR_OF_DAY, 0);
        fromC.set(Calendar.MINUTE, 0);
        fromC.set(Calendar.SECOND, 0);
        fromC.set(Calendar.MILLISECOND, 0);
        Calendar toC = (Calendar) fromC.clone(); // 週の終了(次週の開始)(exclusive)
        toC.add(Calendar.WEEK_OF_YEAR, 1);
        long from = fromC.getTimeInMillis() / 1000, to = toC.getTimeInMillis() / 1000;
        int total = 0, thisWeek = 0;
        try {
            Git repo = Git.open(dir);
            for (org.eclipse.jgit.revwalk.RevCommit commit : repo.log().call()) {
                if (from <= commit.getCommitTime() && commit.getCommitTime() < to)
                    thisWeek++;
                else if (buildNumber != null)
                    break;
                total++;
            }
            repo.close();
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
        if (buildNumber != null)
            thisWeek--;
        versionCode = buildNumber != null ? Integer.valueOf(buildNumber) : total;
        versionNameDebug = format(now, thisWeek, buildNumber != null);
    }
    int getCode() {
        return versionCode;
    }
    String getNameDebug() {
        return versionNameDebug;
    }
}
