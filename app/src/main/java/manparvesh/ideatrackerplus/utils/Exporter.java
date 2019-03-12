package manparvesh.ideatrackerplus.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import manparvesh.ideatrackerplus.database.DatabaseHelper;
import manparvesh.ideatrackerplus.models.Idea;

public class Exporter {

    private final DatabaseHelper mDatabaseHelper;
    private final IdeaFormatter mIdeaFormatter;

    public Exporter(DatabaseHelper databaseHelper, IdeaFormatter ideaFormatter) {
        mDatabaseHelper = databaseHelper;
        mIdeaFormatter = ideaFormatter;
    }

    public void saveFile(String path) throws IOException {
        final FileWriter writer = new FileWriter(path, false);

        try {
            List<Idea> ideas = mDatabaseHelper.getAllIdeas();
            mIdeaFormatter.serialize(ideas, writer);
        } finally {
            writer.close();
        }
    }
}
