package at.jku.cp.ai.rau;

import at.jku.cp.ai.rau.endconditions.EndCondition;
import at.jku.cp.ai.rau.objects.*;

import java.util.List;

/**
 * This interface defines what a board must be able to do at the minimum
 */
public interface IBoard {
	/**
	 * This method deep-copies all the <b>active</b> gameobjects:
	 * * EndCondition
	 * * Fountains
	 * * Clouds
	 * * Unicorns
	 * * Seeds
	 * * Rainbows
	 * <p>
	 * And copies only references to the <b>passive</b> ones:
	 * - Walls
	 * - Paths
	 *
	 * @return a partial deep-copy of the board
	 */
	IBoard copy();

	/**
	 * This method deep-copies the complete object
	 *
	 * @return a deep-copy of the board
	 */
	IBoard deepCopy();

	/**
	 * This method gives us a List<Move> of possible moves for the
	 * current unicorn / player.
	 *
	 * @return a list of possible moves
	 */
	List<Move> getPossibleMoves();

	/**
	 * This is the only method to change the state of the board, according to the
	 * move specified.
	 *
	 * @param move
	 * @return true if the move was executed, false if it couldn't be executed
	 */
	boolean executeMove(Move move);

	/**
	 * Return all the gameobjects at a specified position.
	 *
	 * @param pos the position on the board
	 * @return a list of gameobjects
	 */
	List<GameObject> at(V pos);

	/**
	 * Checks a position on the board, if it stops rainbows from propagating.
	 *
	 * @param pos the position on the board to check
	 * @return true if the position contains any GameObject that stops Rainbows
	 */
	boolean isStoppingRainbow(V pos);

	/**
	 * Checks a position on the board, if it is removable
	 *
	 * @param pos the position on the board
	 * @return true if all gameobjects at the position are removable
	 */
	boolean isRemovable(V pos);

	/**
	 * Checks a position on the board, if one or more rainbows are on it
	 *
	 * @param pos the position on the board
	 * @return true if there is a rainbow at this position
	 */
	boolean isRainbowAt(V pos);

	/**
	 * Checks a position on the board, if it is walkable.
	 *
	 * @param pos the position on the board
	 * @return true if all gameobjects at this position can be walked upon
	 */
	boolean isPassable(V pos);

	/**
	 * Renders a board state as text. Please note that the 'rendering' can be
	 * non-invertible, due to overlapping gameobjects.
	 *
	 * @return a text-representation of the boardstate.
	 */
	String toString();

	/**
	 * Get a representation of the boardstate as a 2D character array.
	 *
	 * @return a 2D array of chars
	 */
	char[][] getTextBoard();

	/**
	 * Returns the unicorn who may make a move during this tick.
	 *
	 * @return the unicorn
	 */
	Unicorn getCurrentUnicorn();

	int hashCode();

	boolean equals(Object obj);

	int getTick();

	int getWidth();

	int getHeight();

	boolean isRunning();

	EndCondition getEndCondition();

	void setEndCondition(EndCondition endCondition);

	List<Wall> getWalls();

	List<Path> getPaths();

	List<Fountain> getFountains();

	List<Cloud> getClouds();

	List<Unicorn> getUnicorns();

	List<Seed> getSeeds();

	List<Rainbow> getRainbows();

	List<List<? extends GameObject>> getAllObjects();


}