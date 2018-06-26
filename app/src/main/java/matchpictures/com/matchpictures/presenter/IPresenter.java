package matchpictures.com.matchpictures.presenter;

import matchpictures.com.matchpictures.model.PhotoItem;

public interface IPresenter {
    void onPhotoClicked(int x, int y);
    boolean match(PhotoItem photoItem1, PhotoItem photoItem2);
    void reset();
}
