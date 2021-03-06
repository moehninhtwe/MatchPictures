package matchpictures.com.matchpictures.view;

public interface IPhotoView {
    void updateFlip(int totalFlips);

    void flipOver();

    void flipBack();

    void leaveOpen();

    void resetView();

    void finishGame();
}
