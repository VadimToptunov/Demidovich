package com.demidovich.pageObject;

import com.demidovich.R;
import com.demidovich.helpers.CommonActions;

public class ListPasswordsActivityPageObject {

    CommonActions commonActions = new CommonActions();


    public void checkListPasswordsActivityElementsAreDisplayed(){
        commonActions.checkElementsAreDisplayed(R.id.list_item_cardView);
        commonActions.checkElementsAreDisplayed(R.id.list_item_linearLayout_item);
        commonActions.checkElementsAreDisplayed(R.id.list_item_saved_pass_text);
        commonActions.checkElementsAreDisplayed(R.id.list_item_btn_delete);
    }

    public void checkRecyclerViewElementIsDeletedOnPressingDeleteButton(int position){
        commonActions.checkElementDeletedOnPosition(position);
    }
}
