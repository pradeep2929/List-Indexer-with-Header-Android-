package com.wafer.quickscroll;

import com.database.realm.models.Contact;

import java.util.List;

/**
 * Created by pankajsoni on 13/02/16.
 */

public class AlphabetIndexerEx {

    protected List<Contact> contactList;

    /**
     * The string of characters that make up the indexing sections.
     */
    protected CharSequence mAlphabet;

    /**
     * Cached length of the alphabet array.
     */
    private int mAlphabetLength;

    /**
     * This contains a cache of the computed indices so far. It will get reset whenever
     * the dataset changes or the cursor changes.
     */

    /**
     * Use a collator to compare strings in a localized manner.
     */
    private java.text.Collator mCollator;

    /**
     * The section array converted from the alphabet string.
     */
    private String[] mAlphabetArray;

    /**
     * Constructs the indexer.
     *
     * @param contactList   the cursor containing the data set
     *                          alphabetically
     * @param alphabet          string containing the alphabet, with space as the first character.
     *                          For example, use the string " ABCDEFGHIJKLMNOPQRSTUVWXYZ" for English indexing.
     *                          The characters must be uppercase and be sorted in ascii/unicode order. Basically
     *                          characters in the alphabet will show up as preview letters.
     */
    public AlphabetIndexerEx(List<Contact> contactList, CharSequence alphabet) {
        this.contactList = contactList;
        mAlphabet = alphabet;
        mAlphabetLength = alphabet.length();
        mAlphabetArray = new String[mAlphabetLength];
        for (int i = 0; i < mAlphabetLength; i++) {
            mAlphabetArray[i] = Character.toString(mAlphabet.charAt(i));
        }

        // Get a Collator for the current locale for string comparisons.
        mCollator = java.text.Collator.getInstance();
        mCollator.setStrength(java.text.Collator.PRIMARY);
    }

    /**
     * Returns the section array constructed from the alphabet provided in the constructor.
     *
     * @return the section array
     */
    public Object[] getSections() {
        return mAlphabetArray;
    }

    /**
     * Sets a new list as the data set and resets the cache of indices.
     *
     * @param list the new list to use as the data set
     */
    public void updateList(List<Contact> list) {
        contactList = list;
    }

    /**
     * Default implementation compares the first character of word with letter.
     */
    protected int compare(String word, String letter) {
        final String firstLetter;
        if (word.length() == 0) {
            firstLetter = " ";
        } else {
            firstLetter = word.substring(0, 1);
        }

        return mCollator.compare(firstLetter, letter);
    }


    public int getPositionForSection(int sectionIndex) {
        if (this.contactList == null || sectionIndex <= 0) return 0;

        if (sectionIndex >= mAlphabetLength) {
            sectionIndex = mAlphabetLength - 1;
        }

        final List<Contact> contactList = this.contactList;


        char letter = mAlphabet.charAt(sectionIndex);
        String targetLetter = Character.toString(letter);

        Integer position = null;

        for (int i = 0; i < contactList.size(); i++) {
            String curName = contactList.get(i).getLocalName();
            if (compare(curName, targetLetter) == 0) {
                position = i;
                break;
            }
        }

        position = (position == null) ? 0 : position;

        return position;
    }

    /**
     * Returns the section index for a given position in the list by querying the item
     * and comparing it with all items in the section array.
     */
    public int getSectionForPosition(int position) {
        if (contactList == null || position < 0) return 0;
        if (contactList.size() <= 0) return 0;
        Contact contact = contactList.get(position);
        boolean isFormatted = contact.isNumberFormatted();
        String curName = contact.getLocalName();

        for (int i = 0; i < mAlphabetLength; i++) {
            char letter = mAlphabet.charAt(i);
            String targetLetter = Character.toString(letter);
            if (compare(curName, targetLetter) == 0) {
                return i;
            }
        }
        return isFormatted ? 0 : getSections().length - 1;
    }
}
