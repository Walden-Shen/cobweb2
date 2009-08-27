/*
 * $$$$$: Comments by Liang $$$$$$: Codes modified and/or added by Liang
 */

package cwcore;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cobweb.Environment.Location;

public class ComplexAgentInfo {

	public static final int MAX_PATH_HISTORY = 32;

	/** The gene status of the agent. Default is 1. */

	/* Formatting variables. The WIDTH of certain columns */
	final static int W_TICK = 6;

	final static int W_ENERGY = 8;

	final static int W_MOVE = 6;

	final static int W_AGENT = 6;

	final static int W_ROCK = 6;

	final static int W_OTHER_GAIN = 6;

	final static int W_OTHER_LOSS = 6;

	final static int W_CANN = 6;

	final static int W_TURNING = 6;

	final static int W_SPACE = 2;

	final static int W_POP = 6;

	final static int W_FEED = 6;

	final static int W_AGE = 6;

	private static boolean headerPrinted = false;

	private static PrintWriter group_out;

	private static boolean groupDataStreamInit = false;

	static int agentTypes = 0; // total agent types

	static int[] deadAgentsofType; // numbers of agents dead of type i.

	// This is initialized in printAgent method.
	static int[] aliveAgentsofType;

	static int[] sexAgentsofType;

	static int[] asexAgentsofType;

	static int[] offspringsAgentsofType;

	static int[] stepsAgentsofType;

	static int[] totalsAgentsofType;

	static int[] stepslivedAgentsofType;

	static int[] eatenAgentsofType;

	/* Dumps the group data */
	public static void dumpGroupData(long tick, java.io.Writer www) {
		if (!groupDataStreamInit) {
			try {
				group_out = new java.io.PrintWriter(www, false);
				groupDataStreamInit = true;
			} catch (Exception ex) {
				Logger.getLogger("COBWEB2").log(Level.WARNING, "exception", ex);
			}
		} else {
			try {
				if (!headerPrinted) {
					group_out.print(paddMe("", W_TICK));
					// TITLE LINE 1
					for (int i = 0; i < agentTypes; i++) {
						group_out.print("||");
						group_out.print(paddMe("Type " + (i + 1), W_POP));
						group_out.print(paddMe("gained", W_FEED));
						group_out.print(paddMe("gained", W_CANN));
						group_out.print(paddMe("gained", W_OTHER_GAIN));
						group_out.print(paddMe("energy", W_ENERGY));
						group_out.print(paddMe("usage", W_MOVE));
						group_out.print(paddMe("usage", W_AGENT));
						group_out.print(paddMe("usage", W_ROCK));
						group_out.print(paddMe("usage", W_TURNING));
						group_out.print(paddMe("usage", W_OTHER_LOSS));
						group_out.print(paddMe("usage", W_AGE));
					}
					group_out.println();
					// TITLE LINE 2
					group_out.print(paddMe("TICK", W_TICK));
					for (int i = 0; i < agentTypes; i++) {
						group_out.print("||");
						group_out.print(paddMe("pop.", W_POP));
						group_out.print(paddMe("food", W_FEED));
						group_out.print(paddMe("cann", W_CANN));
						group_out.print(paddMe("others", W_OTHER_GAIN));
						group_out.print(paddMe("total", W_ENERGY));
						group_out.print(paddMe("moving", W_MOVE));
						group_out.print(paddMe("agents", W_AGENT));
						group_out.print(paddMe("rocks", W_ROCK));
						group_out.print(paddMe("turn", W_TURNING));
						group_out.print(paddMe("others", W_OTHER_LOSS));
						group_out.print(paddMe("age", W_AGE));
					}
					group_out.println();
					headerPrinted = true;
				} // !headerPrinted
				group_out.print(paddMe(tick, W_TICK));

				// DATA
				for (int i = 0; i < agentTypes; i++) {
					group_out.print("||");
					group_out.print(paddMe(liveCount[i], W_POP));
					group_out.print(paddMe(foodEnergies[i], W_FEED));
					group_out.print(paddMe(cannibalEnergies[i], W_CANN));
					group_out.print(paddMe(otherEnergySources[i], W_OTHER_GAIN));
					group_out.print(paddMe(energies[i], W_ENERGY));
					group_out.print(paddMe(stepEnergies[i], W_MOVE));
					group_out.print(paddMe(agentBumpEnergies[i], W_AGENT));
					group_out.print(paddMe(rockBumpEnergies[i], W_ROCK));
					group_out.print(paddMe(turningEnergies[i], W_TURNING));
					group_out.print(paddMe(otherEnergySinks[i], W_OTHER_LOSS));
					group_out.print(paddMe(agentAgingEnergies[i], W_AGE));
				}
				group_out.println();
				group_out.flush();
			} catch (Exception ex) {
				Logger.getLogger("COBWEB2").log(Level.WARNING, "exception", ex);
			}
		}
	}

