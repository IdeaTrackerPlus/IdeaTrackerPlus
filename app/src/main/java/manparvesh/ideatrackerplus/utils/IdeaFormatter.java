package manparvesh.ideatrackerplus.utils;

import java.io.Reader;
import java.io.Writer;
import java.text.ParseException;
import java.util.List;

import manparvesh.ideatrackerplus.models.Idea;

public interface IdeaFormatter {
    void serialize(List<Idea> ideas, Writer writer);

    String serialize(List<Idea> ideas);

    List<Idea> deserialize(Reader reader) throws ParseException;
}
