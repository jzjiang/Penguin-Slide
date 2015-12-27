import java.applet.*;
import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Penguin Slide Board
 * 
 * @author Jessica Jiang & Kitty Su
 * @version January 6, 2014
 */
public class PenguinSlideBoard extends JPanel implements MouseListener,
		MouseMotionListener
{

	// Program constants
	private final int NO_OF_PENGUINS = 6;
	private final int EMPTY = 0;
	private final int POP = -1;
	private final int NO_OF_ROWS = 8;
	private final int NO_OF_COLUMNS = 8;
	private final int SQUARE_SIZE = 80;
	private final double THERMOMETER_BASE_SCORE = 2500;
	private final Image penguins[] = new Image[7];

	// Program variables
	private int fromRow, fromColumn, toRow, toColumn;
	private Image mainMenu, pop, penguinLogo, menuButton, inGameMenu,
			resumeButton, newGameButton, instructionsButton, instructionsOne,
			instructionsTwo, instructionsThree, backMenuButton, nextButton,
			backButton, thermometer, thermometerLines, lose, loseHighScore,
			loseHighScoreSubmit, viewHighScore, highScoreImage, about, title,
			scoreImage, levelImage, levelUpImage, noMoreMovesImage, hintButton;
	private int[][] board;
	private int selectedPiece, switchedPiece, shiftRow, shiftColumn,
			draggedXPos, draggedYPos;
	private boolean gameOver, inGameMenuScreen, inGame, newHighScore,
			highScoreSubmit, levelUp, showHighScore, noMoreMoves;
	private int instructionsScreen = 0;
	private int level, score;
	private double thermometerScore;
	private Point pickUp;
	private int thermometerStart, thermometerSize;
	private boolean timerOn;
	private int time;
	private Timer timer;
	private AudioClip backgroundMusic, splashSound, loseSound, levelUpSound;
	private String player;
	private int highScore;
	private TextField nameBox;

	// imageBackground from www.stockfreeimages.com
	Image imageBackground = new ImageIcon("images\\background.jpg").getImage();
	Image boardBackground = new ImageIcon("images\\Ice.png").getImage();
	int imageHeight = imageBackground.getHeight(PenguinSlideBoard.this);
	int imageWidth = imageBackground.getWidth(PenguinSlideBoard.this);

	final Dimension BOARD_SIZE = new Dimension(imageWidth, imageHeight);

	/**
	 * Constructs a new frame and sets up the game.
	 */
	public PenguinSlideBoard()
	{

		// Sets up the board area, loads in piece images and starts a new game
		setPreferredSize(BOARD_SIZE);

		// Sets the 2D array which represents the board.
		board = new int[NO_OF_ROWS + 2][NO_OF_COLUMNS + 2];

		loadResources();
		addMouseListener(this);
		addMouseMotionListener(this);
		setFont(new Font("Arial", Font.BOLD, 45));
		setFocusable(true);
		requestFocusInWindow();

		// Set up the timer variables
		timerOn = false;
		time = 0;
		thermometerScore = 1;

		timer = new Timer(1000, new TimerEventHandler());

	}

	/**
	 * Loads up the images and sound files
	 * 
	 */
	private void loadResources()
	{
		// Initialize penguins
		for (int i = 1; i < penguins.length; i++)
		{
			penguins[i] = new ImageIcon("images\\penguin" + i + ".png")
					.getImage();
		}

		// Load up other images
		pop = new ImageIcon("images\\pop.png").getImage();
		penguinLogo = new ImageIcon("images\\penguinLogo.png").getImage();
		menuButton = new ImageIcon("images\\menuButton.png").getImage();
		inGameMenu = new ImageIcon("images\\inGameMenu.png").getImage();
		resumeButton = new ImageIcon("images\\resumeButton.png").getImage();
		newGameButton = new ImageIcon("images\\newGameButton.png").getImage();
		instructionsButton = new ImageIcon("images\\instructionsButton.png")
				.getImage();
		instructionsOne = new ImageIcon("images\\instructionsOne.png")
				.getImage();
		instructionsTwo = new ImageIcon("images\\instructionsTwo.png")
				.getImage();
		instructionsThree = new ImageIcon("images\\instructionsThree.png")
				.getImage();
		backMenuButton = new ImageIcon("images\\backMenuButton.png").getImage();
		nextButton = new ImageIcon("images\\nextButton.png").getImage();
		backButton = new ImageIcon("images\\backButton.png").getImage();
		mainMenu = new ImageIcon("images\\mainMenu.png").getImage();
		thermometer = new ImageIcon("images\\thermometer.png").getImage();
		thermometerLines = new ImageIcon("images\\thermometerLines.png")
				.getImage();
		lose = new ImageIcon("images\\lose.png").getImage();
		loseHighScore = new ImageIcon("images\\loseHighScore.png").getImage();
		loseHighScoreSubmit = new ImageIcon("images\\loseHighScoreSubmit.png")
				.getImage();
		about = new ImageIcon("images\\about.png").getImage();
		title = new ImageIcon("images\\title.png").getImage();
		levelImage = new ImageIcon("images\\level.png").getImage();
		scoreImage = new ImageIcon("images\\score.png").getImage();
		levelUpImage = new ImageIcon("images\\levelUp.png").getImage();
		viewHighScore = new ImageIcon("images\\viewHighScore.png").getImage();
		highScoreImage = new ImageIcon("images\\highScore.png").getImage();
		noMoreMovesImage = new ImageIcon("images\\noMoreMoves.png").getImage();
		hintButton = new ImageIcon("images\\hintButton.png").getImage();

		// Load audio files - files from Youtube's Free Music library and
		// freesound.org
		backgroundMusic = Applet
				.newAudioClip(getCompleteURL("audio\\backgroundMusic.wav"));
		splashSound = Applet.newAudioClip(getCompleteURL("audio\\splash.wav"));
		loseSound = Applet.newAudioClip(getCompleteURL("audio\\lose.wav"));
		levelUpSound = Applet.newAudioClip(getCompleteURL("audio\\levelUp.wav"));
	}

	/**
	 * Starts a new game
	 */
	public void newGame()
	{
		// Set the in game to true and game over to false
		inGame = true;
		gameOver = false;

		// Start the music
		backgroundMusic.loop();

		// Reset the score and level to 0 and 1
		score = 0;
		level = 1;

		// Reset the thermometer to 5000 and the graphics to full
		thermometerScore = THERMOMETER_BASE_SCORE * level;
		thermometerStart = 50;
		thermometerSize = 890;

		// Shuffle the penguins
		shuffle();

		// Remove any existing 3 in a rows
		removeExisting();

		// If there aren't any possible moves, re-shuffle the board
		while (!checkForPossibleMoves())
		{
			noMoreMoves = true;
			paintImmediately(0, 0, getWidth(), getHeight());
			delay(1000);
			shuffle();
			removeExisting();
			noMoreMoves = false;
		}

		// Set the dragged piece to empty
		selectedPiece = EMPTY;

		// Update the screen
		repaint();
	}

	/**
	 * Shuffles the board
	 */
	public void shuffle()
	{
		// Go through the entire board and set all the values to a random
		// penguin
		for (int row = 1; row < board.length - 1; row++)
			for (int col = 1; col < board[row].length - 1; col++)
			{
				int randInt = (int) (Math.random() * NO_OF_PENGUINS + 1);
				board[row][col] = randInt;
			}
	}

	/**
	 * Checks if there is a possible move on the board
	 * 
	 * @return where there are any possible moves or not
	 */
	public boolean checkForPossibleMoves()

	{
		for (int row = 1; row < NO_OF_ROWS; row++)
			for (int column = 1; column < NO_OF_COLUMNS; column++)

			{
				// two connected horizontally
				if (board[row][column] == board[row][column + 1]
						&& (board[row - 1][column + 2] == board[row][column] // upper-right
								|| board[row + 1][column + 2] == board[row][column] // lower-right
								|| board[row - 1][column - 1] == board[row][column] // upper-left
								|| board[row + 1][column - 1] == board[row][column] // lower-left
								|| column + 3 <= NO_OF_COLUMNS
								&& board[row][column + 3] == board[row][column] // right
						|| column - 2 >= 1
								&& board[row][column - 2] == board[row][column] // left
						))
					return true;

				// one between two
				if (board[row][column] == board[row][column + 2]
						&& (board[row - 1][column + 1] == board[row][column] // upper
						|| board[row + 1][column + 1] == board[row][column] // lower
						))
					return true;

				// two connected vertically
				if (board[row][column] == board[row + 1][column]
						&& (board[row - 1][column + 1] == board[row][column] // upper-right
								|| board[row + 2][column + 1] == board[row][column] // lower-right
								|| board[row - 1][column - 1] == board[row][column] // upper-left
								|| board[row + 2][column - 1] == board[row][column] // lower-left
								|| row - 2 >= 1
								&& (board[row - 2][column] == board[row][column])// upper
						|| row + 3 <= NO_OF_ROWS
								&& (board[row + 3][column] == board[row][column])// lower
						))
					return true;

				// one between two
				if (board[row][column] == board[row + 2][column]
						&& (board[row + 1][column + 1] == board[row][column] // right
						|| board[row - 1][column - 1] == board[row][column] // left
						))
					return true;

			}
		return false;

	}

	/**
	 * Processes through the entire board and removes three or more in a row or
	 * column
	 */
	public void removeExisting()
	{
		int popCount;

		// Keep removing until there are none left
		do
		{
			popCount = 0;

			// Go through each row and column
			for (int row = 1; row <= NO_OF_ROWS; row++)
			{
				for (int column = 1; column <= NO_OF_COLUMNS; column++)
				{

					int newColumn = column + 1;
					int newRow = row + 1;
					int pointsEarned;

					// Create a variable to count the length of the same colored
					// penguins in a row
					int count = 1;

					// Check for a connect three or more on the right side of
					// the current position
					for (; newColumn <= NO_OF_COLUMNS
							&& board[row][newColumn] == board[row][column]; newColumn++)
						count++;

					// Subtract due to last loop
					newColumn--;

					int count2 = 1;

					// Check for a three or more under the current
					// position
					for (; newRow <= NO_OF_ROWS
							&& board[newRow][column] == board[row][column]; newRow++)
						count2++;

					// Subtract due to last loop
					newRow--;

					// Pop all the required penguins if there are 3 in a row
					if (count >= 3 && count2 >= 3)
					{

						// Update the screen
						paintImmediately(0, 0, getWidth(), getHeight());
						delay(500);

						// Pop the required penguins
						pop(row, column, row, newColumn);
						pop(row, column, newRow, column);
						splashSound.play();

						// Calculate the amount of points earned
						pointsEarned = calculateScore(row, column, row,
								newColumn);
						pointsEarned += calculateScore(row, column, newRow,
								column);

						// Subtract the amount of points earned from the
						// thermometer (lower the thermometer)
						thermometerScore -= pointsEarned;

						// Update the screen
						paintImmediately(0, 0, getWidth(), getHeight());
						delay(1050);

						// Regenerate the penguins
						regenerate(row, column, row, newColumn);
						regenerate(row, column, newRow, column);

						// Calculate the amount to lower the graphical
						// thermometer by (number of pixels)
						double changeValue = Math
								.ceil(thermometerSize
										- ((thermometerScore / (THERMOMETER_BASE_SCORE * level)) * 890));

						// Adjust the thermometer accordingly
						thermometerStart += changeValue;
						thermometerSize -= changeValue;

						// Start the timer
						timerOn = true;
						timer.start();

						popCount++;
					}

					// Pop all the required penguins if there are 3 in a row
					else if (count >= 3)
					{

						// Update the screen
						paintImmediately(0, 0, getWidth(), getHeight());
						delay(500);

						// Pop the required penguins
						pop(row, column, row, newColumn);
						splashSound.play();

						// Calculate the amount of points earned
						pointsEarned = calculateScore(row, column, row,
								newColumn);

						// Subtract the amount of points earned from the
						// thermometer (lower the thermometer)
						thermometerScore -= pointsEarned;

						// Update the screen
						paintImmediately(0, 0, getWidth(), getHeight());
						delay(1050);

						// Regenerate the popped penguins
						regenerate(row, column, row, newColumn);

						// Calculate the amount to lower the graphical
						// thermometer by (number of pixels)
						double changeValue = Math
								.ceil(thermometerSize
										- ((thermometerScore / (THERMOMETER_BASE_SCORE * level)) * 890));

						// Adjust the thermometer accordingly
						thermometerStart += changeValue;
						thermometerSize -= changeValue;

						// Start the timer
						timerOn = true;
						timer.start();

						popCount++;
					}

					// Pop all the required penguins if there are 3 in a row
					else if (count2 >= 3)
					{

						// Update the screen
						paintImmediately(0, 0, getWidth(), getHeight());
						delay(500);

						// Pop the required penguins
						pop(row, column, newRow, column);
						splashSound.play();

						// Calculate the amount of points earned
						pointsEarned = calculateScore(row, column, newRow,
								column);

						// Subtract the amount of points earned from the
						// thermometer (lower the thermometer)
						thermometerScore -= pointsEarned;

						// Update the screen
						paintImmediately(0, 0, getWidth(), getHeight());
						delay(1050);

						// Regenerate the popped penguins
						regenerate(row, column, newRow, column);

						// Calculate the amount to lower the graphical
						// thermometer by (number of pixels)
						double changeValue = Math
								.ceil(thermometerSize
										- ((thermometerScore / (THERMOMETER_BASE_SCORE * level)) * 890));

						// Adjust the thermometer accordingly
						thermometerStart += changeValue;
						thermometerSize -= changeValue;

						// Start the timer
						timerOn = true;
						timer.start();

						popCount++;
					}

					// If the user goes to the next level (brings the
					// thermometer down to zero)
					if (thermometerScore <= 0)
					{
						// Draw the level up image
						levelUp = true;
						paintImmediately(0, 0, getWidth(), getHeight());
						// Play sound
						backgroundMusic.stop();
						levelUpSound.play();
						backgroundMusic.loop();
						delay(1000);
						levelUp = false;

						// Add to the level
						level++;

						// Shuffle the board
						shuffle();

						// Add score bonus
						score += level * 100;

						// Adjust thermometer accordingly
						thermometerStart = 50;
						thermometerSize = 890;
						thermometerScore = THERMOMETER_BASE_SCORE * level;

						// Stop the timer
						timer.stop();
						timerOn = false;

						// Remove any existing 3 or more in a rows
						removeExisting();
					}
				}
			}
		}
		while (popCount > 0);
	}

	/**
	 * 
	 * @param fromRow the row value of the penguin that the user wants to pop
	 * @param fromColumn the column value of the penguin that the user wants to
	 *            pop
	 * @param toRow the row value of the penguin that the user wants to switch
	 * @param toColumn the column value of the penguin that the user wants to
	 *            switch
	 * @return the coordinates of the array to pop
	 */
	public int[] findPop(int fromRow, int fromColumn, int toRow, int toColumn)
	{
		// Create an array to store all the coordinates
		int[] popCoordinates = new int[8];

		// Create a variable to count the number of penguins in a row
		int count = 1;
		int count2 = 1;
		int column = toColumn - 1;
		int targetPenguin = board[toRow][toColumn];

		// Check if the move is possible horizontally (R to L)
		for (; column >= 1 && targetPenguin == board[toRow][column]; column--)
			count++;

		// Add due to last loop
		column++;

		// Keep track of the position of the leftmost piece
		popCoordinates[0] = toRow;
		popCoordinates[1] = column;

		// Check if the move is possible horizontally (L to R)
		for (column = toColumn + 1; column <= NO_OF_COLUMNS
				&& targetPenguin == board[toRow][column]; column++)
			count++;

		// Subtract due to last loop
		column--;

		// Keep track of the position of the rightmost piece
		popCoordinates[2] = toRow;
		popCoordinates[3] = column;

		int row = toRow - 1;

		// Check if the move is possible vertically (B to T)
		for (; row >= 1 && targetPenguin == board[row][toColumn]; row--)
			count2++;

		// Add due to last loop
		row++;

		// Keep track of the position of the topmost piece
		popCoordinates[4] = row;
		popCoordinates[5] = toColumn;

		// Check if the move is possible vertically (T to B)
		for (row = toRow + 1; row <= NO_OF_ROWS
				&& targetPenguin == board[row][toColumn]; row++)
			count2++;

		// Subtract due to last loop
		row--;

		// Keep track of the position of the bottom most piece
		popCoordinates[6] = row;
		popCoordinates[7] = toColumn;

		// If the move does form 3 or more in a row, then the move is possible
		if (count >= 3 && count2 >= 3)
			return popCoordinates;

		// If it is a horizontal pop, set the vertical coordinates to 0
		else if (count >= 3)
		{
			popCoordinates[4] = 0;
			popCoordinates[5] = 0;
			popCoordinates[6] = 0;
			popCoordinates[7] = 0;
			return popCoordinates;
		}

		// If it is a horizontal pop, set the horizontal coordinates to 0
		else if (count2 >= 3)
		{
			popCoordinates[0] = 0;
			popCoordinates[1] = 0;
			popCoordinates[2] = 0;
			popCoordinates[3] = 0;
			return popCoordinates;
		}

		// Return -1 if the move is not possible
		popCoordinates[0] = -1;
		return popCoordinates;
	}

	/**
	 * Sets the given penguins to POP
	 * 
	 * @param firstRow the row position of the first penguin in the pop
	 * @param firstColumn the column position of the first penguin in the pop
	 * @param lastRow the row position of the last penguin in the pop
	 * @param lastColumn the column position of the last penguin in the pop
	 */
	private void pop(int firstRow, int firstColumn, int lastRow, int lastColumn)
	{

		// Go through the given row/column and set them all to POP
		for (int row = firstRow; row <= lastRow; row++)
			for (int column = firstColumn; column <= lastColumn; column++)
				board[row][column] = POP;

	}

	/**
	 * Shifts all the penguins down and generates new ones
	 * 
	 * @param firstRow the row position of the first penguin in the pop
	 * @param firstColumn the column position of the first penguin in the pop
	 * @param lastRow the row position of the last penguin in the pop
	 * @param lastColumn the column position of the last penguin in the pop
	 */
	private void regenerate(int firstRow, int firstColumn, int lastRow,
			int lastColumn)
	{

		// Variable to track the row above
		int shiftDown = firstRow;

		// Go through the given row/column and shift everything down
		for (int row = lastRow; row >= 1; row--)
		{

			// Only keep checking rows above while shiftDown hasn't reached the
			// border
			if (shiftDown >= 1)
				shiftDown--;

			for (int column = firstColumn; column <= lastColumn; column++)
			{

				// If the column doesn't change, keep them the same
				if (lastColumn == firstColumn)
					column = firstColumn;

				// If the piece above the current one is empty (the border) then
				// generate new random penguins
				if (board[shiftDown][column] == EMPTY)
				{
					int randInt = (int) (Math.random() * NO_OF_PENGUINS + 1);
					board[row][column] = randInt;
				}

				// Otherwise, shift the penguins down
				else
					board[row][column] = board[shiftDown][column];
			}
		}
	}

	/**
	 * Calculates the score
	 * 
	 * @param firstRow the row position of the first penguin in the pop
	 * @param firstColumn the column position of the first penguin in the pop
	 * @param lastRow the row position of the last penguin in the pop
	 * @param lastColumn the column position of the last penguin in the pop
	 * @return the points scored
	 */
	private int calculateScore(int firstRow, int firstColumn, int lastRow,
			int lastColumn)
	{
		// Make sure the penguin is on the board
		if (lastRow > 0 && firstRow > 0 && lastColumn > 0 && firstColumn > 0)
		{

			// Calculate how many points were earned (each penguin is worth 50
			// points
			int pointsEarned = (lastRow - firstRow + 1)
					* (lastColumn - firstColumn + 1) * 50;

			// Add the the score
			score += pointsEarned;

			// Return the amount of points earned
			return pointsEarned;
		}

		// Return zero if nothing was popped
		return 0;

	}

	/**
	 * Check if the user's move is possible and makes the move. Adds to the
	 * score.
	 */
	private void makeMove()
	{
		// Check if place to move is within a cross shape of the original
		// piece(i.e. not diagonal or more than 1 piece away)
		if (toColumn == fromColumn
				&& (toRow - 1 == fromRow || toRow + 1 == fromRow)
				|| (toRow == fromRow && (toColumn + 1 == fromColumn || toColumn - 1 == fromColumn)))
		{

			// Switch the pieces
			board[fromRow][fromColumn] = board[toRow][toColumn];
			board[toRow][toColumn] = selectedPiece;

			// Find the coordinates for the pop
			int[] popCoordinates = findPop(fromRow, fromColumn, toRow, toColumn);
			int[] popCoordinates2 = findPop(toRow, toColumn, fromRow,
					fromColumn);

			int pointsEarned;

			// If the pop is possible (the penguin the user clicked is part of
			// the pop)
			if (popCoordinates[0] != -1)
			{
				// Update the screen with the new positions
				paintImmediately(0, 0, getWidth(), getHeight());
				delay(500);

				// Pop the penguins
				pop(popCoordinates[0], popCoordinates[1], popCoordinates[2],
						popCoordinates[3]);
				pop(popCoordinates[4], popCoordinates[5], popCoordinates[6],
						popCoordinates[7]);
				splashSound.play();

				// Calculate the score for the penguins popped
				pointsEarned = calculateScore(popCoordinates[0],
						popCoordinates[1], popCoordinates[2], popCoordinates[3]);
				pointsEarned += calculateScore(popCoordinates[4],
						popCoordinates[5], popCoordinates[6], popCoordinates[7]);

				// Lower the thermometer
				thermometerScore -= pointsEarned;
				double changeValue = Math
						.ceil(thermometerSize
								- ((thermometerScore / (THERMOMETER_BASE_SCORE * level)) * 890));
				thermometerStart += changeValue;
				thermometerSize -= changeValue;
				paintImmediately(0, 0, getWidth(), getHeight());
				delay(1050);

				// Regenerate the penguins
				regenerate(popCoordinates[0], popCoordinates[1],
						popCoordinates[2], popCoordinates[3]);
				regenerate(popCoordinates[4], popCoordinates[5],
						popCoordinates[6], popCoordinates[7]);

				// Remove any new 3 in a rows formed
				removeExisting();

				// Reset the timer to 0 after a move is made
				time = 0;

				// Start the timer
				timerOn = true;
				timer.start();

			}

			// If the pop is possible (the penguin the dragged to is part of the
			// pop)
			else if (popCoordinates2[0] != -1)
			{

				// Update the screen with the new positions
				paintImmediately(0, 0, getWidth(), getHeight());
				delay(500);

				// Pop the penguins
				pop(popCoordinates2[0], popCoordinates2[1], popCoordinates2[2],
						popCoordinates2[3]);
				pop(popCoordinates2[4], popCoordinates2[5], popCoordinates2[6],
						popCoordinates2[7]);
				splashSound.play();

				// Calculate the score for the penguins popped
				pointsEarned = calculateScore(popCoordinates2[0],
						popCoordinates2[1], popCoordinates2[2],
						popCoordinates2[3]);
				pointsEarned += calculateScore(popCoordinates2[4],
						popCoordinates2[5], popCoordinates2[6],
						popCoordinates2[7]);

				// Lower the thermometer
				thermometerScore -= pointsEarned;
				double changeValue = Math
						.ceil(thermometerSize
								- ((thermometerScore / (THERMOMETER_BASE_SCORE * level)) * 890));
				thermometerStart += changeValue;
				thermometerSize -= changeValue;

				paintImmediately(0, 0, getWidth(), getHeight());
				delay(1050);

				// Regenerate the penguins
				regenerate(popCoordinates2[0], popCoordinates2[1],
						popCoordinates2[2], popCoordinates2[3]);
				regenerate(popCoordinates2[4], popCoordinates2[5],
						popCoordinates2[6], popCoordinates2[7]);
				splashSound.play();

				// Remove any new 3 in a rows formed
				removeExisting();

				// Reset to 0 after a move is made
				time = 0;

				// Start the timer
				timerOn = true;
				timer.start();

			}

			// If the pop is not possible
			else
			{
				// Switch the pieces back
				board[toRow][toColumn] = board[fromRow][fromColumn];
				board[fromRow][fromColumn] = selectedPiece;

			}
		}

		// If the pop is not possible
		else
		{
			// Don't move the pieces
			board[fromRow][fromColumn] = selectedPiece;
		}

		// If there aren't any possible moves, re-shuffle the board
		while (!checkForPossibleMoves())
		{
			noMoreMoves = true;
			paintImmediately(0, 0, getWidth(), getHeight());
			delay(1000);
			shuffle();
			removeExisting();
			noMoreMoves = false;
		}

		// If the user goes to the next level (brings the
		// thermometer down to zero)
		if (thermometerScore <= 0)
		{
			// Draw the level up image
			levelUp = true;
			paintImmediately(0, 0, getWidth(), getHeight());
			// Play sound
			backgroundMusic.stop();
			levelUpSound.play();
			backgroundMusic.loop();
			delay(1000);
			levelUp = false;

			// Add to the level
			level++;

			// Shuffle the board
			shuffle();

			// Add score bonus
			score += level * 100;

			// Adjust thermometer accordingly
			thermometerStart = 50;
			thermometerSize = 890;
			thermometerScore = THERMOMETER_BASE_SCORE * level;

			// Stop the timer
			timer.stop();
			timerOn = false;

			// Remove any existing 3 or more in a rows
			removeExisting();
		}

		// Reset the mouse variables
		fromRow = -1;
		fromColumn = -1;
		toRow = -1;
		toColumn = -1;

		// Update the screen
		repaint();

	}

	/**
	 * Checks if the use has gotten a new high score
	 * 
	 * @throws IOException
	 */
	private void checkHighScore() throws IOException
	{

		// Read in the file information
		Scanner fileIn = new Scanner(new File("highScore.txt"));
		player = fileIn.nextLine();
		highScore = fileIn.nextInt();
		fileIn.close();

		// If the user has gotten a new high score
		if (score > highScore)
		{
			newHighScore = true;

			// Get the user's name
			nameBox = new TextField();
			nameBox.setBounds(467, 309, 217, 35);
			nameBox.setFont(new Font("Arial", Font.PLAIN, 14));
			add(nameBox);
		}
	}

	/**
	 * Records high score to text file
	 * 
	 * @throws IOException
	 */
	private void recordHighScore() throws IOException
	{
		// Write to the file
		PrintWriter fileOut = new PrintWriter(new FileWriter("highScore.txt"));
		fileOut.println(player);
		fileOut.println(highScore);
		fileOut.close();
	}

	/**
	 * Delays the given number of milliseconds
	 * 
	 * @param milliSec The number of milliseconds to delay
	 */
	private void delay(int milliSec)
	{
		try
		{
			Thread.sleep(milliSec);
		}
		catch (InterruptedException e)
		{
		}
	}

	/**
	 * Giving users a possible move, when they click on the hint button
	 */
	private void giveHint()
	{
		// Create variables to hold the positions of the "from penguin" to the
		// "to penguin"
		int fromR = 0;
		int fromC = 0;
		int toR = 0;
		int toC = 0;

		// Go through the array to find the first possible move
		// Record those coordinates and exit the loop
		for (int row = 1; row <= NO_OF_ROWS; row++)
		{
			for (int column = 1; column <= NO_OF_COLUMNS; column++)
			{

				// Two connected horizontally
				// Upper right
				if (column + 2 <= NO_OF_COLUMNS && row - 1 >= 1)
				{
					if (board[row][column] == board[row][column + 1]
							&& board[row][column] == board[row - 1][column + 2])
					{
						fromR = row - 1;
						fromC = column + 2;
						toR = row;
						toC = column + 2;
						row = NO_OF_ROWS;
						column = NO_OF_COLUMNS;
					}
				}

				// Lower right
				if (column + 2 <= NO_OF_COLUMNS && row + 1 <= NO_OF_ROWS)
				{
					if (board[row][column] == board[row][column + 1]
							&& board[row][column] == board[row + 1][column + 2])
					{
						fromR = row + 1;
						fromC = column + 2;
						toR = row;
						toC = column + 2;
						row = NO_OF_ROWS;
						column = NO_OF_COLUMNS;
					}
				}

				if (column - 1 >= 1 && column + 1 <= NO_OF_COLUMNS
						&& row - 1 >= 1)
				{
					// Upper left
					if (board[row][column] == board[row][column + 1]
							&& board[row][column] == board[row - 1][column - 1])
					{
						fromR = row - 1;
						fromC = column - 1;
						toR = row;
						toC = column - 1;
						row = NO_OF_ROWS;
						column = NO_OF_COLUMNS;
					}
				}

				// Lower left
				if (column - 1 >= 1 && column + 1 <= NO_OF_COLUMNS
						&& row + 1 <= NO_OF_ROWS)
				{
					if (board[row][column] == board[row][column + 1]
							&& board[row][column] == board[row + 1][column - 1])
					{
						fromR = row + 1;
						fromC = column - 1;
						toR = row;
						toC = column - 1;
						row = NO_OF_ROWS;
						column = NO_OF_COLUMNS;
					}
				}

				// Right side
				if (column + 3 <= NO_OF_COLUMNS)
				{
					if (board[row][column] == board[row][column + 1]
							&& board[row][column] == board[row][column + 3])
					{
						fromR = row;
						fromC = column + 3;
						toR = row;
						toC = column + 2;
						row = NO_OF_ROWS;
						column = NO_OF_COLUMNS;
					}
				}

				// Left side
				if (column - 2 >= 1 && column + 1 <= NO_OF_COLUMNS)
				{
					if (board[row][column] == board[row][column + 1]
							&& board[row][column] == board[row][column - 2])
					{
						fromR = row;
						fromC = column - 2;
						toR = row;
						toC = column - 1;
						row = NO_OF_ROWS;
						column = NO_OF_COLUMNS;
					}
				}

				// One between two
				// Upper
				if (column + 2 <= NO_OF_COLUMNS && row - 1 >= 1)
				{
					if (board[row][column] == board[row][column + 2]
							&& board[row][column] == board[row - 1][column + 1])
					{
						fromR = row - 1;
						fromC = column + 1;
						toR = row;
						toC = column + 1;
						row = NO_OF_ROWS;
						column = NO_OF_COLUMNS;
					}
				}

				// Lower
				if (column + 2 <= NO_OF_COLUMNS && row + 1 <= NO_OF_ROWS)
				{
					if (board[row][column] == board[row][column + 2]
							&& board[row][column] == board[row + 1][column + 1])
					{
						fromR = row + 1;
						fromC = column + 1;
						toR = row;
						toC = column + 1;
						row = NO_OF_ROWS;
						column = NO_OF_COLUMNS;
					}
				}

				if (column + 1 <= NO_OF_COLUMNS && row - 1 >= 1
						&& row + 1 <= NO_OF_ROWS)
				{
					// Two connected vertically
					// Upper right
					if (board[row][column] == board[row + 1][column]
							&& board[row][column] == board[row - 1][column + 1])
					{
						fromR = row - 1;
						fromC = column + 1;
						toR = row - 1;
						toC = column;
						row = NO_OF_ROWS;
						column = NO_OF_COLUMNS;
					}
				}

				// Lower right
				if (row + 2 <= NO_OF_ROWS && column + 1 <= NO_OF_COLUMNS)
				{
					if (board[row][column] == board[row + 1][column]
							&& board[row][column] == board[row + 2][column + 1])
					{
						fromR = row + 2;
						fromC = column + 1;
						toR = row + 2;
						toC = column;
						row = NO_OF_ROWS;
						column = NO_OF_COLUMNS;
					}
				}

				// Lower left
				if (row + 2 <= NO_OF_ROWS && column - 1 >= 1)
				{
					if (board[row][column] == board[row + 1][column]
							&& board[row][column] == board[row + 2][column - 1])
					{
						fromR = row + 2;
						fromC = column - 1;
						toR = row + 2;
						toC = column;
						row = NO_OF_ROWS;
						column = NO_OF_COLUMNS;
					}
				}

				if (column - 1 >= 1 && row - 1 >= 1 && row + 1 <= NO_OF_ROWS)
				{
					// Upper left
					if (board[row][column] == board[row + 1][column]
							&& board[row][column] == board[row - 1][column - 1])
					{
						fromR = row - 1;
						fromC = column - 1;
						toR = row - 1;
						toC = column;
						row = NO_OF_ROWS;
						column = NO_OF_COLUMNS;
					}
				}

				// Upper
				if (row - 2 >= 1 && row + 1 <= NO_OF_ROWS)
				{
					if (board[row][column] == board[row + 1][column]
							&& board[row - 2][column] == board[row][column])
					{
						fromR = row - 2;
						fromC = column;
						toR = row - 1;
						toC = column;
						row = NO_OF_ROWS;
						column = NO_OF_COLUMNS;
					}
				}

				// Lower
				if (row + 3 <= NO_OF_ROWS)
				{
					if (board[row][column] == board[row + 1][column]
							&& board[row + 3][column] == board[row][column])
					{
						fromR = row + 3;
						fromC = column;
						toR = row + 2;
						toC = column;
						row = NO_OF_ROWS;
						column = NO_OF_COLUMNS;
					}
				}

				// One between two
				// Right
				if (column + 1 <= NO_OF_COLUMNS && row + 2 <= NO_OF_ROWS)
				{
					if (board[row][column] == board[row + 2][column]
							&& board[row + 1][column + 1] == board[row][column])
					{
						fromR = row + 1;
						fromC = column + 1;
						toR = row + 1;
						toC = column;
						row = NO_OF_ROWS;
						column = NO_OF_COLUMNS;
					}
				}
				// Left
				if (column - 1 >= 1 && row - 1 >= 1 && row + 2 <= NO_OF_ROWS)
				{
					if (board[row][column] == board[row + 2][column]
							&& board[row - 1][column - 1] == board[row][column])
					{
						fromR = row + 1;
						fromC = column - 1;
						toR = row + 1;
						toC = column;
						row = NO_OF_ROWS;
						column = NO_OF_COLUMNS;
					}
				}
			}
		}

		// Set up the temporary variables to remember the penguin colors
		int fromTemp = board[fromR][fromC];
		int toTemp = board[toR][toC];

		// Create a variable to count how many times the penguins flash
		int cycleCount = 1;

		// Let the penguin flash 3 times
		while (cycleCount <= 3)
		{
			// Set both elements to empty
			board[fromR][fromC] = EMPTY;
			board[toR][toC] = EMPTY;
			paintImmediately(0, 0, getWidth(), getHeight());

			// Delay for a quarter of a second
			delay(250);

			// Set the elements back to penguins
			board[fromR][fromC] = fromTemp;
			board[toR][toC] = toTemp;
			paintImmediately(0, 0, getWidth(), getHeight());

			// Delay for half a second
			delay(500);
			cycleCount++;
		}
	}

	/**
	 * The graphical component of the game
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// Draw the main menu if the user is not in game
		if (!inGame)
			g.drawImage(mainMenu, 0, 0, this);

		// If the user is in game
		else
		{
			// Draw the background
			g.drawImage(imageBackground, 0, 0, this);
			g.drawImage(boardBackground, 350, 54, this);

			// Draw the board with current pieces.
			for (int row = 1; row <= NO_OF_ROWS; row++)
				for (int column = 1; column <= NO_OF_COLUMNS; column++)
				{
					// Find the x and y positions for each row and column.
					int xPos = (column - 1) * SQUARE_SIZE + 350;
					int yPos = (row - 1) * SQUARE_SIZE + 54;

					// Draw the squares
					g.setColor(Color.BLACK);
					g.drawRect(xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);

					// Find the image of the piece
					int pieceNumber = board[row][column];

					if (pieceNumber == POP)
						g.drawImage(pop, xPos, yPos, this);
					else
						g.drawImage(penguins[pieceNumber], xPos, yPos, this);
				}

			// Draw the selected piece being DRAGGED.
			if (fromRow != -1 && board[fromRow][fromColumn] == EMPTY)
				g.drawImage(penguins[selectedPiece], draggedXPos, draggedYPos,
						this);

			// Draw the side panel
			g.drawRect(25, 54, 300, 640);
			g.drawImage(about, 25, 664, this);
			g.drawImage(penguinLogo, 85, 200, this);
			g.drawImage(menuButton, 100, 600, this);
			g.drawImage(title, 25, 54, this);
			g.drawImage(levelImage, 50, 385, this);
			g.drawString("" + level, 225, 423);
			g.drawImage(scoreImage, 50, 440, this);
			g.drawString("" + score, 50, 525);
			g.drawImage(hintButton, 100, 530, this);

			// Draw the thermometer
			g.drawImage(thermometer, 25, 670, this);
			g.setColor(Color.RED);
			g.fillRect(thermometerStart, 703, thermometerSize, 9);
			g.drawImage(thermometerLines, 25, 670, this);

			// Draw losing screen if user loses
			if (gameOver )
			{
				// If there is a new high score, draw the high score screen
				if (newHighScore )
					g.drawImage(loseHighScore, 262, 259, this);

				// Otherwise, draw the regular high score screen
				else
					g.drawImage(lose, 262, 259, this);

				// If the user has submitted their high score, draw the high
				// score submitted screen
				if (highScoreSubmit )
					g.drawImage(loseHighScoreSubmit, 262, 259, this);

				// If the user click view high score
				if (showHighScore )
				{
					g.drawImage(highScoreImage, 262, 259, this);
					g.setColor(Color.BLACK);
					g.setFont(new Font("Arial", Font.BOLD, 22));
					g.drawString(player, 296, 390);
					g.drawString("" + highScore, 600, 390);
					g.drawImage(backMenuButton, 294, 430, this);
				}

				if (!showHighScore)
				{
					g.drawImage(newGameButton, 282, 423, this);
					g.drawImage(viewHighScore, 500, 423, this);
				}
			}

			// Draw the no more moves text if there aren't any possible moves
			if (noMoreMoves )
				g.drawImage(noMoreMovesImage, 428, 319, this);

			// Draw the level up text if the user levels up
			if (levelUp )
				g.drawImage(levelUpImage, 428, 319, this);

			// Draw the game menu
			if (inGameMenuScreen )
			{
				g.drawImage(inGameMenu, 262, 134, this);
				g.drawImage(resumeButton, 396, 302, this);
				g.drawImage(newGameButton, 396, 398, this);
				g.drawImage(instructionsButton, 396, 494, this);
			}

		}

		// Draw the first instructions screen
		if (instructionsScreen == 1)
		{
			g.drawImage(instructionsOne, 262, 134, this);
			g.drawImage(backMenuButton, 280, 550, this);
			g.drawImage(nextButton, 620, 535, this);

		}

		// Draw the second instructions screen
		else if (instructionsScreen == 2)
		{
			g.drawImage(instructionsTwo, 262, 134, this);
			g.drawImage(backButton, 280, 535, this);
			g.drawImage(nextButton, 620, 535, this);
		}

		// Draw the third instructions screen
		else if (instructionsScreen == 3)
		{
			g.drawImage(instructionsThree, 262, 134, this);
			g.drawImage(backButton, 280, 535, this);
			g.drawImage(backMenuButton, 570, 560, this);
		}
	}

	/**
	 * Finds out which piece was selected
	 * 
	 * @param event information about the mouse pressed event
	 */

	public void mousePressed(MouseEvent event)
	{
		Point pressedPoint = event.getPoint();

		// If the user is not in a game
		if (!inGame)
		{
			if (instructionsScreen == 0)
			{
				// If the user hits play when the instructions are not open,
				// start a new game
				if (pressedPoint.x >= 119 && pressedPoint.x < 338
						&& pressedPoint.y >= 587 && pressedPoint.y < 691)
					newGame();

				// If the user hits instructions and they are not open, bring up
				// the instructions
				else if (pressedPoint.x >= 549 && pressedPoint.x < 792
						&& pressedPoint.y >= 501 && pressedPoint.y < 615)
					instructionsScreen = 1;
			}

			else if (instructionsScreen == 1)
			{
				// Respond if the instructions are open on the first page and
				// the user clicks back to menu
				if (pressedPoint.x >= 280 && pressedPoint.x < 461
						&& pressedPoint.y >= 550 && pressedPoint.y < 608)
					// Repaint the panel with the main menu screen showing
					instructionsScreen = 0;

				// Respond if the instructions are open on the first page and
				// the user clicks next
				else if (pressedPoint.x >= 620 && pressedPoint.x < 742
						&& pressedPoint.y >= 535 && pressedPoint.y < 628)
					// Repaint the panel with help screen #2 showing
					instructionsScreen++;
			}

			else if (instructionsScreen == 2)
			{
				// Respond if the instructions are open on the second page and
				// the user clicks back to menu
				if (pressedPoint.x >= 280 && pressedPoint.x < 402
						&& pressedPoint.y >= 535 && pressedPoint.y < 628)
					// Repaint the panel with help screen #1 showing
					instructionsScreen--;

				// Respond if the instructions are open on the second page and
				// the
				// user clicks next
				else if (pressedPoint.x >= 620 && pressedPoint.x < 742
						&& pressedPoint.y >= 535 && pressedPoint.y < 628)
					// Repaint the panel with help screen #3 showing
					instructionsScreen++;

			}

			else if (instructionsScreen == 3)
			{
				// Respond if the instructions are open on the third page and
				// the user clicks back
				if (pressedPoint.x >= 280 && pressedPoint.x < 402
						&& pressedPoint.y >= 535 && pressedPoint.y < 628)
					// Repaint the panel with help screen #2 showing
					instructionsScreen--;

				// Respond if the instructions are open on the third page and
				// the user clicks back to menu
				else if (pressedPoint.x >= 570 && pressedPoint.x < 751
						&& pressedPoint.y >= 560 && pressedPoint.y < 617)
					// Repaint the panel with in game menu screen showing
					instructionsScreen = 0;

			}
		}

		// If the user is in game
		else
		{

			// If the menu is not open
			if (!inGameMenuScreen)
			{
				// Respond if a mouse button was pressed over the Menu image
				if (pressedPoint.x >= 100 && pressedPoint.x < 250
						&& pressedPoint.y >= 600 && pressedPoint.y < 662)
				{
					// Repaint the panel with in game menu showing
					inGameMenuScreen = true;
					timer.stop();

				}

				// Respond if a mouse button was pressed over the hint button
				else if (pressedPoint.x >= 100 && pressedPoint.x < 250
						&& pressedPoint.y >= 530 && pressedPoint.y < 592)
				{
					giveHint();

					// Add to the thermometer
					thermometerScore += (7 / 890.0) * THERMOMETER_BASE_SCORE
							* level;
					thermometerStart -= 7;
					thermometerSize += 7;

					// When the thermometer is full
					if (thermometerScore >= THERMOMETER_BASE_SCORE * level)
					{
						// End the game
						gameOver = true;

						// Stop all the music
						backgroundMusic.stop();
						loseSound.play();

						// Check if there is a new high score
						try
						{
							checkHighScore();
						}
						catch (IOException e)
						{
						}

						// Stop the timer
						timer.stop();
					}

				}
			}

			// If the menu is open
			else if (inGameMenuScreen)
			{
				// Respond if the menu is open and the user clicks resume
				if (pressedPoint.x >= 396 && pressedPoint.x < 604
						&& pressedPoint.y >= 302 && pressedPoint.y < 368)
				{
					// Repaint the panel with game showing
					inGameMenuScreen = false;
					timer.start();
				}

				// Respond if the menu is open and the user clicks new game
				else if (pressedPoint.x >= 396 && pressedPoint.x < 604
						&& pressedPoint.y >= 398 && pressedPoint.y < 464)
				{
					// Repaint the panel with a new game showing
					inGameMenuScreen = false;
					newGame();

				}

				// Respond if the menu is open and the user clicks instructions
				// without it already being open
				else if (pressedPoint.x >= 396 && pressedPoint.x < 604
						&& pressedPoint.y >= 494 && pressedPoint.y < 560)
				{
					// Repaint the panel with help screen #1 showing
					inGameMenuScreen = false;
					instructionsScreen = 1;

				}
			}

			if (instructionsScreen == 1)
			{
				// Respond if the instructions are open on the first page and
				// the user clicks back to menu
				if (pressedPoint.x >= 280 && pressedPoint.x < 461
						&& pressedPoint.y >= 550 && pressedPoint.y < 608)
				{
					// Repaint the panel with the main menu screen showing
					instructionsScreen = 0;
					inGameMenuScreen = true;
				}
				// Respond if the instructions are open on the first page and
				// the user clicks next
				else if (pressedPoint.x >= 620 && pressedPoint.x < 742
						&& pressedPoint.y >= 535 && pressedPoint.y < 628)
					// Repaint the panel with help screen #2 showing
					instructionsScreen++;
			}

			else if (instructionsScreen == 2)
			{
				// Respond if the instructions are open on the second page and
				// the user clicks back to menu
				if (pressedPoint.x >= 280 && pressedPoint.x < 402
						&& pressedPoint.y >= 535 && pressedPoint.y < 628)
					// Repaint the panel with help screen #1 showing
					instructionsScreen--;

				// Respond if the instructions are open on the second page and
				// the
				// user clicks next
				else if (pressedPoint.x >= 620 && pressedPoint.x < 742
						&& pressedPoint.y >= 535 && pressedPoint.y < 628)
					// Repaint the panel with help screen #3 showing
					instructionsScreen++;

			}

			else if (instructionsScreen == 3)
			{
				// Respond if the instructions are open on the third page and
				// the user clicks back
				if (pressedPoint.x >= 280 && pressedPoint.x < 402
						&& pressedPoint.y >= 535 && pressedPoint.y < 628)
					// Repaint the panel with help screen #2 showing
					instructionsScreen--;

				// Respond if the instructions are open on the third page and
				// the user clicks back to menu
				else if (pressedPoint.x >= 570 && pressedPoint.x < 751
						&& pressedPoint.y >= 560 && pressedPoint.y < 617)
				{
					// Repaint the panel with the main menu screen showing
					instructionsScreen = 0;
					inGameMenuScreen = true;
				}
			}

			// Convert mouse-pressed location to board row and column
			if (!inGameMenuScreen && !gameOver
					&& instructionsScreen == 0 && pressedPoint.x >= 350
					&& pressedPoint.x < 990 && pressedPoint.y >= 54
					&& pressedPoint.y < 694)
			{
				// Track the row and column
				fromColumn = (pressedPoint.x - 350) / SQUARE_SIZE + 1;
				fromRow = (pressedPoint.y - 54) / SQUARE_SIZE + 1;

				// Update the dragged position to the current position
				draggedXPos = pressedPoint.x - SQUARE_SIZE / 2;
				draggedYPos = pressedPoint.y - SQUARE_SIZE / 2;

				// Set the piece to EMPTY
				selectedPiece = board[fromRow][fromColumn];
				board[fromRow][fromColumn] = EMPTY;

			}

			// Respond if the game is over
			if (gameOver)
			{
				// Respond if the user clicks new game
				if (!showHighScore && pressedPoint.x >= 282
						&& pressedPoint.x < 490 && pressedPoint.y >= 423
						&& pressedPoint.y < 489)
				{
					if (newHighScore )
						remove(nameBox);
					highScoreSubmit = false;
					newGame();
				}

				// Respond if the user clicks view high score
				else if (pressedPoint.x >= 500 && pressedPoint.x < 708
						&& pressedPoint.y >= 423 && pressedPoint.y < 489)
				{
					if (newHighScore )
						remove(nameBox);
					showHighScore = true;
				}
				else if (showHighScore  && pressedPoint.x >= 294
						&& pressedPoint.x < 475 && pressedPoint.y >= 430
						&& pressedPoint.y < 488)
				{
					if (newHighScore )
						add(nameBox);
					showHighScore = false;
				}

				// Respond if the user has a new high score and clicks the enter
				// button
				else if (newHighScore  && pressedPoint.x >= 698
						&& pressedPoint.x < 741 && pressedPoint.y >= 299
						&& pressedPoint.y < 335)
				{
					// Get the name of the player, then remove the box
					player = nameBox.getText().trim();
					remove(nameBox);

					highScore = score;

					// Record the high score in the text file
					try
					{
						recordHighScore();
					}
					catch (IOException e)
					{
					}

					// Change screens
					newHighScore = false;
					highScoreSubmit = true;
				}
			}
		}

		// Update the screen
		repaint();
	}

	/**
	 * Finds where the penguin is and move the penguin, if allowed
	 * 
	 * @param event information about the mouse released event
	 */
	public void mouseReleased(MouseEvent event)
	{
		if (fromRow != -1)
		{
			// Convert mouse-released location to board row and column
			Point releasedPoint = event.getPoint();

			// Convert mouse-released location to board row and column
			toColumn = (int) ((double) (releasedPoint.x - 350) / SQUARE_SIZE + 1);
			toRow = (int) ((double) (releasedPoint.y - 54) / SQUARE_SIZE + 1);
			makeMove();
		}

	}

	public void mouseClicked(MouseEvent event)
	{

	}

	public void mouseEntered(MouseEvent event)
	{

	}

	public void mouseExited(MouseEvent event)
	{

	}

	/**
	 * Moves the selected piece when the mouse is dragged
	 * 
	 * @param event information about the mouse dragged event
	 */
	public void mouseDragged(MouseEvent event)
	{
		// Find the position of the piece that is being dragged
		if (selectedPiece != EMPTY)
		{
			draggedXPos = event.getX() - SQUARE_SIZE / 2;
			draggedYPos = event.getY() - SQUARE_SIZE / 2;
			repaint();
		}
	}

	public void mouseMoved(MouseEvent event)
	{

	}

	/**
	 * Gets the URL needed for newAudioClip
	 * 
	 * @param fileName the name of the music file to play
	 * @return the complete file name
	 */
	public URL getCompleteURL(String fileName)
	{
		try
		{
			return new URL("file:" + System.getProperty("user.dir") + "/"
					+ fileName);
		}
		catch (MalformedURLException e)
		{
			System.err.println(e.getMessage());
		}
		return null;
	}

	/**
	 * Inner timer class for thermometer
	 * 
	 * @author Jessica Jiang & Kitty Su
	 * 
	 */
	public class TimerEventHandler implements ActionListener
	{

		public void actionPerformed(ActionEvent event)
		{

			// After two seconds, add to the thermometer
			if (time >= 2 && timerOn
					&& thermometerScore < THERMOMETER_BASE_SCORE * level)
			{
				// Add to the thermometer
				thermometerScore += (5 / 890.0) * THERMOMETER_BASE_SCORE
						* level;
				thermometerStart -= 5;
				thermometerSize += 5;

				// When the thermometer is full
				if (thermometerScore >= THERMOMETER_BASE_SCORE * level)
				{
					// End the game
					gameOver = true;

					// Stop all the music
					backgroundMusic.stop();
					loseSound.play();

					// Check if there is a new high score
					try
					{
						checkHighScore();
					}
					catch (IOException e)
					{
					}

					// Stop the timer
					timer.stop();
				}
				// Repaint the screen
				repaint();

			}
			else
			{
				// Increment the time
				time++;

				// Repaint the screen
				repaint();
			}

		}

	}

}
