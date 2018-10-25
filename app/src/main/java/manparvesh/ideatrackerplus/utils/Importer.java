package manparvesh.ideatrackerplus.utils;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import manparvesh.ideatrackerplus.database.DatabaseHelper;
import manparvesh.ideatrackerplus.models.Idea;

public class Importer {

    private final DatabaseHelper mDatabaseHelper;
    private final IdeaFormatter mIdeaFormatter;

    public Importer(DatabaseHelper databaseHelper, IdeaFormatter ideaFormatter) {
        mDatabaseHelper = databaseHelper;
        mIdeaFormatter = ideaFormatter;
    }

    public void readFile(String path) throws IOException, ParseException {
        final FileReader reader = new FileReader(path);
        try {
            List<Idea> deserializedIdeas = mIdeaFormatter.deserialize(reader);
            mDatabaseHelper.newEntries(deserializedIdeas);
        } finally {
            reader.close();
        }
    }
}
