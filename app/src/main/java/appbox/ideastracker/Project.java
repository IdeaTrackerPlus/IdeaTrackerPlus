package appbox.ideastracker;

/**
 * Created by Nicklos on 23/07/2016.
 */
public class Project {

    private String mName;
    private int mPrimaryColor;
    private int mSecondaryColor;
    private int mTextColor;

    public Project(String name, int primaryColor, int secondaryColor, int textColor) {
        mName = name;
        mPrimaryColor = primaryColor;
        mSecondaryColor = secondaryColor;
        mTextColor = textColor;
    }

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
}
