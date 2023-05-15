package org.reriva.invstorage.Utils;

import org.reriva.invstorage.Classes.IEHelper;

import java.util.ArrayList;

public class ReverseArrayList {

    // Reverses an arraylist of type IEHelper
    public ArrayList<IEHelper> reverseArrayList(ArrayList<IEHelper> alist)
    {
        for (int i = 0; i < alist.size() / 2; i++) {
            IEHelper temp = alist.get(i);
            alist.set(i, alist.get(alist.size() - i - 1));
            alist.set(alist.size() - i - 1, temp);
        }

        return alist;
    }
}
