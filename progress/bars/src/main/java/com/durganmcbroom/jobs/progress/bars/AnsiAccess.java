package com.durganmcbroom.jobs.progress.bars;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface AnsiAccess {
    char ESCAPE = '\033';

    String DOWN_LINE = ESCAPE + "[1B";

    String UP_LINE_SCROLLING = ESCAPE + "M";

    static String RIGHT_COLUMNS(int num) {
        return ESCAPE + "[" + num + "C";
    }

    static String LEFT_COLUMNS(int num) {
        return ESCAPE + "[" + num + "D";
    }

    // All credit to: https://github.com/keqingrong/supports-ansi/blob/master/index.js
    static boolean supportsAnsi() {
        // Check if it is running in the terminal.
        // NOTE: process.stdout.isTTY is not directly available in Java.
        // You might need to use different approaches to check if running in a terminal.
        // For this example, we'll assume it returns true.

        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");

        if (osName.toLowerCase().contains("win")) {
            // Be natively supported on Windows 10 after v.1607 ("Anniversary Update", OS build 14393).
            // Reference: https://api.dart.dev/stable/1.24.3/dart-io/Stdout/supportsAnsiEscapes.html
            String[] osRelease = osVersion.split("\\.");
            int majorVersion = Integer.parseInt(osRelease[0]);
            int buildNumber = Integer.parseInt(osRelease[2]);

            if (majorVersion >= 10 && buildNumber >= 14393) {
                return true;
            }

        }

        // Check if the terminal supports ANSI escape sequences based on a pattern.
        String[] patterns = {
                "^xterm", "^rxvt", "^eterm", "^screen", "^tmux",
                "^vt100", "^vt102", "^vt220", "^vt320",
                "ansi", "scoansi", "cygwin", "linux", "konsole", "bvterm"
        };

        String term = System.getenv("TERM");
        if (term != null && !term.equals("dumb")) {
            String patternRegex = String.join("|", patterns);
            Pattern regex = Pattern.compile(patternRegex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = regex.matcher(term);
            if (matcher.find()) {
                return true;
            }
        }

        // ConEmu can process ANSI X3.64 when the environment variable ConEmuANSI is set to ON.
        // See https://conemu.github.io/en/AnsiEscapeCodes.html#Environment_variable
        String conEmuAnsi = System.getenv("ConEmuANSI");
        if (conEmuAnsi != null && conEmuAnsi.equalsIgnoreCase("ON")) {
            return true;
        }

        // ANSICON provides ANSI escape sequences for Windows console programs.
        // It will create an ANSICON environment variable.
        String ansicon = System.getenv("ANSICON");
        if (ansicon != null && !ansicon.isEmpty()) {
            return true;
        }

        return false;
    }
}
