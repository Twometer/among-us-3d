package de.twometer.amongus.model;

public class SessionConfig {

    // Base variables for converting the relative values to world-values
    public static float PLAYER_VISION_BASE_RADIUS = 6.0f;
    public static float PLAYER_VISION_BASE_SPEED = 0.1f;

    private int impostorCount = 1;
    private float playerVision = 0.8f;
    private float impostorVision = 1.2f;
    private float playerSpeed = 1.0f;
    private int votingTime = 60;
    private boolean confirmEjects = true;
    private int emergencyMeetings = 1;
    private int commonTasks = 1;
    private int shortTasks = 2;
    private int longTasks = 2;
    private int killCooldown = 25;

    public int getImpostorCount() {
        return impostorCount;
    }

    public void setImpostorCount(int impostorCount) {
        this.impostorCount = impostorCount;
    }

    public float getPlayerVision() {
        return playerVision;
    }

    public void setPlayerVision(float playerVision) {
        this.playerVision = playerVision;
    }

    public float getImpostorVision() {
        return impostorVision;
    }

    public void setImpostorVision(float impostorVision) {
        this.impostorVision = impostorVision;
    }

    public float getPlayerSpeed() {
        return playerSpeed;
    }

    public void setPlayerSpeed(float playerSpeed) {
        this.playerSpeed = playerSpeed;
    }

    public int getVotingTime() {
        return votingTime;
    }

    public void setVotingTime(int votingTime) {
        this.votingTime = votingTime;
    }

    public boolean isConfirmEjects() {
        return confirmEjects;
    }

    public void setConfirmEjects(boolean confirmEjects) {
        this.confirmEjects = confirmEjects;
    }

    public int getEmergencyMeetings() {
        return emergencyMeetings;
    }

    public void setEmergencyMeetings(int emergencyMeetings) {
        this.emergencyMeetings = emergencyMeetings;
    }

    public int getCommonTasks() {
        return commonTasks;
    }

    public void setCommonTasks(int commonTasks) {
        this.commonTasks = commonTasks;
    }

    public int getShortTasks() {
        return shortTasks;
    }

    public void setShortTasks(int shortTasks) {
        this.shortTasks = shortTasks;
    }

    public int getLongTasks() {
        return longTasks;
    }

    public void setLongTasks(int longTasks) {
        this.longTasks = longTasks;
    }

    public int getKillCooldown() {
        return killCooldown;
    }

    public void setKillCooldown(int killCooldown) {
        this.killCooldown = killCooldown;
    }
}
