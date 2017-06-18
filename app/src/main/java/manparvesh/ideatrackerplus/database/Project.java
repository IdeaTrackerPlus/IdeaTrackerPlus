package manparvesh.ideatrackerplus.database;

/**
 * This class represents a project to be stored in the TinyDB.
 *
 * It does not contain the ideas, basically just the name
 * of the table in the database that gives access to the ideas.
 */
public class Project {

    // Name of the table in the database, also name of the project
    private String mName;

    // Color preferences
    private int mPrimaryColor;
    private int mSecondaryColor;
    private int mTextColor;
    private int mId;

    private static int counter = 0; //increment the id for new projects

    public Project(String name, int primaryColor, int secondaryColor, int textColor) {
        mName = name;
        mPrimaryColor = primaryColor;
        mSecondaryColor = secondaryColor;
        mTextColor = textColor;
        mId = counter++;
    }

    // GETTERs AND SETTERS //

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getPrimaryColor() {
        return mPrimaryColor;
    }

    public void setPrimaryColor(int primaryColor) {
        this.mPrimaryColor = primaryColor;
    }

    public int getSecondaryColor() {
        return mSecondaryColor;
    }

    public void setSecondaryColor(int secondaryColor) {
        this.mSecondaryColor = secondaryColor;
    }

    public int getId() {
        return mId;
    }

    public static void setCounter(int c) {
        counter = c;
    }
}
