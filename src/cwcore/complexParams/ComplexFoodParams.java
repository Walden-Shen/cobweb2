package cwcore.complexParams;

import cobweb.params.AbstractReflectionParams;
import cobweb.params.ConfDisplayName;
import cobweb.params.ConfXMLTag;

/**
 * Parameters for food in the ComplexEnvironment.
 */
public class ComplexFoodParams extends AbstractReflectionParams {
	/**
	 *
	 */
	private static final long serialVersionUID = 4935757387466603476L;

	/**
	 * Food type index.
	 */
	@ConfXMLTag("Index")
	public int type = -1;

	/**
	 * Amount of food dropped on the grid initially
	 */
	@ConfDisplayName("Initial amount")
	@ConfXMLTag("Food")
	public int initial = 20;

	/**
	 * How many squares of this food to randomly drop on the grid at each time step.
	 * Note, 1.5 drop rate means drop 1 square and have 50% chance of dropping a second.
	 */
	@ConfDisplayName("Spawn rate")
	@ConfXMLTag("FoodRate")
	public float dropRate = 0;

	/**
	 * Rate at which food grows around existing food.
	 * The chance food will grow at a specific cell, given there are N cells 
	 * with food touching this cell from top/bottom/left/right is: 
	 * N * growRate / 100
	 */
	@ConfDisplayName("Growth rate")
	@ConfXMLTag("FoodGrow")
	public int growRate = 4;

	/**
	 * Fraction of the food that will disappear when the deplete time comes.
	 */
	@ConfDisplayName("Depletion rate")
	@ConfXMLTag("FoodDeplete")
	public float depleteRate = 0.9f;

	/**
	 * Food will deplete every depleteTime time steps.
	 */
	@ConfDisplayName("Depletion time")
	@ConfXMLTag("DepleteTimeSteps")
	public int depleteTime = 40;

	/**
	 * How many time steps to wait between dropping new food.
	 */
	@ConfDisplayName("Draught period")
	@ConfXMLTag("DraughtPeriod")
	public int draughtPeriod = 0;

	public ComplexFoodParams() {
		// public, no parameter constructor for serialization
	}
}