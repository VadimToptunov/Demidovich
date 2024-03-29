package com.demidovich.pageObject;

import static com.demidovich.helpers.CommonActions.checkElementDeletedOnPosition;
import static com.demidovich.helpers.CommonActions.checkElementsAreDisplayed;

import com.demidovich.R;

public class ListPasswordsActivityPageObject {

    public static void checkListPasswordsActivityElementsAreDisplayed() {
        checkElementsAreDisplayed(R.id.list_item_cardView);
        checkElementsAreDisplayed(R.id.list_item_linearLayout_item);
        checkElementsAreDisplayed(R.id.list_item_saved_pass_text);
        checkElementsAreDisplayed(R.id.list_item_btn_delete);
    }

    public static void checkRecyclerViewElementIsDeletedOnPressingDeleteButton(int position) {
        checkElementDeletedOnPosition(position);
    }
}
