package ru.tictactoe.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class PlayingField {

    private int[][] matrix;

    public void setElement(int i, int j, int newElement) throws IllegalArgumentException {
        if (i < 3 && i >= 0 && j < 3 && j >= 0) {
            if (matrix[i][j] != 0 && newElement == 1) {
                throw new IllegalArgumentException("This cell is not empty");
            } else {
                matrix[i][j] = newElement;
            }
        } else {
            throw new IllegalArgumentException("Rows and cols must be 0-2");
        }
    }

}
