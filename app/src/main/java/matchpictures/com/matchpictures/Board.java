package matchpictures.com.matchpictures;

import java.util.List;
import matchpictures.com.matchpictures.model.PhotoItem;

public class Board {
    private List<PhotoItem> photoItemList;
    private PhotoItem[][] photoBoard = new PhotoItem[4][4];

    public Board(List<PhotoItem> photoItemList) {
        this.photoItemList = photoItemList;
        setBoard();
    }

    private void setBoard() {
        for (int i = 0; i < photoBoard.length; i++) {
            for (int j = 0; j < photoBoard.length; j++) {
                photoBoard[i][j] = photoItemList.get(i * 4 + j);
            }
        }
    }

    public PhotoItem getPhotoItem(int x, int y) {
        return photoBoard[x][y];
    }

    public void setPhotoItem(int x, int y, PhotoItem photoItem) {
        photoBoard[x][y] = photoItem;
    }

    public boolean isFinished() {
        for (int i = 0; i < photoBoard.length; i++) {
            for (int j = 0; j < photoBoard.length; j++) {
                if(!photoBoard[i][j].isOpen()) return false;
            }
        }
        return true;
    }
}
