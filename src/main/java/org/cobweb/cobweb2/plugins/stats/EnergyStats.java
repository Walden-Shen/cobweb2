package org.cobweb.cobweb2.plugins.stats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.cobweb.cobweb2.core.Agent;
import org.cobweb.cobweb2.core.Cause;
import org.cobweb.cobweb2.core.LocationDirection;
import org.cobweb.cobweb2.core.Topology;
import org.cobweb.cobweb2.plugins.EnergyMutator;
import org.cobweb.cobweb2.plugins.EnvironmentMutator;
import org.cobweb.cobweb2.plugins.stats.CauseTree.CauseTreeNode;


public class EnergyStats implements EnergyMutator, EnvironmentMutator {

	public float[][] map;
	public float max;
	public float min;
	private Topology topo;

	public static class CauseStats {
		public CauseStats(CauseTreeNode node) {
			this.node = node;
		}
		public int count = 0;
		public double totalDelta = 0;
		public CauseTreeNode node;

		@Override
		public String toString() {
			return node.getName() + " count: " + count + " total: " + totalDelta;
		}

		public void reset() {
			count = 0;
			totalDelta = 0;
		}
	}

	public Map<Class<? extends Cause>, CauseStats> causeStats = new HashMap<>();

	public CauseTree causeTree = new CauseTree();

	public EnergyStats(Topology topo) {
		this.topo = topo;

		Iterator<CauseTreeNode> iterator = causeTree.iterator();
		while (iterator.hasNext()) {
			CauseTreeNode node = iterator.next();
			CauseStats stats = new CauseStats(node);
			causeStats.put(node.type, stats);
		}
	}

	@Override
	public void onEnergyChange(Agent agent, int delta, Cause cause) {
		LocationDirection loc = agent.getPosition();
		if (loc == null)
			return;

		Class<? extends Cause> causeClass = cause.getClass();

		if (!isWatching(causeClass))
			return;

		updateStats(delta, causeClass);

		float newValue = map[loc.x][loc.y] + delta;
		map[loc.x][loc.y] = newValue;

		if (newValue > max)
			max = newValue;
		if (newValue < min)
			min = newValue;
	}

	private void updateStats(int delta, Class<? extends Cause> causeClass) {
		CauseStats stats = causeStats.get(causeClass);
		do {
			stats.count++;
			stats.totalDelta += delta;
			stats = causeStats.get(stats.node.parent.type);
		} while (stats != null);
	}

	public void resetStats() {
		for (CauseStats v : causeStats.values())
			v.reset();
	}

	@Override
	public void update() {
		map = new float[topo.width][topo.height];
		max = 0;
		min = 0;
	}

	@Override
	public void loadNew() {
		update();
	}

	public void whitelist(Class<? extends Cause> type) {
		blackList.remove(type);
		whiteList.add(type);
	}

	public void blacklist(Class<? extends Cause> type) {
		whiteList.remove(type);
		blackList.add(type);
	}

	public void unlist(Class<? extends Cause> type) {
		whiteList.remove(type);
		blackList.remove(type);
	}

	public Set<Class<? extends Cause>> whiteList = new HashSet<>();

	public Set<Class<? extends Cause>> blackList = new HashSet<>();

	public boolean isWatching(Class<? extends Cause> type) {
		if (!whiteList.isEmpty()) {
			boolean found = false;
			for (Class<? extends Cause> c : whiteList)
				if (c.isAssignableFrom(type)) {
					found = true;
					break;
				}
			if (!found)
				return false;
		}

		for (Class<? extends Cause> c : blackList) {
			if (c.isAssignableFrom(type))
				return false;
		}
		return true;
	}

}
