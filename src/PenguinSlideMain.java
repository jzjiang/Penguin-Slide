import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.io.IOException;

public class PenguinSlideMain extends JFrame
{
	// Program variables the game board
	private PenguinSlideBoard gameBoard;

	public PenguinSlideMain() throws IOException
	{
		// Sets up the frame for the game
		super("Penguin Slide");
		setResizable(false);

		// Load up the icon image (penguin image from www.iconshock.com)
		setIconImage(Toolkit.getDefaultToolkit().getImage("images\\penguin1.png"));

		// Sets up the Connect Four board that plays most of the game
		// and add it to the centre of this frame
		gameBoard = new PenguinSlideBoard();
		getContentPane().add(gameBoard, BorderLayout.CENTER);

		// Centre the frame in the middle of the screen
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screen.width - gameBoard.BOARD_SIZE.width) / 2,
				(screen.height - gameBoard.BOARD_SIZE.height) / 2);

	}

	/**
	 * Starts up the PenguinSlideMain frame
	 * 
	 * @param args An array of Strings (ignored)
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		// Starts up the PenguinSlide frame
		PenguinSlideMain frame = new PenguinSlideMain();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	} // main method

}
