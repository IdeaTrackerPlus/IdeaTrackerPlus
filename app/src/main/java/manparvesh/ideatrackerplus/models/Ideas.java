package manparvesh.ideatrackerplus.models;

import java.util.ArrayList;
import java.util.List;

public class Ideas {
    public List<Idea> ideas = new ArrayList<>();

    public Ideas(List<Idea> ideas) {
        this.ideas = ideas;
    }

    public Ideas() {

    }

    public List<Idea> getIdeas() {
        return ideas;
    }

    public void setIdeas(List<Idea> ideas) {
        this.ideas = ideas;
    }
}
