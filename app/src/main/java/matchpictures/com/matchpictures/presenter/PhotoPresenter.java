package matchpictures.com.matchpictures.presenter;

import android.os.Handler;
import android.util.Log;
import java.util.List;
import matchpictures.com.matchpictures.Board;
import matchpictures.com.matchpictures.model.PhotoItem;
import matchpictures.com.matchpictures.view.IPhotoView;

public class PhotoPresenter implements IPresenter {
    private IPhotoView iPhotoView;
    private PhotoItem firstPhotoItem;
    private int totalFlips = 0;
    private Board board;
    private int firstPhotoXaxis;
    private int firstPhotoYaxis;

    public PhotoPresenter(IPhotoView iPhotoView, List<PhotoItem> photoItems) {
        this.iPhotoView = iPhotoView;
        board = new Board(photoItems);
    }

    public void setPhotoItems(List<PhotoItem> photoItems) {
        board = new Board(photoItems);
    }

    @Override public void onPhotoClicked(int x, int y) {
        PhotoItem photoItem = board.getPhotoItem(x, y);
        if (firstPhotoItem == null) {
            if (!photoItem.isOpen()) {
                firstPhotoItem = photoItem;
                firstPhotoXaxis = x;
                firstPhotoYaxis = y;
                photoItem.setOpen(true);
                board.setPhotoItem(x, y, photoItem);
                iPhotoView.flipOver();
            }
        } else {
            if (!photoItem.isOpen()) {
                iPhotoView.updateFlip(++totalFlips);
                iPhotoView.flipOver();
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    if (!match(photoItem, firstPhotoItem)) {
                        iPhotoView.flipBack();
                        photoItem.setOpen(false);
                        board.setPhotoItem(x, y, photoItem);
                        firstPhotoItem.setOpen(false);
                        board.setPhotoItem(firstPhotoXaxis, firstPhotoYaxis, firstPhotoItem);
                    } else {
                        photoItem.setOpen(true);
                        board.setPhotoItem(x, y, photoItem);
                        iPhotoView.leaveOpen();
                        if (board.isFinished()) {
                            iPhotoView.finishGame();
                        }
                    }
                    firstPhotoItem = null;
                }, 1000);
            }
        }
    }

    @Override public boolean match(PhotoItem photoItem1, PhotoItem photoItem2) {
        return (photoItem1.getId() == photoItem2.getId());
    }

    @Override public void reset() {
        totalFlips = 0;
        iPhotoView.resetView();
    }
}
