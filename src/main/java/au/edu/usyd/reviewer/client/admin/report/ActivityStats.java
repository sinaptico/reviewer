package au.edu.usyd.reviewer.client.admin.report;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ActivityStats implements Serializable {

	private static final long serialVersionUID = 1L;

	private double avgDaysWriting;
	private double avgGroupContributors;
	private double avgGroupRevisions;
	private double avgRevisions;
	private double avgSessionsWriting;

	private double medianDaysWriting;
	private double medianGroupContributors;
	private double medianGroupRevisions;
	private double medianRevisions;
	private double medianSessionsWriting;

	private double stdevDaysWriting;
	private double stdevGroupContributors;
	private double stdevGroupRevisions;
	private double stdevRevisions;
	private double stdevSessionsWriting;

	public ActivityStats(Collection<UserStats> userStats) {
		calculateStats(userStats);
	}

	private double calculateMean(List<Integer> values) {
		double mean = 0;
		for (int value : values) {
			mean += value;
		}
		mean = mean / values.size();
		return mean;
	}

	private double calculateMedian(List<Integer> values) {
		int n = values.size();
		double median = values.get(n/2-1);
		if (values.size() % 2 == 0) {
			median = (median + values.get(n/2)) / 2;
		}
		return median;
	}

	private void calculateStats(Collection<UserStats> stats) {
		List<Integer> daysWriting = new LinkedList<Integer>();
		List<Integer> groupContributors = new LinkedList<Integer>();
		List<Integer> groupRevisions = new LinkedList<Integer>();
		List<Integer> revisions = new LinkedList<Integer>();
		List<Integer> sessionsWriting = new LinkedList<Integer>();

		for (UserStats userStats : stats) {
			daysWriting.add(userStats.getDaysWriting());
			groupContributors.add(userStats.getGroupContributors());
			groupRevisions.add(userStats.getGroupRevisions());
			revisions.add(userStats.getRevisions());
			sessionsWriting.add(userStats.getSessionsWriting());
		}

		Collections.sort(daysWriting);
		Collections.sort(groupContributors);
		Collections.sort(groupRevisions);
		Collections.sort(revisions);
		Collections.sort(sessionsWriting);

		medianDaysWriting = calculateMedian(daysWriting);
		medianGroupContributors = calculateMedian(groupContributors);
		medianGroupRevisions = calculateMedian(groupRevisions);
		medianRevisions = calculateMedian(revisions);
		medianSessionsWriting = calculateMedian(sessionsWriting);

		avgDaysWriting = calculateMean(daysWriting);
		avgGroupContributors = calculateMean(groupContributors);
		avgGroupRevisions = calculateMean(groupRevisions);
		avgRevisions = calculateMean(revisions);
		avgSessionsWriting = calculateMean(sessionsWriting);

		stdevDaysWriting = calculateStdev(daysWriting, avgDaysWriting);
		stdevGroupContributors = calculateStdev(groupContributors, avgGroupContributors);
		stdevGroupRevisions = calculateStdev(groupRevisions, avgGroupRevisions);
		stdevRevisions = calculateStdev(revisions, avgRevisions);
		stdevSessionsWriting = calculateStdev(sessionsWriting, avgSessionsWriting);
	}

	private double calculateStdev(List<Integer> values, double mean) {
		double stdev = 0;
		for (int value : values) {
			stdev += Math.pow(value - mean, 2);
		}
		stdev = Math.sqrt(stdev / (values.size() - 1));
		return stdev;
	}

	public double getAvgDaysWriting() {
		return avgDaysWriting;
	}

	public double getAvgGroupContributors() {
		return avgGroupContributors;
	}

	public double getAvgGroupRevisions() {
		return avgGroupRevisions;
	}

	public double getAvgRevisions() {
		return avgRevisions;
	}

	public double getAvgSessionsWriting() {
		return avgSessionsWriting;
	}

	public double getMedianDaysWriting() {
		return medianDaysWriting;
	}

	public double getMedianGroupContributors() {
		return medianGroupContributors;
	}

	public double getMedianGroupRevisions() {
		return medianGroupRevisions;
	}

	public double getMedianRevisions() {
		return medianRevisions;
	}

	public double getMedianSessionsWriting() {
		return medianSessionsWriting;
	}

	public double getStdevDaysWriting() {
		return stdevDaysWriting;
	}

	public double getStdevGroupContributors() {
		return stdevGroupContributors;
	}

	public double getStdevGroupRevisions() {
		return stdevGroupRevisions;
	}

	public double getStdevRevisions() {
		return stdevRevisions;
	}

	public double getStdevSessionsWriting() {
		return stdevSessionsWriting;
	}

	public void setAvgDaysWriting(double avgDaysWriting) {
		this.avgDaysWriting = avgDaysWriting;
	}

	public void setAvgGroupContributors(double avgGroupContributors) {
		this.avgGroupContributors = avgGroupContributors;
	}

	public void setAvgGroupRevisions(double avgGroupRevisions) {
		this.avgGroupRevisions = avgGroupRevisions;
	}

	public void setAvgRevisions(double avgRevisions) {
		this.avgRevisions = avgRevisions;
	}

	public void setAvgSessionsWriting(double avgSessionsWriting) {
		this.avgSessionsWriting = avgSessionsWriting;
	}

	public void setMedianDaysWriting(double medianDaysWriting) {
		this.medianDaysWriting = medianDaysWriting;
	}

	public void setMedianGroupContributors(double medianGroupContributors) {
		this.medianGroupContributors = medianGroupContributors;
	}

	public void setMedianGroupRevisions(double medianGroupRevisions) {
		this.medianGroupRevisions = medianGroupRevisions;
	}

	public void setMedianRevisions(double medianRevisions) {
		this.medianRevisions = medianRevisions;
	}

	public void setMedianSessionsWriting(double medianSessionsWriting) {
		this.medianSessionsWriting = medianSessionsWriting;
	}

	public void setStdevDaysWriting(double stdevDaysWriting) {
		this.stdevDaysWriting = stdevDaysWriting;
	}

	public void setStdevGroupContributors(double stdevGroupContributors) {
		this.stdevGroupContributors = stdevGroupContributors;
	}

	public void setStdevGroupRevisions(double stdevGroupRevisions) {
		this.stdevGroupRevisions = stdevGroupRevisions;
	}

	public void setStdevRevisions(double stdevRevisions) {
		this.stdevRevisions = stdevRevisions;
	}

	public void setStdevSessionsWriting(double stdevSessionsWriting) {
		this.stdevSessionsWriting = stdevSessionsWriting;
	}
	
	
}
