package manparvesh.ideatrackerplus.utils;

import android.support.annotation.StyleRes;

import manparvesh.ideatrackerplus.R;

public class Helper {
    public static @StyleRes int getEditTextStyle(final boolean isDarkTheme){
        return isDarkTheme ? R.style.EditTextTintThemeDark : R.style.EditTextTintTheme;
    }

    public static @StyleRes int getAlertDialogStyle(final boolean isDarkTheme){
        return isDarkTheme ? android.support.v7.appcompat.R.style.Theme_AppCompat_Dialog_Alert : 0;
    }
}
