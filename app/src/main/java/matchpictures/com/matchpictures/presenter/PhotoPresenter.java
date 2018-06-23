package matchpictures.com.matchpictures.presenter;

import matchpictures.com.matchpictures.model.PhotoItem;
import matchpictures.com.matchpictures.view.IPhotoView;

public class PhotoPresenter implements IPresenter {
    private IPhotoView iPhotoView;
    private PhotoItem firstPhotoItem;

    public PhotoPresenter(IPhotoView iPhotoView) {
        this.iPhotoView = iPhotoView;
    }

    @Override public void flip(PhotoItem photoItem, boolean isSecondFlip) {
        if(isSecondFlip && isMatch(photoItem, firstPhotoItem)) {
        }
    }

    private boolean isMatch(
        PhotoItem photoItem1, PhotoItem photoItem2) {
        return (photoItem1.getId() == photoItem2.getId());
    }
}
