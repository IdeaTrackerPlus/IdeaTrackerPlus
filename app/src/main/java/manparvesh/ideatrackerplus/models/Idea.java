package manparvesh.ideatrackerplus.models;

import android.support.annotation.StringDef;

import manparvesh.ideatrackerplus.MainActivity;

public class Idea {
    public String title;
    public String note;
    public int priority;
    @IdeaState
    public String state;

    @StringDef({
        DONE,
        LATER,
        IDEAS
    })
    public @interface IdeaState {
    }

    public static final String DONE = "Done";
    public static final String LATER = "Later";
    public static final String IDEAS = "Ideas";

    public Idea() {
    }

    public Idea(String title, String note, int priority, @MainActivity.tab int tab) {
        this.title = title;
        this.note = note;
        this.priority = priority;
        state = tabToState(tab);
    }

    @MainActivity.tab
    public static int stateToTab(@IdeaState String state) {
        @MainActivity.tab final int ret;
        switch (state) {
            case DONE:
                ret = MainActivity.DONE_TAB;
                break;
            case LATER:
                ret = MainActivity.LATER_TAB;
                break;
            case IDEAS:
            default:
                ret = MainActivity.IDEAS_TAB;
        }
        return ret;
    }

    @IdeaState
    public static String tabToState(@MainActivity.tab int tab) {
        @IdeaState final String ret;
        switch (tab) {
            case MainActivity.DONE_TAB:
                ret = DONE;
                break;
            case MainActivity.LATER_TAB:
                ret = LATER;
                break;
            case MainActivity.IDEAS_TAB:
            default:
                ret = IDEAS;
        }
        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Idea idea = (Idea) o;

        if (priority != idea.priority) {
            return false;
        }
        if (!title.equals(idea.title)) {
            return false;
        }
        if (!note.equals(idea.note)) {
            return false;
        }
        return state.equals(idea.state);
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + note.hashCode();
        result = 31 * result + priority;
        result = 31 * result + state.hashCode();
        return result;
    }
}
