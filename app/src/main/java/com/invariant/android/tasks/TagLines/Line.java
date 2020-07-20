package com.invariant.android.tasks.TagLines;

import java.util.ArrayList;

/**
 * Class for easier line drawing. Stores positions (rows) of all tasks
 * {@link com.invariant.android.tasks.Task} that have the same tag and other helpful data.
 */
class Line {

    /**
     * Index of the column this line is in (0 indexed).
     * Used to get X coordinate for a line drawing.
     */
    private Integer lineColumn;
    /**
     * List of all rows (positions in the tasks ListView) that have a one given tag.
     * Used to got Y coordinates for a line drawing.
     */
    private ArrayList<Integer> rows;

    /**
     * Constructor. Sets all up.
     */
    Line() {
        rows = new ArrayList<>();
        this.lineColumn = null;
    }

    /**
     * Getter method.
     * @return All rows. See {@link this#rows}.
     */
    ArrayList<Integer> getRows() {
        return this.rows;
    }

    /**
     * Kinda setter method. Adds new row. See {@link this#rows}.
     * @param row Number of the row (position in the ListView) that has the same tag
     *            as other rows in this object
     */
    void addRow(int row) {
        this.rows.add(row);
    }

    /**
     * Getter method. Used for line drawing (start of the line).
     * @return Position/row of the first element (uppermost) that has tag of this object.
     */
    Integer getFirstRow() {
        if(rows.size() <= 0) return null;
        return rows.get(0);
    }
    /**
     * Getter method. Used for line drawing (end of the line).
     * @return Position/row of the last element (lowest) that has tag of this object.
     */
    Integer getLastRow() {
        if(rows.size() <= 0) return null;
        return rows.get(rows.size()-1);
    }

    /**
     * Setter method.
     *
     * @param column See {@link this#lineColumn}
     *
     * IMPORTANT this needs to be set before getting line column.
     *           See {@link this#isLineColumnSet()}
     */
    void setLineColumn(int column) {
        this.lineColumn = column;
    }

    /**
     * Getter method.
     * @return See {@link this#lineColumn}
     */
    Integer getLineColumn() {
        return this.lineColumn;
    }

    /**
     * @return true if {@link this#lineColumn} is already defined
     */
    boolean isLineColumnSet() {
        return lineColumn != null;
    }

}