	/**
	 * This method should be called prior to calling the printInfo method. This method will initializes the static
	 * array. Should be called once not in a loop.
	 */

	public static void initStaticAgentInfo(int agenttypes) {
		if (alreadyInitialized) {
			System.out.println("ComplexAgentInfo::initStaticAgentInfo(" + agenttypes + ")"
					+ " ComplexAgentInfo::alreadyInitialized = " + alreadyInitialized);
			return;
		}
		System.out.println("ComplexAgentInfo::initStaticAgentInfo(" + agenttypes + ")"
				+ " ComplexAgentInfo::alreadyInitialized = " + alreadyInitialized);
		alreadyInitialized = true;

		agentTypes = agenttypes;
		deadAgentsofType = new int[agentTypes];
		aliveAgentsofType = new int[agentTypes];
		sexAgentsofType = new int[agentTypes];
		asexAgentsofType = new int[agentTypes];
		offspringsAgentsofType = new int[agentTypes];
		stepsAgentsofType = new int[agentTypes];
		totalsAgentsofType = new int[agentTypes];
		stepslivedAgentsofType = new int[agentTypes];
		eatenAgentsofType = new int[agentTypes];
	}

	/* emulating C's printf() fcn */
	private static String paddMe(int i, int pad) {
		String tmp = i + "";
		return paddMe(tmp, pad);
	}

	private static String paddMe(long i, int pad) {
		String tmp = i + "";
		return paddMe(tmp, pad);
	}

	private static String paddMe(String s, int pad) {
		return paddMe(s, pad, ' ');
	}

	/* Allows custom padding. */
	private static String paddMe(String s, int pad, char cpad) {
		String ret = "";
		for (; pad >= s.length(); pad--) {
			ret += cpad;
		}
		ret += s;
		return ret;
	}

	/******* Defunct */
	public static void printAgentsCount(java.io.PrintWriter pw) {
		for (int i = 0; i < agentTypes; i++) {
			pw.print((i + 1)); // $$$$$$ change from "i" to "(i + 1)". Apr 3
			pw.print("\t" + deadAgentsofType[i]);
			pw.print("\t" + aliveAgentsofType[i]);
			pw.print("\t" + offspringsAgentsofType[i]);
			pw.print("\t" + sexAgentsofType[i]);
			pw.print("\t" + asexAgentsofType[i]);
			pw.println("\t" + stepsAgentsofType[i]);
		}
	}

	/** Prints the species-wise statistics of an agent type. Intended to replace printAgentsCount() */
	public static void printAgentsCountByType(java.io.PrintWriter pw, int type) {
		pw.print((type + 1)); // $$$$$$ change from "i" to "(i + 1)". Apr 3
		pw.print("\t" + deadAgentsofType[type]);
		pw.print("\t" + aliveAgentsofType[type]);
		pw.print("\t" + offspringsAgentsofType[type]);
		pw.print("\t" + sexAgentsofType[type]);
		pw.print("\t" + asexAgentsofType[type]);
		pw.print("\t" + stepsAgentsofType[type]);
	}

	public static void resetGroupData() {
		for (int i = 0; i < typesCount; i++) {
			energies[i] = 0;
			foodEnergies[i] = 0;
			stepEnergies[i] = 0;
			agentBumpEnergies[i] = 0;
			rockBumpEnergies[i] = 0;
			liveCount[i] = 0;
			otherEnergySources[i] = 0;
			cannibalEnergies[i] = 0;
			turningEnergies[i] = 0;
			otherEnergySinks[i] = 0;
			agentAgingEnergies[i] = 0;
		}
	}

	private int action = 0;

	private long parent1;

	private long parent2;

	private long birthTick;

	private long deathTick = -1;

	private int countSteps;

	private int countTurns;

	private List<Location> path = new LinkedList<Location>();

	private int countAgentBumps;

	private int countRockBumps;

	// The static variables to keep track of the agents produced so far.

	private int agentNumber;

	private int type;

	private int sexualPregs;

	private int directChildren;

	private int totalChildren;

	private static boolean alreadyInitialized = false;

	private static int typesCount = 4;

	private static int[] energies;

	private static int[] stepEnergies;

	private static int[] agentBumpEnergies;

	private static int[] rockBumpEnergies;

	private static int[] liveCount;

	private static int[] foodEnergies;

	private static int[] otherEnergySources;

	private static int[] cannibalEnergies;

	private static int[] turningEnergies;

	private static int[] otherEnergySinks;

	private static int[] agentAgingEnergies;

	public static void initialize(int agentCount) {
		typesCount = agentCount;

		energies = new int[typesCount];

		stepEnergies = new int[typesCount];

		agentBumpEnergies = new int[typesCount];

		rockBumpEnergies = new int[typesCount];

		liveCount = new int[typesCount];

		foodEnergies = new int[typesCount];

		otherEnergySources = new int[typesCount];

		cannibalEnergies = new int[typesCount];

		turningEnergies = new int[typesCount];

		otherEnergySinks = new int[typesCount];

		agentAgingEnergies = new int[typesCount];
	}

	public ComplexAgentInfo(int num, int type, long birth, ComplexAgentInfo p1, ComplexAgentInfo p2, int strat) {
		this.type = type;
		agentNumber = num;
		birthTick = birth;
		parent1 = p1 != null ? p1.agentNumber : -1;
		parent2 = p2 != null ? p2.agentNumber : -1;
		action = strat;
	}

	public ComplexAgentInfo(int num, int type, long birth, ComplexAgentInfo p1, int strat) {
		this.type = type;
		agentNumber = num;
		birthTick = birth;
		parent1 = p1 != null ? p1.agentNumber : -1;
		action = strat;
	}

	public ComplexAgentInfo(int num, int type, long birth, int strat) {
		this.type = type;
		agentNumber = num;
		birthTick = birth;
		action = strat;
	}

	public void addAgentBump() {
		++countAgentBumps;
	}

	public void addCannibalism(int val) {
		cannibalEnergies[type] += val;
	}

	public void addDirectChild() {
		++directChildren;
	}

	/* Total energy per agent type */
	public void addEnergy(int val) {
		energies[type] += val;
	}

	public void addFoodEnergy(int val) {
		foodEnergies[type] += val;
	}

	/** Other sources including energy gained from the agent strategy */
	public void addOthers(int val) {
		otherEnergySources[type] += val;
	}

	public void addPathStep(Location loc) {
		path.add(loc);
		if (path.size() > MAX_PATH_HISTORY) {
			path.remove(0);
		}
	}

	public void addRockBump() {
		++countRockBumps;
	}

	public void addSexPreg() {
		++sexualPregs;
	}

	public void addStep() {
		++countSteps;
	}

	public void addTurn() {
		++countTurns;
	}

	/* Keep a total tally of the living agents */
	public void alive() {
		liveCount[type]++;
	}

	public void ate(int agentType) {
		eatenAgentsofType[agentType]++;
	}

	public int getAgentNumber() {
		return agentNumber;
	}

	public int getAgentType() {
		return type;
	}

	public long getDeathTick() {
		return deathTick;
	}

	public List<Location> getPathHistory() {
		return path;
	}

	public int getTypeNum() {
		return agentTypes;

	}

	public void printInfo(java.io.PrintWriter pw) {
		pw.print(agentNumber);
		pw.print("\t" + (type + 1)); // $$$$$$ change from "agentType" to "(agentType + 1)". Apr 3
		pw.print("\t" + birthTick);
		if (parent1 == -1 && parent2 == -1) {
			asexAgentsofType[type]++;
			pw.print("\tRandomly generated");
		} else if (parent2 == -1) {
			asexAgentsofType[type]++;
			offspringsAgentsofType[type]++;
			pw.print("\tAsexual, from agent " + parent1);
		} else {
			pw.print("\tSexual, from Mother: " + parent1 + ", Father: " + parent2);
			sexAgentsofType[type]++;
			offspringsAgentsofType[type]++;
			offspringsAgentsofType[type]++;
		}
		pw.print("\t" + deathTick);
		if (deathTick != -1) {
			stepslivedAgentsofType[type] += (deathTick - birthTick);
			deadAgentsofType[type]++;
		} else {
			aliveAgentsofType[type]++;
		}

		pw.print("\t" + directChildren);
		pw.print("\t" + totalChildren);
		pw.print("\t" + sexualPregs);

		int total = countSteps + countTurns + countAgentBumps + countRockBumps;
		totalsAgentsofType[type] += total;
		stepsAgentsofType[type] += countSteps;
		DecimalFormat dform = new DecimalFormat("###.##%");
		pw.print("\t" + dform.format((double) (countSteps) / total));
		pw.print("\t" + dform.format((double) (countTurns) / total));
		pw.print("\t" + dform.format((double) (countAgentBumps) / total));
		pw.print("\t" + dform.format((double) (countRockBumps) / total));

		if (action == 0) {
			pw.println("\tcooperator");
		} else {
			pw.println("\tcheater");
		}
	}

	public void setDeath(long death) {
		deathTick = death;
		path = null;
	}

	public void setStrategy(int strat) {
		action = strat;
	}

	public void useAgentBumpEnergy(int val) {
		agentBumpEnergies[type] += val;
	}

	public void useExtraEnergy(int val) {
		agentAgingEnergies[type] += val;
	}

	/* Other sources including energy gained from the agent strategy */
	public void useOthers(int val) {
		otherEnergySinks[type] += val;
	}

	public void useRockBumpEnergy(int val) {
		rockBumpEnergies[type] += val;
	}

	public void useStepEnergy(int val) {
		stepEnergies[type] += val;
	}

	public void useTurning(int val) {
		turningEnergies[type] += val;
	}

}
